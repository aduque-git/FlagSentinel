# 📜 Changelog — FlagSentinel


Este documento recoge los cambios realizados en el proyecto FlagSentinel.  
El formato sigue el estándar *Keep a Changelog*.

# Changelog 
## [1.0.1]
- 2026-01-28 
    #### Added 
    - Modelos JPA: FeatureFlag y Rule 
    - DTOs de entrada, salida e intermedios
    - Mappers para conversión entre entidades y DTOs 
    - Controladores refactorizados para CRUD de flags y reglas 
    - Motor de evaluación de reglas (FlagEvaluator) 
    - Servicio de evaluación de flags 
    - Base del sistema de seguridad con JWT 
    - AuthController para login 

    #### Changed 
    - Servicios actualizados con métodos getById y update 
    - Controladores reorganizados para no exponer entidades JPA 
    
    #### Notes 
    - Preparado para configurar entornos (dev, prod, test) 
    - Pendiente completar seguridad avanzada y configuración de perfiles
