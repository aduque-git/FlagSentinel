package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.LoginRequest;
import com.example.flagsentinelapi.dto.LoginResponse;
import com.example.flagsentinelapi.security.JwtUtil;
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

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login") public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        if (auth.isAuthenticated()){
            return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(request.getUsername())));
        }else{
            throw new RuntimeException("Invalid credentials");
        }
    }
}
