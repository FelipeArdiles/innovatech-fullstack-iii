# Diagrama de flujo de petición – Innovatech

Este documento describe el recorrido completo de una petición HTTP desde que el **usuario interactúa en el navegador** hasta que la **respuesta JSON se renderiza en la interfaz**.

| Punto | Descripción |
|-------|-------------|
| **INICIO** | El usuario realiza una acción en la UI (clic, formulario, carga de página). El navegador ejecuta la SPA React, que obtiene el JWT de Keycloak y lanza `fetch` hacia el API Gateway (`VITE_API_URL`, puerto `:8080`). |
| **FIN** | El Frontend recibe el JSON (`200 OK` / `201 Created`), actualiza el estado de React y el navegador muestra los datos al usuario (lista, tarjeta Kanban, formulario confirmado, etc.). |

> **Nota de enrutamiento:** la mayoría de rutas `/api/**` pasan por el **BFF** (p. ej. proyectos, dashboard, usuarios). Las rutas `/api/tareas/**` van **directamente** del Gateway al microservicio `ms-tareas` (orden de ruta `1` en el Gateway), sin pasar por el BFF.

## Diagrama gráfico (SVG)

Flujo visual numerado (ida y vuelta):

![Flujo de petición usuario](assets/flujo-peticion.svg)

Abrir en el navegador: [`docs/assets/flujo-peticion.svg`](assets/flujo-peticion.svg)

---

## Ejemplo 1: `GET /api/proyectos` (listar proyectos)

Flujo típico vía **BFF → ms-proyectos → PostgreSQL**.

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuario
    participant NAV as Navegador
    participant FE as Frontend :5173
    participant KC as Keycloak :8180
    participant AG as API Gateway :8080
    participant EU as Eureka :8761
    participant BFF as BFF Gateway :8081
    participant MSP as ms-proyectos :8083
    participant PG as PostgreSQL :5432

    Note over U,NAV: ▶ INICIO — Usuario en el navegador

    U->>NAV: Abre página Proyectos / clic actualizar
    NAV->>FE: Evento UI (React)
    FE->>FE: refreshToken() + getToken()
    FE->>AG: GET /api/proyectos<br/>Authorization: Bearer JWT (HTTP)
    AG->>KC: Validar firma JWT (JWKS)
    KC-->>AG: Token válido
    AG->>EU: Resolver lb://bff-gateway
    EU-->>AG: Instancia bff-gateway:8081
    AG->>BFF: GET /api/proyectos (HTTP)
    BFF->>EU: Resolver lb://ms-proyectos
    EU-->>BFF: Instancia ms-proyectos:8083
    BFF->>MSP: GET /api/proyectos (HTTP)
    MSP->>PG: SELECT proyectos (JDBC)
    PG-->>MSP: Filas
    MSP-->>BFF: 200 OK — List ProyectoDto (JSON)
    BFF-->>AG: 200 OK — JSON
    AG-->>FE: 200 OK — JSON
    FE->>NAV: setState / render lista
    NAV-->>U: Proyectos visibles en pantalla

    Note over U,NAV: ◀ FIN — Respuesta JSON renderizada en UI
```

---

## Ejemplo 2: `POST /api/tareas` (crear tarea)

El Gateway enruta `/api/tareas/**` **directamente** a `ms-tareas` (sin BFF). El Frontend envía el cuerpo JSON con título, estado, proyecto, etc.

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuario
    participant NAV as Navegador
    participant FE as Frontend :5173
    participant KC as Keycloak :8180
    participant AG as API Gateway :8080
    participant EU as Eureka :8761
    participant BFF as BFF Gateway :8081
    participant MST as ms-tareas :8084
    participant PG as PostgreSQL :5432

    Note over U,NAV: ▶ INICIO — Usuario en el navegador

    U->>NAV: Completa formulario / crea tarjeta Kanban
    NAV->>FE: Submit (React)
    FE->>FE: refreshToken() + getToken()
    FE->>AG: POST /api/tareas + body JSON<br/>Authorization: Bearer JWT (HTTP)
    AG->>KC: Validar firma JWT (JWKS)
    KC-->>AG: Token válido
    Note over BFF: No interviene<br/>(ruta Gateway order=1 → ms-tareas)
    AG->>EU: Resolver lb://ms-tareas
    EU-->>AG: Instancia ms-tareas:8084
    AG->>MST: POST /api/tareas (HTTP + JSON)
    MST->>PG: INSERT tarea (JDBC)
    PG-->>MST: ID generado
    MST-->>AG: 201 Created — TareaDto (JSON)
    AG-->>FE: 201 Created — JSON
    FE->>NAV: Añade tarjeta al tablero
    NAV-->>U: Nueva tarea visible

    Note over U,NAV: ◀ FIN — Respuesta JSON renderizada en UI
```

---

## Resumen de participantes

| Participante | Rol en el flujo |
|--------------|-----------------|
| **Usuario** | Dispara la acción en la UI |
| **Navegador** | Ejecuta la SPA, DOM, red HTTP del cliente |
| **Frontend** | React + Keycloak JS; `authFetch` con Bearer JWT |
| **Keycloak** | Emite JWT (login previo) y firma validada por el Gateway |
| **API Gateway** | Punto de entrada `:8080`; valida JWT y enruta |
| **Eureka** | Service discovery (`lb://`); resuelve instancias |
| **BFF Gateway** | Orquestación HTTP hacia MS (proyectos, dashboard, etc.) |
| **MS** | Microservicio de dominio (`ms-proyectos`, `ms-tareas`, …) |
| **PostgreSQL** | Persistencia JDBC (una BD por microservicio) |

---

Ver también: [Diagrama de comunicación](DIAGRAMA_COMUNICACION.md) · [Guía de inicio](GUIA_INICIO.md) · [README principal](../README.md)
