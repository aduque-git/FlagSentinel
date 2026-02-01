package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ---------------------------------------------------------
    // LOGIN SUCCESS
    // ---------------------------------------------------------
    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        String username = "aaron";
        String password = "1234";
        String token = "jwt-token";

        Authentication auth = mock(Authentication.class);

        when(authManager.authenticate(any())).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn(token);

        mockMvc.perform(post("/api/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(authManager).authenticate(any());
        verify(jwtUtil).generateToken(username);
    }

    // ---------------------------------------------------------
    // LOGIN FAILURE (AUTHENTICATION MANAGER FAILS)
    // ---------------------------------------------------------
    @Test
    void login_shouldThrow_whenAuthenticationFails() throws Exception {
        String username = "aaron";
        String password = "wrong";

        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isInternalServerError()); // RuntimeException

        verify(authManager).authenticate(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    // ---------------------------------------------------------
    // LOGIN FAILURE (AUTH RETURNS NOT AUTHENTICATED)
    // ---------------------------------------------------------
    @Test
    void login_shouldThrow_whenAuthObjectIsNotAuthenticated() throws Exception {
        String username = "aaron";
        String password = "1234";

        Authentication auth = mock(Authentication.class);

        when(authManager.authenticate(any())).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isInternalServerError());

        verify(jwtUtil, never()).generateToken(any());
    }
}
