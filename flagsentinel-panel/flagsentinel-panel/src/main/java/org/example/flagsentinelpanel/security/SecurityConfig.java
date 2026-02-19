package org.example.flagsentinelpanel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth -> auth

                // ============================
                //  RUTAS PÚBLICAS DEL PANEL
                // ============================
                .requestMatchers("/login",
                        "/",
                        "/VAADIN/**",
                        "/frontend/**",
                        "/webjars/**",
                        "/themes/**",
                        "/images/**",
                        "/icons/**",
                        "/manifest.webmanifest",
                        "/sw.js",
                        "/offline.html",
                        "/favicon.ico",
                        "/robots.txt")
                .permitAll()
                .anyRequest().authenticated()
        );

        http.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
            response.sendRedirect("/login");
        }));

        // Filtro JWT antes del filtro de autenticación
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
