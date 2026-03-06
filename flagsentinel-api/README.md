# 🚩 FlagSentinel API

FlagSentinel API es un servicio backend para la **gestión y evaluación de Feature Flags** mediante reglas dinámicas.

Permite activar o desactivar funcionalidades de una aplicación **sin necesidad de desplegar nuevas versiones**, 
utilizando reglas basadas en atributos de usuario o contexto.

La API proporciona:

- 🚩 Gestión de feature flags
- ⚙️ Evaluación dinámica mediante reglas
- 🔐 Autenticación con JWT
- 👥 Control de acceso por roles
- 📡 Actualizaciones en tiempo real con WebSocket
- 📖 Documentación OpenAPI
- 🧾 Logging estructurado

El sistema está diseñado para actuar como **plataforma centralizada de control de feature flags** 
para aplicaciones cliente o SDKs.

---

# 📌 ¿Qué son las Feature Flags?

Las **Feature Flags (o Feature Toggles)** permiten controlar el comportamiento de una aplicación **en tiempo de ejecución**.

Esto permite a los equipos:

- 🚀 Desplegar código sin activar la funcionalidad
- 🧪 Realizar A/B testing
- 🛠 Activar o desactivar features rápidamente
- 👥 Activar funcionalidades para segmentos concretos de usuarios
- 📉 Reducir riesgos en despliegues

---

# 🏗 Arquitectura

La API sigue una arquitectura en capas típica de aplicaciones Spring Boot.

 --- 
Cliente / SDK

│

▼

Controllers (REST API)

│

▼

Services (Lógica de negocio)

│

▼

Repositories (Acceso a datos)

│

▼

Base de datos

---

Componentes principales:

- **Controllers** → Exponen los endpoints REST
- **Services** → Implementan la lógica de negocio
- **Repositories** → Acceso a la base de datos
- **DTOs & Mappers** → Separación entre modelos internos y API
- **Middleware** → Seguridad, logging y filtros
- **WebSocket** → Comunicación en tiempo real

---

# 🔐 Autenticación


La autenticación se realiza mediante **JWT (JSON Web Token)**.

### Login
POST /api/auth/login

Ejemplo de petición:

```json
{
  "username": "admin",
  "password": "password"
}

Respuesta:

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
Las peticiones posteriores deben incluir el token:
Authorization: Bearer <token>
---
# 👤 Roles y permisos


El sistema utiliza control de acceso basado en roles (RBAC).

Roles disponibles:

👑 ADMIN

👤 USER

Permisos:

| Recurso            | USER | ADMIN |
| ------------------ | ---- | ----- |
| Ver flags          | ✅    | ✅     |
| Crear/editar flags | ❌    | ✅     |
| Ver reglas         | ✅    | ✅     |
| Gestionar reglas   | ❌    | ✅     |
| Gestionar usuarios | ❌    | ✅     |

---
# 🚩 Gestión de Feature Flags


Los feature flags representan funcionalidades que pueden activarse o desactivarse dinámicamente.

Ejemplo:

```json
{
"key": "nuevo_checkout",
"enabled": true
}
```
Cada flag puede tener reglas asociadas que determinan cuándo se activa.

---
# ⚙️ Reglas


Las reglas definen condiciones para activar una feature.

Ejemplo:

```json
{
"attribute": "country",
"operator": "EQUALS",
"value": "ES"
}
```
Esto permite activar funcionalidades solo para determinados usuarios o contextos.

---

# 🔎 Evaluación de Feature Flags

Las aplicaciones cliente pueden consultar si una funcionalidad está activa mediante:
 - POST /api/flags/evaluate

Ejemplo de petición:

```json
{
  "key": "nuevo_checkout",
  "attributes": {
    "userId": "123",
    "country": "ES"
  }
}

Respuesta:

{
  "flagKey": "nuevo_checkout",
  "enabled": true,
  "reason": "Rule matched: country EQUALS ES"
}
```
Este endpoint es público, pensado para consumo desde SDKs.

---

# 📦 Bootstrap para SDK

Los clientes pueden obtener la configuración inicial del sistema:

- GET /api/bootstrap
Devuelve:

🚩 Flags disponibles

⚙️ Reglas asociadas

🔧 Operadores de reglas

Esto permite a los SDKs evaluar flags localmente.

---

# 📡 Actualizaciones en tiempo real

La API soporta WebSockets para enviar actualizaciones cuando cambian los flags o las reglas.

Endpoint:

/ws

Uso típico:

- Sincronización de SDKs
- Actualización instantánea de configuraciones
- Activación inmediata de features

---

# 📖 Documentación de la API

La API incluye especificación OpenAPI / Swagger.

Endpoints disponibles:

 - /swagger-ui
 - /v3/api-docs
 - /openapi.yaml

Permiten explorar y probar la API de forma interactiva.

---

# 🧾 Logging y observabilidad

El sistema incluye:

 - 🪵 Logging de peticiones HTTP
 - 🔗 Correlation ID para trazabilidad
 - 📊 Logs estructurados

Esto facilita el debugging y monitorización en producción.

---

# ▶️ Ejecución del proyecto
Requisitos

 - Java 17+
 - Maven
 - Base de datos configurada

Ejecutar en desarrollo
```bash
mvn spring-boot:run
```

Compilar y ejecutar

```bash
mvn clean package
java -jar target/flagsentinel-api.jar
```
---

# 🧪 Tests

El proyecto incluye:

 - Tests de servicios
 - Tests de controladores

Ejecutar:
```bash
mvn test
```
---

# 📂 Estructura del proyecto
    config/        Configuración de la aplicación
    controller/    Endpoints REST
    dto/           Modelos de petición y respuesta
    exception/     Manejo de errores
    logging/       Utilidades de logging
    mapper/        Conversión entidad ↔ DTO
    middleware/    Filtros e interceptores
    model/         Entidades del dominio
    repository/    Acceso a datos
    service/       Lógica de negocio
    util/          Utilidades
    websocket/     Configuración WebSocket

---

# 📜 Licencia
Proyecto interno – uso privado.

---