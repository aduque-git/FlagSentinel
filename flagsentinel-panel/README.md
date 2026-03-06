# 🌐 FlagSentinel Panel

FlagSentinel Panel es la **interfaz de administración** de la plataforma FlagSentinel.  
Permite gestionar **Feature Flags, reglas y configuración del sistema** mediante una interfaz web conectada a la **FlagSentinel API**.

El panel actúa como **cliente administrativo de la API**, proporcionando herramientas visuales para la gestión de configuración sin necesidad de interactuar directamente con los endpoints REST.

---

# 🎯 Objetivo del Panel

El panel proporciona una interfaz segura para:

- 🚩 Gestionar **Feature Flags**
- ⚙️ Crear y modificar **reglas de activación**
- 🔐 Autenticarse contra la API mediante **JWT**
- 🔄 Consumir los endpoints REST de la plataforma

Todo el panel opera como **cliente HTTP de la API**, delegando la lógica de negocio al backend.

---

# 🏗 Arquitectura

El panel sigue una arquitectura basada en **cliente HTTP + UI server-side**.

Usuario

│

▼
Panel Web (Vaadin UI)

│

▼

API Client Layer

│

▼

FlagSentinel API

│

▼

Base de datos

---


Capas principales:

- **UI Layer** → Componentes Vaadin
- **View Layer** → Pantallas de gestión
- **Service Layer** → Lógica de acceso a la API
- **API Client Layer** → Cliente HTTP centralizado
- **Security Layer** → Gestión de JWT

---

# 🧩 Tecnologías utilizadas

- ☕ **Java**
- 🌱 **Spring Boot**
- 🎨 **Vaadin (UI server-side)**
- 🔐 **Spring Security**
- 🌐 **REST API Client**
- 🧾 **JWT Authentication**

El panel se integra directamente con **FlagSentinel API**, consumiendo sus endpoints REST.

---

# 🔐 Seguridad

La autenticación se realiza mediante **JWT (JSON Web Token)**.

El flujo de seguridad es el siguiente:

Login
│
▼
API devuelve JWT
│
▼
Panel almacena token
│
▼
JWT enviado en cada petición HTTP


Las peticiones a la API incluyen el encabezado:
 - Authorization: Bearer + token


El panel utiliza un **filtro de seguridad JWT** que:

- Extrae el token de la cabecera `Authorization`
- Valida su integridad
- Establece el contexto de seguridad del usuario

Esto permite mantener una arquitectura **stateless**.

---

# 🌐 Cliente HTTP de la API

El acceso a la API se centraliza mediante una capa de cliente HTTP reutilizable.

Características principales:

- 📡 Comunicación mediante **RestTemplate**
- 🔐 Inclusión automática del **JWT en las cabeceras**
- ⚠️ Manejo centralizado de errores
- 📦 Conversión automática de respuestas JSON

La clase base del cliente proporciona:

- Construcción automática de cabeceras
- Manejo de errores de la API
- Conversión de respuestas

---

# 📡 Comunicación con la API

El panel interactúa con los endpoints principales de FlagSentinel:

    GET /rules
    GET /rules/{id}
    POST /flags
    PUT /rules/{id}
    DELETE /user/{id}
    GET /rules/paged
    ...


Todas las operaciones se realizan mediante llamadas HTTP al backend.

---

# ⚠️ Manejo de errores

Los errores de la API se procesan de forma centralizada.

Cuando la API devuelve un error:

1. Se captura la excepción HTTP
2. Se parsea la respuesta JSON
3. Se extrae el mensaje de error
4. Se lanza una excepción interna del panel

Esto permite mostrar mensajes claros al usuario.

Ejemplo de error gestionado:

ApiClientException


Los errores se muestran en la interfaz mediante notificaciones.

---

# 🧠 Gestión de datos y paginación

El panel utiliza un sistema de **paginación controlado por la API**.

Flujo:

Panel solicita página N

│

▼

API devuelve PageResponse

│

▼

Panel renderiza contenido


La respuesta incluye:

- contenido
- número total de elementos
- página actual
- tamaño de página

Esto permite trabajar eficientemente con grandes volúmenes de datos.

---

# 🧱 Componentes reutilizables

El panel implementa componentes reutilizables para operaciones CRUD.

Uno de los componentes centrales es:

AbstractCrudGrid


Este componente proporciona:

- 📊 Grid de datos
- ✏️ Edición inline
- ➕ Creación de registros
- 🗑 Eliminación de registros
- 📄 Paginación integrada
- 🔄 Recarga automática

Las vistas concretas solo deben implementar:

- configuración de columnas
- obtención de datos
- operaciones CRUD

Esto reduce duplicación de código y mantiene la UI consistente.


---

# ▶️ Ejecución del proyecto

## Requisitos

- Java 17+
- Maven
- FlagSentinel API en ejecución

---

## Ejecutar el panel
```bash
mvn spring-boot:run
```

## Compilar y ejecutar

```bash
mvn clean package
java -jar target/flagsentinel-panel.jar
```


---

# 📂 Estructura del proyecto
    config/
    dto/
    exceptions/
    security/
    service/
    ui/
    util/


---

# 🔗 Relación con FlagSentinel API

El panel **no implementa lógica de negocio**.

Su responsabilidad es:

- proporcionar interfaz de administración
- autenticar usuarios
- consumir endpoints de la API
- mostrar datos de forma visual

Toda la lógica de evaluación de flags y reglas reside en **FlagSentinel API**.

---

# 📜 Licencia

Proyecto interno – uso privado.

---
    

