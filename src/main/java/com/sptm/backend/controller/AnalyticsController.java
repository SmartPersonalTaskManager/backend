package com.sptm.backend.controller;

import com.sptm.backend.dto.WeeklyStatsDTO;
import com.sptm.backend.model.WeeklyReview;
import com.sptm.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/weekly/{userId}")
    public ResponseEntity<WeeklyStatsDTO> getWeeklyStats(@PathVariable Long userId) {
        return ResponseEntity.ok(analyticsService.getWeeklyStats(userId));
    }

    @PostMapping("/review")
    public ResponseEntity<WeeklyReview> createReview(@RequestParam Long userId, @RequestBody String notes) {
        return ResponseEntity.ok(analyticsService.createReview(userId, notes));
    }
}
