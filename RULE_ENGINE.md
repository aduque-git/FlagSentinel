# 🧠 Motor de Reglas — FlagSentinel

El motor de reglas de FlagSentinel permite decidir si un feature flag debe estar activo o inactivo para un usuario o contexto específico. Está diseñado para ser rápido, extensible y determinista.

---

## 🎯 Objetivo del Motor de Reglas

- Evaluar condiciones dinámicas
- Aplicar porcentajes de rollout
- Ordenar reglas por prioridad
- Permitir condiciones complejas basadas en atributos
- Garantizar resultados consistentes

---

## 🧩 Flujo de Evaluación

Diagrama simplificado:

+----------------------+
|   Solicitud SDK      |
| (userId, context...) |
+----------+-----------+
           |
           v
+----------------------+
|  Obtener FeatureFlag |
+----------+-----------+
           |
           v
+----------------------+
|  Cargar Reglas       |
+----------+-----------+
           |
           v
+----------------------+
| Evaluar Condiciones  |
+----------+-----------+
           |
           v
+----------------------+
| Aplicar Rollout %    |
+----------+-----------+
           |
           v
+----------------------+
| Resultado Final      |
+----------------------+

---

## 🧱 Tipos de Condiciones

### 1. Igualdad
{ "attribute": "country", "operator": "EQUALS", "value": "ES" }

### 2. Distinto
{ "attribute": "role", "operator": "NOT_EQUALS", "value": "admin" }

### 3. Contiene
{ "attribute": "tags", "operator": "CONTAINS", "value": "beta" }

### 4. Mayor / Menor
{ "attribute": "age", "operator": "GREATER_THAN", "value": 18 }

---

## 🎲 Rollout por Porcentaje

Ejemplo:

{ "rolloutPercentage": 25 }

El SDK calcula un hash estable basado en:

userId + flagName

Si el valor cae dentro del 25%, el flag se activa.

---

## 🧠 Orden de Evaluación

1. Se cargan todas las reglas del flag
2. Se ordenan por priority (menor = más importante)
3. Se evalúan una por una
4. La primera regla que cumpla decide el resultado
5. Si ninguna coincide, se usa el valor por defecto

---

## 📝 Ejemplo Completo de Regla

{
  "id": "rule-123",
  "priority": 1,
  "conditions": [
    { "attribute": "country", "operator": "EQUALS", "value": "ES" },
    { "attribute": "role", "operator": "NOT_EQUALS", "value": "admin" }
  ],
  "rolloutPercentage": 50
}

---

## ⚙️ Objetivos del Diseño

- Evaluación rápida
- Determinismo total
- Extensible a nuevos operadores
- Compatible con SDKs en múltiples lenguajes


