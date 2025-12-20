package com.sptm.backend.service;

import com.sptm.backend.dto.MissionStatementDTO;
import com.sptm.backend.model.MissionStatement;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.MissionStatementRepository;
import com.sptm.backend.repository.SubMissionRepository;
import com.sptm.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MissionStatementServiceTest {

    @Mock
    private MissionStatementRepository missionRepository;

    @Mock
    private SubMissionRepository subMissionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MissionStatementService missionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMission_ShouldReturnDTO_WhenUserExists() {
        Long userId = 1L;
        String content = "My Mission";
        User user = new User();
        user.setId(userId);

        MissionStatement savedMission = new MissionStatement();
        savedMission.setId(1L);
        savedMission.setContent(content);
        savedMission.setVersion(1);
        savedMission.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(missionRepository.save(any(MissionStatement.class))).thenReturn(savedMission);
        when(subMissionRepository.findByMissionStatementId(1L)).thenReturn(Collections.emptyList());

        MissionStatementDTO result = missionService.createMission(userId, content);

        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(1, result.getVersion());
        verify(userRepository, times(1)).findById(userId);
        verify(missionRepository, times(1)).save(any(MissionStatement.class));
    }

    @Test
    void getMissionsByUser_ShouldReturnList_WhenMissionsExist() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        MissionStatement mission = new MissionStatement();
        mission.setId(1L);
        mission.setContent("Content");
        mission.setUser(user);
        mission.setVersion(1);

        when(missionRepository.findByUserId(userId)).thenReturn(List.of(mission));
        when(subMissionRepository.findByMissionStatementId(1L)).thenReturn(Collections.emptyList());

        List<MissionStatementDTO> results = missionService.getMissionsByUser(userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Content", results.get(0).getContent());
        verify(missionRepository, times(1)).findByUserId(userId);
    }
}
