package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.dto.user.Role;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitializer initializer;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(initializer, "adminUsername", "admin");
        ReflectionTestUtils.setField(initializer, "adminPassword", "secret");
    }

    // ---------------------------------------------------------
    // SKIP WHEN USERS EXIST
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should skip admin creation when users already exist")
    void shouldSkipAdminCreationWhenUsersExist() {

        // Given
        given(userRepository.count()).willReturn(5L);

        // When
        initializer.run();

        // Then
        then(userRepository).should(never()).save(any());
    }

    // ---------------------------------------------------------
    // CREATE ADMIN WHEN EMPTY DB
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should create admin user when database is empty")
    void shouldCreateAdminWhenDatabaseEmpty() {

        // Given
        given(userRepository.count()).willReturn(0L);
        given(passwordEncoder.encode("secret")).willReturn("encoded");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        initializer.run();

        // Then
        then(userRepository).should().save(userCaptor.capture());

        User saved = userCaptor.getValue();

        assertThat(saved.getUsername()).isEqualTo("admin");
        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getRole()).isEqualTo(Role.ADMIN);
    }
}