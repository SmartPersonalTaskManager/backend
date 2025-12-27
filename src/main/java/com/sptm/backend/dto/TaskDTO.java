package com.sptm.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sptm.backend.model.Task;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Task.TaskPriority priority;
    private Task.TaskStatus status;
    private Long subMissionId;
    private Long missionId;
    private Long userId;

    // Additional fields for Covey Matrix inputs if needed
    @JsonProperty("urgent")
    private boolean urgent;

    @JsonProperty("important")
    private boolean important;

    private String context;

    @JsonProperty("isInbox")
    private boolean isInbox;

    @JsonProperty("isArchived")
    private boolean isArchived;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;
}
