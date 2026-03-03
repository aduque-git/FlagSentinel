package com.example.flagsentinelapi.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebSocketChannelConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthChannelInterceptor interceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        /*
         * Intercepta todos los mensajes entrantes desde clientes.
         * Aquí se inyecta la autenticación.
         */
        registration.interceptors(interceptor);
    }
}