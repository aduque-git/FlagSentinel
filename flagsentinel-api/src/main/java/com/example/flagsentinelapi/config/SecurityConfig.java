package com.example.flagsentinelapi.config;

import com.example.flagsentinelapi.middleware.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
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

        configureAuthorization(http);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth

                // ============================
                // PUBLIC ENDPOINTS
                // ============================

                .requestMatchers("/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/openapi.yaml",
                        "/openapi.json").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/flags/evaluate").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/error").permitAll()

                // ============================
                // ADMIN-ONLY EXTRA ENDPOINTS
                // ============================

                .requestMatchers(HttpMethod.GET, "/api/rules/operators").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/bootstrap").hasRole("ADMIN")

                // ============================
                // FEATURE FLAGS
                // ============================

                .requestMatchers(HttpMethod.GET, "/api/flags/**")
                .hasAnyRole("USER", "ADMIN")

                .requestMatchers("/api/flags/**")
                .hasRole("ADMIN")

                // ============================
                // RULES
                // ============================

                .requestMatchers(HttpMethod.GET, "/api/rules/**")
                .hasAnyRole("USER", "ADMIN")

                .requestMatchers("/api/rules/**")
                .hasRole("ADMIN")

                // ============================
                // USERS
                // ============================

                .requestMatchers(HttpMethod.GET, "/api/users/**")
                .hasAnyRole("USER", "ADMIN")

                .requestMatchers("/api/users/**")
                .hasRole("ADMIN")

                // ============================
                // FALLBACK
                // ============================

                .anyRequest().authenticated()
        );
    }
}