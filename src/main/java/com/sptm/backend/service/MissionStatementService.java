package com.sptm.backend.service;

import com.sptm.backend.dto.MissionStatementDTO;
import com.sptm.backend.dto.SubMissionDTO;
import com.sptm.backend.model.MissionStatement;
import com.sptm.backend.model.SubMission;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.MissionStatementRepository;
import com.sptm.backend.repository.SubMissionRepository;
import com.sptm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionStatementService {

    @Autowired
    private MissionStatementRepository missionRepository;

    @Autowired
    private SubMissionRepository subMissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MissionStatementDTO createMission(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MissionStatement mission = new MissionStatement();
        mission.setContent(content);
        mission.setUser(user);
        mission.setVersion(1);

        mission = missionRepository.save(mission);
        return convertToDTO(mission);
    }

    @Transactional(readOnly = true)
    public List<MissionStatementDTO> getMissionsByUser(Long userId) {
        return missionRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubMissionDTO addSubMission(Long missionId, SubMissionDTO subMissionDTO) {
        MissionStatement mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        SubMission subMission = new SubMission();
        subMission.setTitle(subMissionDTO.getTitle());
        subMission.setDescription(subMissionDTO.getDescription());
        subMission.setMissionStatement(mission);

        subMission = subMissionRepository.save(subMission);
        return convertToSubMissionDTO(subMission);
    }

    @Transactional
    public MissionStatementDTO updateMission(Long missionId, String content) {
        MissionStatement mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        mission.setContent(content);
        // Simple version increment or keep same. Let's increment.
        mission.setVersion(mission.getVersion() + 1);

        mission = missionRepository.save(mission);
        return convertToDTO(mission);
    }

    @Transactional
    public SubMissionDTO updateSubMission(Long subMissionId, String title) {
        SubMission subMission = subMissionRepository.findById(subMissionId)
                .orElseThrow(() -> new RuntimeException("SubMission not found"));

        subMission.setTitle(title);
        subMission = subMissionRepository.save(subMission);
        return convertToSubMissionDTO(subMission);
    }

    @Transactional
    public void deleteMission(Long missionId) {
        MissionStatement mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        // Delete associated submissions first if not cascading?
        // JPA/Hibernate usually handles cascade if configured in Entity.
        // Assuming default cascade or manual cleanup. Let's rely on repo delete first.
        missionRepository.delete(mission);
    }

    @Transactional
    public void deleteSubMission(Long subMissionId) {
        if (!subMissionRepository.existsById(subMissionId)) {
            throw new RuntimeException("SubMission not found");
        }
        subMissionRepository.deleteById(subMissionId);
    }

    private MissionStatementDTO convertToDTO(MissionStatement mission) {
        MissionStatementDTO dto = new MissionStatementDTO();
        dto.setId(mission.getId());
        dto.setContent(mission.getContent());
        dto.setVersion(mission.getVersion());
        dto.setUserId(mission.getUser().getId());

        List<SubMission> subMissions = subMissionRepository.findByMissionStatementId(mission.getId());
        dto.setSubMissions(subMissions.stream()
                .map(this::convertToSubMissionDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private SubMissionDTO convertToSubMissionDTO(SubMission subMission) {
        SubMissionDTO dto = new SubMissionDTO();
        dto.setId(subMission.getId());
        dto.setTitle(subMission.getTitle());
        dto.setDescription(subMission.getDescription());
        return dto;
    }
}
