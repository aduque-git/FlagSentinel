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

  # Changelog – API v1.0.x

## [1.0.3] – Finalización de fase API
### Added
- Nueva tabla de relación FeatureFlag ↔ Rule.
- Nuevos DTOs: `LoginRequest` y `LoginResponse`.
- Archivo Swagger/OpenAPI YAML actualizado.
- Nuevos endpoints y ajustes en los existentes para soportar reglas dinámicas.

### Changed
- Refactor del flujo de actualización de reglas en FeatureFlag.
- Ajustes en entidades y servicios para mejorar consistencia y trazabilidad.
- Normalización del manejo de colecciones Many-to-Many.
- Mejoras en la estructura interna del proyecto para facilitar futuras extensiones.

### Fixed
- Problemas de mapeo en el update de reglas.
- Inconsistencias entre Swagger y DTOs.
- Correcciones menores en validaciones y serialización.

### Notes
- La API queda estable y funcional.
- Se abre la línea 2.0.0 para el desarrollo del panel.

