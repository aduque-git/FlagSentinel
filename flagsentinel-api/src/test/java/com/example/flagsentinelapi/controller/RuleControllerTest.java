package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.rule.CreateRuleRequest;
import com.example.flagsentinelapi.dto.rule.RuleResponse;
import com.example.flagsentinelapi.dto.rule.UpdateRuleRequest;
import com.example.flagsentinelapi.exception.*;
import com.example.flagsentinelapi.middleware.JwtFilter;
import com.example.flagsentinelapi.service.RuleService;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer test for RuleController.
 * Validates HTTP contract and exception mapping.
 */
@WebMvcTest(
        controllers = RuleController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RuleService service;

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void create_shouldReturn200_whenValid() throws Exception {

        RuleResponse response = new RuleResponse();
        response.setId(1L);

        when(service.create(any(CreateRuleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateRuleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_shouldReturn404_whenApiExceptionThrown() throws Exception {

        when(service.create(any()))
                .thenThrow(new NotFoundException("Rule not found"));

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateRuleRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Rule not found"))
                .andExpect(jsonPath("$.path").value("/api/rules"));
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void update_shouldReturn200_whenValid() throws Exception {

        RuleResponse response = new RuleResponse();
        response.setId(1L);

        when(service.update(eq(1L), any(UpdateRuleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/rules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateRuleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAll_shouldReturn200() throws Exception {

        RuleResponse response = new RuleResponse();
        response.setId(1L);

        when(service.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getById_shouldReturn200_whenExists() throws Exception {

        RuleResponse response = new RuleResponse();
        response.setId(1L);

        when(service.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/rules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {

        when(service.getById(1L))
                .thenThrow(new NotFoundException("Rule not found"));

        mockMvc.perform(get("/api/rules/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void delete_shouldReturn204_whenDeleted() throws Exception {

        mockMvc.perform(delete("/api/rules/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn403_whenAccessDenied() throws Exception {

        doThrow(new AccessDeniedException("forbidden"))
                .when(service).delete(1L);

        mockMvc.perform(delete("/api/rules/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Insufficient permissions"));
    }

    // =========================================================
    // PAGED
    // =========================================================

    @Test
    void getPaged_shouldReturnPage() throws Exception {

        RuleResponse response = new RuleResponse();
        response.setId(1L);

        PageImpl<RuleResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(service.findAllPaged(any())).thenReturn(page);

        mockMvc.perform(get("/api/rules/paged?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    // =========================================================
    // FALLBACK
    // =========================================================

    @Test
    void getAll_shouldReturn500_whenUnexpectedException() throws Exception {

        when(service.getAll())
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"));
    }
}