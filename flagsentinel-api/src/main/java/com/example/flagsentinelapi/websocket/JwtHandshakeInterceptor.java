package com.example.flagsentinelapi.websocket;

import com.example.flagsentinelapi.service.CustomUserDetailsService;
import com.example.flagsentinelapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        /*
         * Solo podemos acceder a headers HTTP si el request es servlet.
         */
        if (request instanceof ServletServerHttpRequest servletRequest) {

            /*
             * Leemos el header Authorization del handshake inicial.
             */
            String authHeader = servletRequest.getServletRequest()
                    .getHeader("Authorization");

            /*
             * Validamos formato Bearer token.
             */
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);

                /*
                 * Validamos firma, expiración y consistencia del JWT.
                 */
                if (jwtUtil.isValid(token)) {

                    /*
                     * Extraemos el username del token.
                     */
                    String username = jwtUtil.extractUsername(token);

                    /*
                     * Cargamos el usuario desde la base de datos.
                     */
                    var userDetails = userDetailsService.loadUserByUsername(username);

                    /*
                     * Creamos el objeto Authentication que usará Spring Security.
                     */
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Guardamos la autenticación en los atributos de la sesión.
                     * Luego será recuperada por el interceptor del canal.
                     */
                    attributes.put("SPRING.AUTHENTICATION", auth);
                }
            }
        }

        /*
         * Siempre devolvemos true para permitir el handshake.
         * La autorización real se hará en el canal STOMP.
         */
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // No se requiere lógica posterior al handshake
    }
}