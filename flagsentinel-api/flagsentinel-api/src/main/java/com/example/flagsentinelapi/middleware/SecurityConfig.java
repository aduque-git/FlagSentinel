package com.example.flagsentinelapi.middleware;

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
                //  ENDPOINTS PÚBLICOS
                // ============================

                // Swagger Api Docs
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs",
                        "/v3/api-docs.yaml", "/v3/api-docs/**", "/v3/api-docs/swagger-config", "/openapi.yaml",
                        "/error").permitAll()
                // Login
                .requestMatchers("/api/auth/**").permitAll()

                // Evaluación de flags (pública)
                .requestMatchers("/api/flags/evaluate").permitAll()

                // WebSocket
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/sdk/**").authenticated()


                // ============================
                //  ENDPOINTS SOLO ADMIN
                // ============================

                // FeatureFlags: crear, actualizar, eliminar
                .requestMatchers(HttpMethod.POST, "/api/flags/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/flags/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/flags/**").hasRole("ADMIN")

                // Rules: crear, actualizar, eliminar
                .requestMatchers(HttpMethod.POST, "/api/rules/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/rules/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/rules/**").hasRole("ADMIN")

                // Users: crear, actualizar, eliminar
                .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")


                // ============================
                //  ENDPOINTS USER + ADMIN
                // ============================

                // FeatureFlags: lectura
                .requestMatchers(HttpMethod.GET, "/api/flags/**").hasAnyRole("USER", "ADMIN")

                // Rules: lectura
                .requestMatchers(HttpMethod.GET, "/api/rules/**").hasAnyRole("USER", "ADMIN")

                // Users: lectura
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("USER", "ADMIN")


                // ============================
                //  CUALQUIER OTRA RUTA
                // ============================
                .anyRequest().authenticated()
        );
    }
}
