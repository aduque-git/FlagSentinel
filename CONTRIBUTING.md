# 🤝 Contribuir — FlagSentinel

Gracias por tu interés en contribuir a FlagSentinel.  
Este proyecto sigue una arquitectura modular y un flujo de trabajo sencillo para facilitar la colaboración.

---

# 🧱 Requisitos Previos

Antes de contribuir, asegúrate de tener:

- Git instalado
- Java 17 o superior
- Maven
- Node.js (para el Panel)
- PostgreSQL
- Redis

---

# 🌿 Flujo de Trabajo

1. Haz un fork del repositorio
2. Crea una rama nueva:
   git checkout -b feature/nueva-funcionalidad
3. Realiza tus cambios
4. Asegúrate de que todo compila y pasa los tests
5. Haz commit siguiendo el formato:
   feat: descripción breve
6. Sube la rama:
   git push origin feature/nueva-funcionalidad
7. Abre un Pull Request

---

# 🧪 Tests

Antes de enviar un PR:

- Ejecuta los tests del backend
- Verifica que el Panel compila sin errores
- Comprueba que el SDK funciona con la API local

---

# 📦 Estructura del Proyecto

El repositorio contiene varios módulos:

- flagsentinel-api → Backend
- flagsentinel-panel → Panel web
- flagsentinel-sdk-java → SDK para clientes
- flagsentinel-demo-app → Aplicación de ejemplo

Cada módulo tiene su propio README.

---

# 📝 Estilo de Código

- Java: seguir convenciones de Google Java Style
- JavaScript/TypeScript: ESLint + Prettier
- Commits: formato convencional (feat, fix, refactor, docs…)

Ejemplos:

feat: añadir soporte para rollout por país  
fix: corregir error en evaluación de reglas  

---

# 🔍 Revisiones de Código

Los PR deben cumplir:

- Código limpio y legible
- Comentarios cuando sea necesario
- Sin dependencias innecesarias
- Tests incluidos si aplica
- Documentación actualizada

---

# 🛡️ Seguridad

No se aceptan contribuciones que:

- Expongan datos sensibles
- Debiliten la autenticación
- Introduzcan dependencias inseguras

---

# 📬 Contacto

Si tienes dudas, abre un Issue en GitHub.
