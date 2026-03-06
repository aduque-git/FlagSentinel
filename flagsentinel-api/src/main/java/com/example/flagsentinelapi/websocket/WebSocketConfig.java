package com.example.flagsentinelapi.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*
     * Interceptor que validará el JWT durante el handshake inicial HTTP
     * (antes de que la conexión WebSocket quede establecida)
     */

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Bean
    public TaskScheduler stompHeartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(stompHeartbeatScheduler());

        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        /*
         * Endpoint de conexión WebSocket.
         * Aquí es donde el cliente abre el handshake inicial:
         *
         * ws://host/ws
         */
        registry.addEndpoint("/ws")

                /*
                 * Permitimos todos los orígenes temporalmente - CAMBIAR POR CONFIGURACION CORS
                 */
                .setAllowedOriginPatterns("*")

                /*
                 * Interceptor que valida JWT y adjunta autenticación
                 * antes de que la sesión WebSocket se cree.
                 */
                .addInterceptors(jwtHandshakeInterceptor);
    }
}
