# 🔐 Seguridad — FlagSentinel

FlagSentinel está diseñado con un enfoque claro en la seguridad, garantizando que la gestión de feature flags sea confiable, auditada y protegida frente a accesos no autorizados.

---

# 🔑 Autenticación

La API utiliza autenticación basada en JWT.

- Todas las peticiones deben incluir:
  Authorization: Bearer <token>
- Los tokens tienen expiración configurable
- El Panel solo puede ser usado por administradores autorizados

---

# 🛂 Autorización

El sistema distingue entre:

- Administradores del Panel
- Aplicaciones cliente (SDK)
- Servicios internos

Cada uno tiene permisos distintos:

- Admin → CRUD de flags, reglas y auditoría
- SDK → Evaluación de flags
- Servicios internos → Publicación de eventos

---

# 🧱 Seguridad del Backend

Medidas principales:

- Validación estricta de entrada
- Sanitización de datos
- Protección contra inyección SQL
- Límite de tamaño en payloads
- CORS configurado para dominios permitidos
- HTTPS obligatorio

---

# 📡 Seguridad en WebSockets

- Conexiones autenticadas mediante token
- Revalidación periódica
- Desconexión automática si el token expira
- Solo se envían actualizaciones de flags, nunca datos sensibles

---

# 🗄️ Seguridad en Base de Datos

- Contraseñas y claves cifradas
- Roles separados para lectura/escritura
- Auditoría completa de cambios
- Backups automáticos

---

# 🔄 Seguridad en Redis

- Canales protegidos
- No se envían datos sensibles, solo eventos
- TTL para mensajes temporales

---

# 🧪 Seguridad del SDK

El SDK:

- No almacena datos sensibles
- No expone el token
- Mantiene cache local segura
- Solo evalúa flags, no modifica nada

---

# 📝 Auditoría

Cada cambio en un flag o regla genera un registro:

{
  "flagId": "123",
  "adminUser": "aaron",
  "action": "UPDATE",
  "timestamp": "2024-01-01T12:00:00Z"
}

---

# 🧰 Buenas Prácticas Recomendadas

- Rotar claves periódicamente
- Usar tokens de corta duración
- Limitar acceso al Panel por IP
- Mantener el SDK actualizado
- Revisar auditorías regularmente
