package com.sptm.backend.controller;

import com.sptm.backend.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getAuthUrl() {
        try {
            return ResponseEntity.ok(Map.of("url", calendarService.getAuthorizationUrl()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @Autowired
    private com.sptm.backend.repository.UserRepository userRepository;

    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> syncCalendar(@RequestBody Map<String, String> payload,
            org.springframework.security.core.Authentication authentication) {
        String code = payload.get("code");
        if (code == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Auth code is required"));
        }

        try {
            String username = (String) authentication.getPrincipal();
            com.sptm.backend.model.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            calendarService.syncEvents(user.getId(), code);
            return ResponseEntity.ok(Map.of("message", "Sync started", "status", "success"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Sync failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshCalendar(org.springframework.security.core.Authentication authentication) {
        try {
            String username = (String) authentication.getPrincipal();
            com.sptm.backend.model.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            java.util.List<com.sptm.backend.model.CalendarEvent> events = calendarService.fetchEvents(user.getId());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Refresh failed: " + e.getMessage()));
        }
    }

    @Autowired
    private com.sptm.backend.repository.CalendarEventRepository calendarEventRepository;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getConnectionStatus(
            org.springframework.security.core.Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        com.sptm.backend.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        boolean isConnected = user.getGoogleRefreshToken() != null && !user.getGoogleRefreshToken().isEmpty();
        return ResponseEntity.ok(Map.of("connected", isConnected));
    }

    @GetMapping("/events")
    public ResponseEntity<java.util.List<com.sptm.backend.model.CalendarEvent>> getEvents(
            org.springframework.security.core.Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        com.sptm.backend.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return ResponseEntity.ok(calendarEventRepository.findByUserId(user.getId()));
    }

    @PostMapping("/push-task")
    public ResponseEntity<?> pushTask(@RequestBody Map<String, Long> payload,
            org.springframework.security.core.Authentication authentication) {
        Long taskId = payload.get("taskId");
        if (taskId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Task ID is required"));
        }

        try {
            String username = (String) authentication.getPrincipal();
            com.sptm.backend.model.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            calendarService.addTaskToGoogleCalendar(user.getId(), taskId);
            return ResponseEntity.ok(Map.of("message", "Task synced to Google Calendar"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Sync failed: " + e.getMessage()));
        }
    }
}
