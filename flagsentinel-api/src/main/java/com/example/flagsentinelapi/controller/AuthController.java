package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.login.LoginRequest;
import com.example.flagsentinelapi.dto.login.LoginResponse;
import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import com.example.flagsentinelapi.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);


    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info(ApiLogMessages.get(LogPropertiesKeys.AUTH_LOGIN_ATTEMPT, request.getUsername()));

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            if (auth.isAuthenticated()) {
                String token = jwtUtil.generateToken(request.getUsername());
                log.info(ApiLogMessages.get(LogPropertiesKeys.AUTH_LOGIN_SUCCESS, request.getUsername(), "127.0.0.1"));
                log.debug(ApiLogMessages.get(LogPropertiesKeys.AUTH_TOKEN_GENERATED, request.getUsername()));
                return ResponseEntity.ok(new LoginResponse(token));
            } else {
                log.warn(ApiLogMessages.get(LogPropertiesKeys.AUTH_LOGIN_FAILED, request.getUsername(), "Authentication failed"));
                throw new RuntimeException("Invalid credentials");
            }
        } catch (Exception e) {
            log.warn(ApiLogMessages.get(LogPropertiesKeys.AUTH_LOGIN_FAILED, request.getUsername(), e.getMessage()));
            throw new RuntimeException("Invalid credentials");
        }
    }

}

