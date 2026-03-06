package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.flag.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.flag.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.flag.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.exception.*;
import com.example.flagsentinelapi.service.FeatureFlagService;
import com.example.flagsentinelapi.middleware.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer test for FeatureFlagController.
 * Validates HTTP contract and exception mapping.
 */
@WebMvcTest(
        controllers = FeatureFlagController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class FeatureFlagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeatureFlagService service;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void create_shouldReturn200_whenValid() throws Exception {

        FeatureFlagResponse response = new FeatureFlagResponse();
        response.setId(1L);

        when(service.create(any(CreateFeatureFlagRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateFeatureFlagRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_shouldReturnBusinessError_whenApiExceptionThrown() throws Exception {

        when(service.create(any()))
                .thenThrow(new NotFoundException("Flag not found"));

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateFeatureFlagRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Flag not found"))
                .andExpect(jsonPath("$.path").value("/api/flags"));
    }

    @Test
    void create_shouldReturnDatabaseError_whenIntegrityViolation() throws Exception {

        when(service.create(any()))
                .thenThrow(new DataIntegrityViolationException("constraint"));

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateFeatureFlagRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DATABASE_ERROR"))
                .andExpect(jsonPath("$.message").value("Database constraint violation"));
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void update_shouldReturn200_whenValid() throws Exception {

        FeatureFlagResponse response = new FeatureFlagResponse();
        response.setId(1L);

        when(service.update(eq(1L), any(UpdateFeatureFlagRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/flags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateFeatureFlagRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_shouldReturn404_whenNotFound() throws Exception {

        when(service.update(eq(1L), any()))
                .thenThrow(new NotFoundException("Flag not found"));

        mockMvc.perform(put("/api/flags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateFeatureFlagRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getById_shouldReturn200_whenExists() throws Exception {

        FeatureFlagResponse response = new FeatureFlagResponse();
        response.setId(1L);

        when(service.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/flags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {

        when(service.getById(1L))
                .thenThrow(new NotFoundException("Flag not found"));

        mockMvc.perform(get("/api/flags/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void delete_shouldReturn204_whenDeleted() throws Exception {

        mockMvc.perform(delete("/api/flags/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturnForbidden_whenAccessDenied() throws Exception {

        doThrow(new AccessDeniedException("forbidden"))
                .when(service).delete(1L);

        mockMvc.perform(delete("/api/flags/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Insufficient permissions"));
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAll_shouldReturn200() throws Exception {

        FeatureFlagResponse response = new FeatureFlagResponse();
        response.setId(1L);

        when(service.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/flags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // =========================================================
    // PAGED
    // =========================================================

    @Test
    void getPaged_shouldReturnPage() throws Exception {

        FeatureFlagResponse response = new FeatureFlagResponse();
        response.setId(1L);

        PageImpl<FeatureFlagResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(service.findAllPaged(any())).thenReturn(page);

        mockMvc.perform(get("/api/flags/paged?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    // =========================================================
    // FALLBACK
    // =========================================================

    @Test
    void getAll_shouldReturnInternalError_whenUnexpectedException() throws Exception {

        when(service.getAll())
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/flags"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"));
    }
}