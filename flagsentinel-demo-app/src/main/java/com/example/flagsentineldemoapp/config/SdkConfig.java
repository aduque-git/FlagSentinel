package com.example.flagsentineldemoapp.config;

import org.example.service.FlagClient;
import org.example.service.FlagSDK;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SdkConfig {

    @Bean
    public FlagClient flagClient() {
        return FlagSDK.init()
                .endpoint("http://localhost:8080")
                .username("admin")
                .password("admin")
                .connectWebSocket(true)
                .build(); // ← ya viene inicializado
    }
}
