package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.service.FeatureFlagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FeatureFlagControllerTest {

    @Mock
    private FeatureFlagService service;

    @InjectMocks
    private FeatureFlagController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Test
    void create_shouldReturnFeatureFlagResponse() throws Exception {
        CreateFeatureFlagRequest req = new CreateFeatureFlagRequest("flag1", true, List.of());
        FeatureFlagResponse response = new FeatureFlagResponse(1L, "flag1", true, List.of());

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "key": "flag1",
                                  "enabled": true,
                                  "rules": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.key").value("flag1"))
                .andExpect(jsonPath("$.enabled").value(true));

        verify(service).create(any());
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Test
    void update_shouldReturnUpdatedFeatureFlag() throws Exception {
        UpdateFeatureFlagRequest req = new UpdateFeatureFlagRequest("flag1", false, List.of());
        FeatureFlagResponse response = new FeatureFlagResponse(1L, "flag1", false, List.of());

        when(service.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/flags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "key": "flag1",
                                  "enabled": false,
                                  "rules": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));

        verify(service).update(eq(1L), any());
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @Test
    void getAll_shouldReturnListOfFeatureFlags() throws Exception {
        FeatureFlagResponse f1 = new FeatureFlagResponse(1L, "flag1", true, List.of());
        FeatureFlagResponse f2 = new FeatureFlagResponse(2L, "flag2", false, List.of());

        when(service.getAll()).thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/api/flags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].key").value("flag1"))
                .andExpect(jsonPath("$[1].key").value("flag2"));

        verify(service).getAll();
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Test
    void getById_shouldReturnFeatureFlag() throws Exception {
        FeatureFlagResponse response = new FeatureFlagResponse(1L, "flag1", true, List.of());

        when(service.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/flags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("flag1"));

        verify(service).getById(1L);
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/flags/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
