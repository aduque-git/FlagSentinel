## 🧪 Aplicación Demo

El repositorio incluye una **aplicación demo basada en Spring Boot** que muestra cómo integrar y 
utilizar el SDK de FlagSentinel en un servicio real.

La aplicación:

- Inicializa el **FlagSentinel SDK**
- Mantiene un **cache local de flags y reglas**
- Escucha **actualizaciones en tiempo real mediante WebSocket**
- Expone un **dashboard HTTP** para inspeccionar el estado del SDK

---

## 📦 Dependencia del SDK

El SDK se integra mediante Maven:

```xml
<dependency>
    <groupId>com.flagsentinel</groupId>
    <artifactId>flagsentinel-sdk-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

# ⚙️ Configuración del SDK

El cliente del SDK se registra como bean de Spring para que pueda inyectarse en cualquier servicio de la aplicación.
```java
    @Configuration
    public class SdkConfig {
    
        @Bean
        public FlagClient flagClient() {
            return FlagSDK.init()
                    .endpoint("http://localhost:8080")
                    .username("admin")
                    .password("admin")
                    .connectWebSocket(true)
                    .build();
        }
    }
```

El cliente queda inicializado automáticamente durante el arranque de la aplicación.

---

# 🧠 Uso del SDK en Servicios

Los servicios de la aplicación pueden acceder al SDK mediante inyección de dependencias.
```java
    @Service
    @RequiredArgsConstructor
    public class SdkService {
    
        private final FlagClient flagClient;
    
        public boolean evaluate(String flagCode, Map<String, Object> ctx) {
            return flagClient.isEnabled(flagCode, ctx);
        }
}
```
Esto permite evaluar feature flags dinámicamente según el contexto de ejecución.

---

# 📊 Dashboard de Observabilidad

La demo incluye un pequeño dashboard backend que expone el estado interno del SDK.

Endpoints disponibles:

| Endpoint            | Descripción                      |
| ------------------- | -------------------------------- |
| `/dashboard/flags`  | Lista de flags cargadas en cache |
| `/dashboard/rules`  | Reglas disponibles               |
| `/dashboard/status` | Estado de conexión del SDK       |
| `/dashboard/events` | Últimos eventos recibidos        |

Este dashboard permite observar:

 - Sincronización del cache
 - Estado del WebSocket
 - Actualizaciones de flags y reglas

---

# 📡 Eventos del SDK

La aplicación implementa FlagClientListener para recibir eventos generados por el SDK.

 - Eventos soportados:
 - Conexión al WebSocket
 - Desconexión
 - Eventos internos del cliente
 - Actualización de flags
 - Actualización de reglas

Esto permite registrar logs o integrar el SDK con sistemas de monitorización.

---

# 🚀 Ejecución

1. Ejecutar el servidor de FlagSentinel
2. Iniciar la aplicación demo
    ```bash
    mvn spring-boot:run
    ```
3. Acceder al dashboard en http://localhost:8081/dashboard

---

# 🎯 Objetivo de la Demo

La aplicación demuestra:

 - Integración del SDK en Spring Boot
 - Evaluación local de feature flags
 - Sincronización mediante WebSocket
 - Observabilidad del estado interno del cliente