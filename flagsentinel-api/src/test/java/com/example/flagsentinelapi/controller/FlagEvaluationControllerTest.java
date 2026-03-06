package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationResponse;
import com.example.flagsentinelapi.exception.*;
import com.example.flagsentinelapi.middleware.JwtFilter;
import com.example.flagsentinelapi.service.FlagEvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer test for FlagEvaluationController.
 * Validates evaluation endpoint and exception mapping.
 */
@WebMvcTest(
        controllers = FlagEvaluationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class FlagEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FlagEvaluationService service;

    // =========================================================
    // EVALUATE
    // =========================================================

    @Test
    void evaluate_shouldReturn200_whenEvaluationIsSuccessful() throws Exception {

        FlagEvaluationResponse response = new FlagEvaluationResponse();
        response.setKey("new-ui");
        response.setEnabled(true);

        when(service.evaluate(any(FlagEvaluationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/flags/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FlagEvaluationRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("new-ui"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void evaluate_shouldReturn404_whenApiExceptionThrown() throws Exception {

        when(service.evaluate(any()))
                .thenThrow(new NotFoundException("Flag not found"));

        mockMvc.perform(post("/api/flags/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FlagEvaluationRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Flag not found"))
                .andExpect(jsonPath("$.path").value("/api/flags/evaluate"));
    }

    @Test
    void evaluate_shouldReturn500_whenUnexpectedExceptionOccurs() throws Exception {

        when(service.evaluate(any()))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/api/flags/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FlagEvaluationRequest())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"));
    }
}