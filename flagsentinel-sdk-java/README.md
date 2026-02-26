# FlagSentinel Java SDK

SDK oficial para interactuar con la API de **FlagSentinel**, permitiendo:

- Autenticación con JWT
- Bootstrap de feature flags
- Evaluación local y remota
- Actualización en tiempo real mediante WebSocket
- Cache interna de flags
- Listeners para cambios de flags y reglas

Este SDK está diseñado para ser **ligero**, **rápido** y **fácil de integrar** en cualquier aplicación Java.

---

## 🚀 Instalación

Incluye las dependencias necesarias:

```xml
<dependency>
    <groupId>org.java-websocket</groupId>
    <artifactId>Java-WebSocket</artifactId>
    <version>1.5.6</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.0</version>
</dependency>

