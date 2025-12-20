package com.sptm.backend.controller;

import com.sptm.backend.dto.MissionStatementDTO;
import com.sptm.backend.dto.SubMissionDTO;
import com.sptm.backend.service.MissionStatementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MissionStatementController.class)
class MissionStatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MissionStatementService missionService;

    @Test
    void createMission_ShouldReturnCreatedMission() throws Exception {
        MissionStatementDTO dto = new MissionStatementDTO();
        dto.setId(1L);
        dto.setContent("Test Mission");
        dto.setUserId(1L);

        given(missionService.createMission(eq(1L), any(String.class))).willReturn(dto);

        mockMvc.perform(post("/api/missions")
                .param("userId", "1")
                .content("Test Mission")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test Mission"));
    }

    @Test
    void getMissionsByUser_ShouldReturnList() throws Exception {
        MissionStatementDTO dto = new MissionStatementDTO();
        dto.setId(1L);
        dto.setContent("Test Mission");
        dto.setSubMissions(Collections.emptyList());

        given(missionService.getMissionsByUser(1L)).willReturn(Arrays.asList(dto));

        mockMvc.perform(get("/api/missions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test Mission"));
    }
}
