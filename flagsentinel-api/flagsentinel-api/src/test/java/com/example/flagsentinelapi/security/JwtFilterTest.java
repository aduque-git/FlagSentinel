package com.example.flagsentinelapi.security;

import com.example.flagsentinelapi.service.CustomUserDetailsService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    private JwtFilter filter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    // ---------------------------------------------------------
    // TOKEN VÁLIDO
    // ---------------------------------------------------------
    @Test
    void doFilterInternal_shouldAuthenticateUser_whenTokenIsValid() throws Exception {
        // Inicializar el contexto de seguridad ANTES de ejecutar el filtro
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validToken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.isValid("validToken")).thenReturn(true);
        when(jwtUtil.extractUsername("validToken")).thenReturn("aaron");
        when(userDetailsService.loadUserByUsername("aaron")).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("aaron");
        when(userDetails.getAuthorities()).thenReturn(java.util.List.of());

        filter.doFilterInternal(request, response, filterChain);

        // Verificamos que el usuario fue autenticado
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("aaron", auth.getName());

        verify(filterChain).doFilter(request, response);
    }

    // ---------------------------------------------------------
    // TOKEN INVÁLIDO
    // ---------------------------------------------------------
    @Test
    void doFilterInternal_shouldNotAuthenticateUser_whenTokenIsInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidToken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.isValid("invalidToken")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        // No debe haber autenticación
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    // ---------------------------------------------------------
    // SIN TOKEN
    // ---------------------------------------------------------
    @Test
    void doFilterInternal_shouldNotAuthenticateUser_whenNoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    // ---------------------------------------------------------
    // HEADER MAL FORMADO
    // ---------------------------------------------------------
    @Test
    void doFilterInternal_shouldIgnoreHeader_whenNotBearerFormat() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Token somethingElse");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
