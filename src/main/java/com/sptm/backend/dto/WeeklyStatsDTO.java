package com.sptm.backend.dto;

import lombok.Data;

@Data
public class WeeklyStatsDTO {
    private int totalTasks;
    private int completedTasks;
    private int completionRate; // Percentage
}
