package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.model.Role;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    // ---------------------------------------------------------
    // 1. Dependencias mockeadas
    // ---------------------------------------------------------
    // Mockeamos el repositorio porque NO queremos acceder a BD real.
    @Mock
    private UserRepository repo;

    // ---------------------------------------------------------
    // 2. Clase bajo test
    // ---------------------------------------------------------
    @InjectMocks
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // 3. Caso positivo: usuario encontrado
    // ---------------------------------------------------------
    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // Arrange
        String username = "aaron";
        User user = new User(username, "encodedPass", Role.ADMIN);

        // Simulamos que el usuario existe en BD
        when(repo.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = service.loadUserByUsername(username);

        // Assert
        assertEquals(username, result.getUsername(), "El username debe coincidir");
        assertEquals("encodedPass", result.getPassword(), "La contraseña debe coincidir");

        // Spring Security añade automáticamente el prefijo ROLE_
        assertTrue(
                result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")),
                "Debe contener el rol ROLE_ADMIN"
        );

        verify(repo).findByUsername(username); // Se llamó al repositorio
    }

    // ---------------------------------------------------------
    // 4. Caso negativo: usuario NO encontrado
    // ---------------------------------------------------------
    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        // Arrange
        String username = "unknown";

        // Simulamos que NO existe
        when(repo.findByUsername(username)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername(username),
                "Debe lanzar UsernameNotFoundException si no existe"
        );

        verify(repo).findByUsername(username); // Se llamó al repositorio
    }
}
