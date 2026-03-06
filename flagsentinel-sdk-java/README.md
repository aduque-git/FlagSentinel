# 📦 FlagSentinel SDK (Java)

FlagSentinel SDK es el **cliente oficial en Java** para consumir la plataforma **FlagSentinel** desde aplicaciones externas.

El SDK permite a las aplicaciones:

- 🚩 Evaluar **Feature Flags**
- ⚡ Realizar **evaluación local de reglas**
- 📡 Recibir **actualizaciones en tiempo real mediante WebSocket**
- 🧠 Mantener **cache local de flags y reglas**
- 🔐 Autenticarse contra la API mediante **JWT**
- 🌐 Consumir los endpoints REST de FlagSentinel

El objetivo del SDK es proporcionar una forma **rápida, eficiente y segura** de integrar Feature Flags dentro de cualquier aplicación Java.

---

## 🧩 Arquitectura del SDK

El SDK está diseñado siguiendo una arquitectura modular orientada a **cliente ligero con evaluación local de Feature Flags**, soporte de **actualización en tiempo real** y fallback a **evaluación remota**.

El flujo general es:

1. Autenticación contra la API
2. Bootstrap inicial de flags y reglas
3. Cache local en memoria
4. Evaluación local de flags
5. Actualizaciones en tiempo real mediante WebSocket

---

## 🔐 Autenticación

El SDK se autentica contra el servidor utilizando credenciales (`username` y `password`) mediante el endpoint:
```
POST /api/auth/login
```


El token JWT obtenido se gestiona mediante `TokenManager` y se utiliza automáticamente en todas las peticiones HTTP posteriores.

---

## 📦 Bootstrap Inicial

Durante la inicialización del cliente se descargan todos los datos necesarios para evaluación local:

- Feature Flags
- Reglas asociadas

Endpoints utilizados:
```
GET /api/bootstrap/flags
GET /api/bootstrap/rules
```


Estos datos se almacenan en un **cache en memoria (`CacheProvider`)** para minimizar llamadas a la API.

---

## ⚡ Evaluación de Feature Flags

El SDK evalúa los flags siguiendo este orden:

1️⃣ **Cache local**

- Si el flag está presente en cache, se evalúa mediante `LocalEvaluationEngine`.

2️⃣ **Fallback remoto**

- Si el flag no existe en cache, se realiza una evaluación remota:
    ```POST /api/flags/evaluate
    ```


---

## 🧠 Motor de Evaluación

El motor de evaluación local analiza las reglas asociadas al flag comparándolas con el **contexto de evaluación** proporcionado por el cliente.

Operadores soportados:

- `equals`
- `not_equals`
- `contains`
- `greater_than`
- `less_than`

Un flag se considera **habilitado únicamente si todas sus reglas se cumplen**.

---

## 📡 Actualizaciones en Tiempo Real

El SDK puede conectarse opcionalmente a un **WebSocket** para recibir cambios en tiempo real.

Canales suscritos:

    /topic/flags
    /topic/rules


Eventos soportados:

- Creación o actualización de flags
- Creación o actualización de reglas
- Eliminación de flags
- Eliminación de reglas

Los cambios recibidos actualizan automáticamente el cache local.

---

## 🧰 Cache Local

El SDK mantiene un cache en memoria thread-safe basado en `ConcurrentHashMap`:

- Flags (`flagCode → FeatureFlag`)
- Reglas (`ruleId → Rule`)

Esto permite evaluaciones **sin latencia de red**.

---

## 🎧 Sistema de Eventos

El SDK permite registrar un `FlagClientListener` para recibir eventos del cliente:

- Conexión WebSocket
- Desconexión
- Eventos internos
- Actualización de flags
- Actualización de reglas

Esto permite integrar fácilmente el SDK con **logs, métricas o sistemas de observabilidad**.

---

## 🚀 Inicialización del Cliente

El SDK utiliza un **builder fluido** para simplificar la configuración:

```java
FlagClient client = FlagSDK.init()
    .endpoint("http://localhost:8080")
    .username("admin")
    .password("admin")
    .connectWebSocket(true)
    .build();
```

Una vez inicializado, el cliente puede evaluar flags mediante:

```java
boolean enabled = client.isEnabled("new_feature", context);
```

---

# 🎯 Objetivo del SDK

Este SDK proporciona:

 - Evaluación rápida y local
 - Consistencia eventual mediante WebSocket
 - Fallback seguro a evaluación remota
 - Integración sencilla en aplicaciones Java