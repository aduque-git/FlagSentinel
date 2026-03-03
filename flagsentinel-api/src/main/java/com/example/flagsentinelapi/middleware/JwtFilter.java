package com.example.flagsentinelapi.middleware;

import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import com.example.flagsentinelapi.service.CustomUserDetailsService;
import com.example.flagsentinelapi.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /*
     * 🔹 Endpoints que nunca deben pasar por el filtro JWT
     * (si no se excluyen aquí, Swagger y recursos públicos fallan)
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars",
            "/actuator/health"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        /*
         * 🔹 Si no hay header válido → NO se procesa JWT
         * Se deja continuar la cadena normalmente
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        /*
         * 🔹 Si ya hay autenticación en contexto → evitar reprocesar
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();

        try {

            if (!jwtUtil.isValid(token)) {
                log.warn(ApiLogMessages.get(
                        LogPropertiesKeys.JWT_INVALID,
                        ip, path
                ));
                chain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.extractUsername(token);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            auth.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            log.debug(ApiLogMessages.get(
                    LogPropertiesKeys.JWT_AUTH_SUCCESS,
                    username, ip, path
            ));

        } catch (JwtException ex) {

            log.warn(ApiLogMessages.get(
                    LogPropertiesKeys.JWT_AUTH_FAILED,
                    ex.getMessage(), ip, path
            ));

        } catch (Exception ex) {

            log.error(
                    ApiLogMessages.get(
                            LogPropertiesKeys.JWT_UNEXPECTED_ERROR,
                            ip, path
                    ),
                    ex
            );
        }

        chain.doFilter(request, response);
    }
}