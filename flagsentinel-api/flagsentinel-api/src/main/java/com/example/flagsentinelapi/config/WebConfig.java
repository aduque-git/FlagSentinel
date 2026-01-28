package com.example.flagsentinelapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.example.flagsentinelapi")
public class WebConfig {
    // Aquí más adelante podrás añadir:
    // - Configuración de recursos estáticos
    // - Configuración de message converters
    // - Interceptores
 }