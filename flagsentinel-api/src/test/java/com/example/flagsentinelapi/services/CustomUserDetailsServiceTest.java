package com.example.flagsentinelapi.services;

import com.example.flagsentinelapi.dto.user.Role;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;
import com.example.flagsentinelapi.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void shouldLoadUserSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("encoded-pass");
        user.setRole(Role.ADMIN);

        when(repo.findByUsername("admin")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = service.loadUserByUsername("admin");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getPassword()).isEqualTo("encoded-pass");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");

        verify(repo).findByUsername("admin");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        // Arrange
        when(repo.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid credentials");

        verify(repo).findByUsername("unknown");
    }
}