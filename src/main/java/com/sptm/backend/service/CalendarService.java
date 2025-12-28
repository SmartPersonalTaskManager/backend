package com.sptm.backend.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import com.google.api.services.calendar.model.EventDateTime;
import com.sptm.backend.repository.TaskRepository;
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

    @Autowired
    private TaskRepository taskRepository;

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
            System.out.println("Google Calendar connected for user: " + user.getUsername());
        } else {
            System.out.println("Warning: No refresh token received for user: " + user.getUsername());
        }

        // Store credential for future use
        flow.createAndStoreCredential(response, userId.toString());
        // Note: We don't fetch events here - that will be done when user explicitly
        // requests
    }

    public List<CalendarEvent> fetchEvents(Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getGoogleRefreshToken() == null) {
            throw new RuntimeException("User is not connected to Google Calendar");
        }

        Credential credential = getCredential(userId, user.getGoogleRefreshToken());
        fetchAndSaveEvents(user, credential);

        return calendarEventRepository.findByUserId(userId);
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

    public void addTaskToGoogleCalendar(Long userId, Long taskId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        com.sptm.backend.model.Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (user.getGoogleRefreshToken() == null) {
            throw new RuntimeException("User is not connected to Google Calendar");
        }

        Credential credential = getCredential(userId, user.getGoogleRefreshToken());
        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        Event event = new Event()
                .setSummary(task.getTitle())
                .setDescription(task.getDescription());

        if (task.getDueDate() != null) {
            DateTime startDateTime = new DateTime(
                    java.util.Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant()));
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(ZoneId.systemDefault().getId());
            event.setStart(start);

            DateTime endDateTime = new DateTime(
                    java.util.Date.from(task.getDueDate().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()));
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone(ZoneId.systemDefault().getId());
            event.setEnd(end);
        } else {
            // Default to all day today if no due date? Or skip?
            // Google Calendar events usually need a time.
            // Let's use now + 1 hour if no due date, or maybe throw error.
            throw new RuntimeException("Task must have a due date to be synced.");
        }

        Event createdEvent = service.events().insert("primary", event).execute();

        task.setCalendarEventId(createdEvent.getId());
        taskRepository.save(task);
    }

    private Credential getCredential(Long userId, String refreshToken) throws IOException {
        Credential credential = flow.loadCredential(userId.toString());
        if (credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() > 60)) {
            return credential;
        }

        // Create new credential from refresh token
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setRefreshToken(refreshToken);
        // We set attributes that indicate it's a bearer token type
        tokenResponse.setTokenType("Bearer");
        // We can lets the flow refresh it automatically

        return flow.createAndStoreCredential(tokenResponse, userId.toString());
    }
}
