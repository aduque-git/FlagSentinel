package com.example.flagsentinelapi.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketEventPublisher {

    /*
     * Enviar Eventos Socket
     */
    private final SimpMessagingTemplate messagingTemplate;

    public void publishFlagUpdate(Object payload) {
        messagingTemplate.convertAndSend("/topic/flags", payload);
    }

    public void publishRuleUpdate(Object payload) {
        messagingTemplate.convertAndSend("/topic/rules", payload);
    }
}
