package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagDTO;
import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagResponse;
import com.example.flagsentinelapi.dto.bootstrap.BootstrapRuleResponse;
import com.example.flagsentinelapi.exception.*;
import com.example.flagsentinelapi.middleware.JwtFilter;
import com.example.flagsentinelapi.service.FeatureFlagService;
import com.example.flagsentinelapi.service.RuleService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer test for SdkController.
 * Validates bootstrap endpoints and error mapping.
 */
@WebMvcTest(
        controllers = SdkController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class SdkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeatureFlagService featureFlagService;

    @MockitoBean
    private RuleService ruleService;

    // =========================================================
    // BOOTSTRAP FLAGS
    // =========================================================

    @Test
    void bootstrapFlags_shouldReturn200_whenDataExists() throws Exception {

        BootstrapFeatureFlagDTO dto = new BootstrapFeatureFlagDTO();
        dto.setFlagCode("test-flag");

        when(featureFlagService.getAllBootstrap())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bootstrap/flags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flags[0].flagCode").value("test-flag"));
    }

    @Test
    void bootstrapFlags_shouldReturn404_whenApiExceptionThrown() throws Exception {

        when(featureFlagService.getAllBootstrap())
                .thenThrow(new NotFoundException("No flags found"));

        mockMvc.perform(get("/api/bootstrap/flags"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("No flags found"))
                .andExpect(jsonPath("$.path").value("/api/bootstrap/flags"));
    }

    // =========================================================
    // BOOTSTRAP RULES
    // =========================================================

    @Test
    void bootstrapRules_shouldReturn200_whenDataExists() throws Exception {

        BootstrapRuleResponse rule = new BootstrapRuleResponse();
        rule.setId(1L);

        when(ruleService.getAllBootstrap())
                .thenReturn(List.of(rule));

        mockMvc.perform(get("/api/bootstrap/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void bootstrapRules_shouldReturn500_whenUnexpectedException() throws Exception {

        when(ruleService.getAllBootstrap())
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/bootstrap/rules"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"));
    }
}