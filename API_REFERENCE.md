# 🔌 API Reference — FlagSentinel

Este documento describe los endpoints principales de la API de FlagSentinel.  
La API está diseñada para ser simple, segura y orientada a integraciones profesionales.

---

# 🔐 Autenticación

La API utiliza JWT.  
Todas las peticiones deben incluir:

Authorization: Bearer <token>

---

# 📁 Endpoints Principales

A continuación se listan los endpoints más importantes del sistema.

---

# 🏷️ Feature Flags

## Obtener todos los flags
GET /api/flags

Respuesta:
[
  { "id": "1", "name": "new-ui", "enabled": true }
]

---

## Obtener un flag por ID
GET /api/flags/{id}

Respuesta:
{ "id": "1", "name": "new-ui", "enabled": true }

---

## Crear un flag
POST /api/flags

Body:
{
  "name": "new-ui",
  "description": "Nueva interfaz",
  "enabled": false
}

---

## Actualizar un flag
PUT /api/flags/{id}

Body:
{
  "enabled": true
}

---

## Eliminar un flag
DELETE /api/flags/{id}

---

# 🎯 Reglas

## Obtener reglas de un flag
GET /api/flags/{id}/rules

Respuesta:
[
  {
    "id": "rule-1",
    "priority": 1,
    "conditions": [
      { "attribute": "country", "operator": "EQUALS", "value": "ES" }
    ],
    "rolloutPercentage": 50
  }
]

---

## Crear una regla
POST /api/flags/{id}/rules

Body:
{
  "priority": 1,
  "conditions": [
    { "attribute": "role", "operator": "NOT_EQUALS", "value": "admin" }
  ],
  "rolloutPercentage": 25
}

---

## Actualizar una regla
PUT /api/rules/{ruleId}

Body:
{
  "priority": 2
}

---

## Eliminar una regla
DELETE /api/rules/{ruleId}

---

# 📝 Auditoría

## Obtener logs de auditoría
GET /api/audit

Respuesta:
[
  {
    "id": "log-1",
    "flagId": "1",
    "adminUser": "aaron",
    "action": "UPDATE",
    "timestamp": "2024-01-01T12:00:00Z"
  }
]

---

# 📡 Evaluación de Flags (SDK)

## Evaluar un flag para un usuario
POST /api/evaluate

Body:
{
  "flagName": "new-ui",
  "userId": "123",
  "context": {
    "country": "ES",
    "role": "user"
  }
}

Respuesta:
{
  "enabled": true
}

---

# ⚙️ Códigos de Estado

200 OK — Petición correcta  
201 Created — Recurso creado  
400 Bad Request — Error en los datos enviados  
401 Unauthorized — Token inválido o ausente  
404 Not Found — Recurso no encontrado  
500 Internal Server Error — Error inesperado  

---

# 📌 Notas

- Todos los endpoints devuelven JSON
- Los cambios en flags y reglas disparan eventos en Redis
- El SDK recibe actualizaciones vía WebSockets
