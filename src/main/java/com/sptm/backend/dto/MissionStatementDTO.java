package com.sptm.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class MissionStatementDTO {
    private Long id;
    private String content;
    private Integer version;
    private Long userId;
    private List<SubMissionDTO> subMissions;
}
