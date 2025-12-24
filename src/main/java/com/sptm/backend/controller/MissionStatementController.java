package com.sptm.backend.controller;

import com.sptm.backend.dto.MissionStatementDTO;
import com.sptm.backend.dto.SubMissionDTO;
import com.sptm.backend.service.MissionStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
public class MissionStatementController {

    @Autowired
    private MissionStatementService missionService;

    @PostMapping
    public ResponseEntity<MissionStatementDTO> createMission(@RequestParam Long userId, @RequestBody String content) {
        return ResponseEntity.ok(missionService.createMission(userId, content));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MissionStatementDTO>> getMissionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(missionService.getMissionsByUser(userId));
    }

    @PostMapping("/{missionId}/submissions")
    public ResponseEntity<SubMissionDTO> addSubMission(@PathVariable Long missionId,
            @RequestBody SubMissionDTO subMissionDTO) {
        return ResponseEntity.ok(missionService.addSubMission(missionId, subMissionDTO));
    }

    @PutMapping("/{missionId}")
    public ResponseEntity<MissionStatementDTO> updateMission(@PathVariable Long missionId,
            @RequestBody String content) {
        return ResponseEntity.ok(missionService.updateMission(missionId, content));
    }

    @PutMapping("/submissions/{subMissionId}")
    public ResponseEntity<SubMissionDTO> updateSubMission(@PathVariable Long subMissionId,
            @RequestBody String content) {
        // content is just the raw string body
        return ResponseEntity.ok(missionService.updateSubMission(subMissionId, content));
    }

    @DeleteMapping("/{missionId}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long missionId) {
        missionService.deleteMission(missionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/submissions/{subMissionId}")
    public ResponseEntity<Void> deleteSubMission(@PathVariable Long subMissionId) {
        missionService.deleteSubMission(subMissionId);
        return ResponseEntity.noContent().build();
    }
}
