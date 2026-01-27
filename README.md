# 🛡️ FlagSentinel — Distributed Feature Flag System

FlagSentinel es un sistema distribuido de *feature flags* diseñado para entornos profesionales.  
Permite activar o desactivar funcionalidades en tiempo real, aplicar reglas avanzadas, propagar cambios instantáneamente y evaluar flags localmente mediante un SDK optimizado.

---

## 📦 Módulos del Proyecto

- **flagsentinel-api** — 🛡️ Backend principal (Spring Boot)
- **flagsentinel-panel** — 🌐 Panel administrativo web
- **flagsentinel-sdk-java** — 📦 SDK para clientes Java
- **flagsentinel-demo-app** — 🧪 Aplicación de ejemplo usando el SDK

---

## 🚀 Características Principales

- ⚡ Activación/desactivación en tiempo real  
- 🎯 Reglas avanzadas (país, rol, atributos personalizados, porcentajes)  
- 🔄 Propagación distribuida (Redis Pub/Sub + WebSockets)  
- 🧠 Evaluación local en el SDK (sin llamadas constantes a la API)  
- 📝 Auditoría completa de cambios  
- 🔐 Seguridad basada en JWT  
- 🧱 Arquitectura modular, escalable y preparada para producción  

---

## 📚 Documentación

- [🏗️ Arquitectura](./ARCHITECTURE.md)
- [🧠 Motor de Reglas](./RULE_ENGINE.md)
- [🔌 API Reference](./API_REFERENCE.md)
- [🧪 Ejemplo de Uso](./USAGE_EXAMPLE.md)
- [🔐 Seguridad](./SECURITY.md)
- [🤝 Contribuir](./CONTRIBUTING.md)
- [📜 Changelog](./CHANGELOG.md)

---

## 🧪 Demo

Consulta el archivo **USAGE_EXAMPLE.md** para ver un caso de uso profesional realista.

---

## 🧑‍💻 Licencia

Proyecto creado por **aduque-git**.  
Uso libre para aprendizaje y portfolio.

