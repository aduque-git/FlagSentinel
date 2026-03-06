package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.login.LoginRequest;
import com.example.flagsentinelapi.middleware.JwtFilter;
import com.example.flagsentinelapi.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link AuthController}.
 *
 * This test class validates the behavior of the authentication endpoint
 * in isolation from the rest of the application.
 *
 * - Only the web layer is loaded using @WebMvcTest.
 * - Security filters are disabled to focus strictly on controller logic.
 * - Dependencies such as AuthenticationManager and JwtUtil are mocked.
 */
@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Mocked AuthenticationManager to simulate authentication behavior
     * without invoking real security configuration.
     */
    @MockitoBean
    private AuthenticationManager authManager;

    /**
     * Mocked JwtUtil to simulate token generation logic.
     */
    @MockitoBean
    private JwtUtil jwtUtil;

    /**
     * ObjectMapper used to serialize request objects into JSON.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Verifies that a valid authentication request returns HTTP 200
     * and a JWT token in the response body.
     *
     * Scenario:
     * - AuthenticationManager successfully authenticates the user.
     * - JWT token is generated.
     * - Endpoint returns 200 OK with token payload.
     */
    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {

        // Arrange: prepare a valid login request payload
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("password");

        // Mock authentication result
        Authentication authentication = Mockito.mock(Authentication.class);

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Simulate successful authentication
        when(authentication.isAuthenticated()).thenReturn(true);

        // Mock token generation
        when(jwtUtil.generateToken("admin")).thenReturn("fake-jwt-token");

        // Act & Assert: perform HTTP request and validate response
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())                      // Expect HTTP 200
                .andExpect(jsonPath("$.token").value("fake-jwt-token")); // Validate token field
    }

    /**
     * Verifies that when authentication fails, the controller
     * returns an error response.
     *
     * Scenario:
     * - AuthenticationManager throws an exception.
     * - Controller catches it and rethrows RuntimeException.
     * - Response results in HTTP 500 (current implementation behavior).
     *
     * Note:
     * In a production-grade API, this should ideally return 401 Unauthorized.
     */
    @Test
    void login_shouldReturn500_whenAuthenticationFails() throws Exception {

        // Arrange: prepare invalid login request
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong-password");

        // Simulate authentication failure
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert: perform request and expect server error
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()); // Current behavior
    }
}