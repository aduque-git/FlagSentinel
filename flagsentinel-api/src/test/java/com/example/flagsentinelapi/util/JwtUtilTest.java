package com.example.flagsentinelapi.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "MySuperSecretKeyForJwtTesting1234567890!";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretFromConfig", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1000L * 60); // 1 minuto
        jwtUtil.init();
    }

    @Test
    void shouldGenerateAndExtractUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertThat(token).isNotNull();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo(username);
    }

    @Test
    void shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("user1");
        assertThat(jwtUtil.isValid(token)).isTrue();
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThat(jwtUtil.isValid(invalidToken)).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenSecretNotConfigured() {
        JwtUtil util = new JwtUtil();
        IllegalStateException ex = assertThrows(IllegalStateException.class, util::init);
        assertThat(ex).hasMessageContaining("JWT secret no está configurado");
    }
}