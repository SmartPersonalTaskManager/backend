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
    public ResponseEntity<String> syncCalendar(@RequestBody Map<String, String> payload,
            org.springframework.security.core.Authentication authentication) {
        String code = payload.get("code");
        if (code == null) {
            return ResponseEntity.badRequest().body("Auth code is required");
        }

        try {
            String email = (String) authentication.getPrincipal();
            com.sptm.backend.model.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            calendarService.syncEvents(user.getId(), code);
            return ResponseEntity.ok("Sync started");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sync failed: " + e.getMessage());
        }
    }

    @Autowired
    private com.sptm.backend.repository.CalendarEventRepository calendarEventRepository;

    @GetMapping("/events")
    public ResponseEntity<java.util.List<com.sptm.backend.model.CalendarEvent>> getEvents(
            org.springframework.security.core.Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        com.sptm.backend.model.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(calendarEventRepository.findByUserId(user.getId()));
    }

    @PostMapping("/push-task")
    public ResponseEntity<String> pushTask(@RequestBody Map<String, Long> payload,
            org.springframework.security.core.Authentication authentication) {
        Long taskId = payload.get("taskId");
        if (taskId == null) {
            return ResponseEntity.badRequest().body("Task ID is required");
        }

        try {
            String email = (String) authentication.getPrincipal();
            com.sptm.backend.model.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            calendarService.addTaskToGoogleCalendar(user.getId(), taskId);
            return ResponseEntity.ok("Task synced to Google Calendar");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sync failed: " + e.getMessage());
        }
    }
}
