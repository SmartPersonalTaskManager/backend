package com.sptm.backend.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.sptm.backend.model.CalendarEvent;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.CalendarEventRepository;
import com.sptm.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Service
public class CalendarService {

    private static final String APPLICATION_NAME = "SPTM Backend";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Value("${sptm.google.client-id:}")
    private String clientId;

    @Value("${sptm.google.client-secret:}")
    private String clientSecret;

    @Value("${sptm.google.redirect-uri:http://localhost:5173}") // Default to frontend
    private String redirectUri;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    @Autowired
    private NetHttpTransport httpTransport;

    private GoogleAuthorizationCodeFlow flow;

    @PostConstruct
    public void init() throws IOException {
        // Initialize Flow if credentials are provided
        if (clientId != null && !clientId.isEmpty() && clientSecret != null && !clientSecret.isEmpty()) {
            GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
            details.setClientId(clientId);
            details.setClientSecret(clientSecret);
            GoogleClientSecrets secrets = new GoogleClientSecrets();
            secrets.setWeb(details);

            flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, secrets, SCOPES)
                    .setAccessType("offline")
                    .build();
        }
    }

    public String getAuthorizationUrl() {
        if (flow == null) {
            throw new RuntimeException("Google Credentials (Client ID/Secret) are missing in backend configuration.");
        }
        return flow.newAuthorizationUrl().setRedirectUri(redirectUri).setAccessType("offline").build();
    }

    public void syncEvents(Long userId, String code) throws IOException {
        if (flow == null) {
            throw new RuntimeException("Google Credentials are not configured.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Exchange code for tokens
        TokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        String refreshToken = response.getRefreshToken();
        if (refreshToken != null) {
            user.setGoogleRefreshToken(refreshToken);
            userRepository.save(user);
        } else if (user.getGoogleRefreshToken() == null) {
            // If we didn't get a refresh token and don't have one, we might need to prompt
            // user to revoke access to get a new one
            // or use "prompt=consent" in auth url.
        }

        Credential credential = flow.createAndStoreCredential(response, userId.toString());
        fetchAndSaveEvents(user, credential);
    }

    private void fetchAndSaveEvents(User user, Credential credential) throws IOException {
        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(50)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            for (Event event : items) {
                String eventId = event.getId();
                CalendarEvent calendarEvent = calendarEventRepository
                        .findByUserIdAndGoogleEventId(user.getId(), eventId)
                        .orElse(new CalendarEvent());

                calendarEvent.setUser(user);
                calendarEvent.setGoogleEventId(eventId);
                calendarEvent.setSummary(event.getSummary());
                calendarEvent.setDescription(event.getDescription());

                if (event.getStart() != null && event.getStart().getDateTime() != null) {
                    calendarEvent.setStartTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(event.getStart().getDateTime().getValue()),
                            ZoneId.systemDefault()));
                }
                if (event.getEnd() != null && event.getEnd().getDateTime() != null) {
                    calendarEvent.setEndTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(event.getEnd().getDateTime().getValue()),
                            ZoneId.systemDefault()));
                }

                calendarEventRepository.save(calendarEvent);
            }
        }
    }
}
