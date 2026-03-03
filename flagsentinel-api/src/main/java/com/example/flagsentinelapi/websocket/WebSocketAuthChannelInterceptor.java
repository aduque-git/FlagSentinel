package com.example.flagsentinelapi.websocket;

import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import com.example.flagsentinelapi.service.CustomUserDetailsService;
import com.example.flagsentinelapi.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null) {
                log.warn("{}", LogPropertiesKeys.WS_AUTH_HEADER_MISSING);
                throw new BadCredentialsException("Missing Authorization header");
            }

            authHeader = authHeader.trim();

            if (!authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
                log.warn("{}", LogPropertiesKeys.WS_AUTH_HEADER_MISSING);
                throw new BadCredentialsException("Invalid Authorization header format");
            }

            String token = authHeader.substring(7).trim();

            try {

                if (!jwtUtil.isValid(token)) {
                    log.warn("{}", LogPropertiesKeys.WS_INVALID_TOKEN);
                    throw new BadCredentialsException("Invalid token");
                }

                String username = jwtUtil.extractUsername(token);
                var userDetails = userDetailsService.loadUserByUsername(username);

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                accessor.setUser(auth);

                log.debug("{} - username={}", LogPropertiesKeys.WS_AUTH_SUCCESS, username);

            } catch (JwtException ex) {
                log.warn("{}", LogPropertiesKeys.WS_INVALID_TOKEN);
                throw new BadCredentialsException("Invalid token", ex);
            }
        }

        return message;
    }
}