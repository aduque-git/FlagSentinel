# 🏗️ Arquitectura — FlagSentinel

FlagSentinel es un sistema distribuido de *feature flags* diseñado para ofrecer activación en tiempo real, reglas avanzadas y propagación instantánea de cambios.

---

## 🌐 Arquitectura General

Diagrama simplificado compatible con Markdown:

+------------------------+
|   FlagSentinel Panel   |
+-----------+------------+
|
|  REST + JWT
v
+----------------------------+
|     FlagSentinel API       |
+-----------+----------------+
|
+-------------------+-------------------+
|                   |                   |
v                   v                   v
+---------------+   +---------------+   +---------------+
|  PostgreSQL   |   | Redis PubSub |   |  WebSockets   |
+---------------+   +---------------+   +---------------+
^
|
v
+----------------------------+
|     FlagSentinel SDK       |
+----------------------------+


---

## 🧩 Componentes del Backend

- 🎛️ **Controllers** — Endpoints REST  
- 🧠 **Services** — Lógica de negocio  
- 📡 **WebSocketNotifier** — Notificaciones en tiempo real  
- 🔄 **EventPublisher** — Publicación de eventos en Redis  
- 🗄️ **Repositories** — Acceso a PostgreSQL  
- 🔐 **Security (JWT)**  

---

## 🗃️ Modelo de Datos

### 🏷️ FeatureFlag
- `id` — UUID  
- `name` — nombre único  
- `description` — descripción  
- `enabled` — activado/desactivado  
- `createdAt` — fecha de creación  
- `updatedAt` — última modificación  

### 🎯 Rule
- `id` — UUID  
- `flagId` — referencia al flag  
- `priority` — orden de evaluación  
- `rolloutPercentage` — porcentaje de activación  
- `conditions` — lista de condiciones en JSON  

### 📝 AuditLog
- `id` — UUID  
- `flagId` — referencia al flag  
- `adminUser` — usuario que hizo el cambio  
- `action` — CREATE / UPDATE / DELETE  
- `oldValue` — estado anterior  
- `newValue` — estado nuevo  
- `timestamp` — fecha del cambio  

---

## 🔄 Flujo de Propagación de Cambios

+------------------------+
|   FlagSentinel Panel   |
+-----------+------------+
|
| 1. Admin modifica un flag
v
+----------------------------+
|     FlagSentinel API       |
+-----------+----------------+
|
| 2. Guarda cambios en DB
v
+----------------------------+
|        PostgreSQL          |
+----------------------------+
|
| 3. Registra auditoría
v
+----------------------------+
|         Audit Log          |
+----------------------------+
|
| 4. Publica evento
v
+----------------------------+
|        Redis Pub/Sub       |
+----------------------------+
|
| 5. Notifica a WebSockets
v
+----------------------------+
|       WebSocket Server     |
+----------------------------+
|
| 6. SDK recibe actualización
v
+----------------------------+
|     FlagSentinel SDK       |
+----------------------------+
|
| 7. Invalida cache local
v
+----------------------------+
|   Aplicaciones Cliente     |
+----------------------------+

