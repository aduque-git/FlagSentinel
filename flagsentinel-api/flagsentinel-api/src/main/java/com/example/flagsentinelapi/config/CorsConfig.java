package com.example.flagsentinelapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    /*
        🔥 Crear una configuración dev (orígenes abiertos) y otra prod (orígenes restringidos)
        🔥 Configurar CORS para JWT
        🔥 Configurar CORS para WebSockets
        🔥 Configurar CORS dinámico según base de datos o variables de entorno
     */

    /*
     * Cross-Origin Resource Sharing
     * Este bean define la configuracion global de CORS para toda la aplicacion
     * Spring detecta automaticamente este webmvcconfigurer y aplica las reglas
     * a todos los controladores REST
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {


             /*
              * Aquí configuramos qué orígenes, métodos y cabeceras pueden acceder a la API.
              * Esto es necesario cuando el frontend está en otro dominio (localhost:3000, etc.)
             */

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**") // Aplica CORS a todos los clientes permitidos
                        .allowedOrigins(
                                "http://localhost:8081",
                                "http://localhost:4200",
                                "http://localhost:5173"
                        ) // Orígenes permitidos (React, Angular, Vite, etc.)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        // Métodos HTTP permitidos
                        .allowedHeaders("*") // Permite todas las cabeceras
                        .allowCredentials(true) // Permite cookies/autenticación si usas JWT en cookies
                        .maxAge(3600); // Cachea la configuración durante 1 hora (mejora rendimiento)
            }
        };
    }
}
