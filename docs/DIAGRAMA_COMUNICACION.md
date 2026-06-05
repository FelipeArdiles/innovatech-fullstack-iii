# Diagrama de comunicación – Innovatech

Documento de referencia sobre cómo se comunican los módulos del sistema Innovatech Solutions: frontend, autenticación, gateway, BFF, microservicios, service discovery y persistencia.

## Diagrama gráfico (SVG)

Abre en el navegador o en el preview del IDE:

![Arquitectura lógica](assets/arquitectura-logica.svg)

Archivo: [`docs/assets/arquitectura-logica.svg`](assets/arquitectura-logica.svg)

## Vista lógica por capas

```mermaid
flowchart LR
    subgraph L1["1. Usuario"]
        U((Usuario))
    end

    subgraph L2["2. Presentacion"]
        FE[Frontend React]
        KC[Keycloak JWT]
    end

    subgraph L3["3. Borde"]
        AG[API Gateway]
    end

    subgraph L4["4. Orquestacion"]
        BFF[BFF + Circuit Breaker]
    end

    subgraph L5["5. Dominio"]
        MSU[ms-usuarios]
        MSP[ms-proyectos]
        MST[ms-tareas]
    end

    subgraph L6["6. Datos"]
        PG[(PostgreSQL)]
    end

    EU[Eureka]

    U --> FE
    FE --> KC
    FE -->|Bearer JWT| AG
    AG --> BFF
    AG -.->|rutas directas| MST
    AG -.->|trabajadores| MSU
    BFF --> MSU
    BFF --> MSP
    BFF --> MST
    MSU --> PG
    MSP --> PG
    MST --> PG
    BFF -.-> EU
    AG -.-> EU
    MSU -.-> EU
    MSP -.-> EU
    MST -.-> EU
```

## Diagrama de arquitectura

```mermaid
flowchart TB
    subgraph Cliente["Capa cliente"]
        FE["Frontend React<br/>:5173 (dev)"]
    end

    subgraph Auth["Autenticación"]
        KC["Keycloak<br/>:8180<br/>OIDC / JWT"]
    end

    subgraph Gateway["Punto de entrada"]
        AG["API Gateway<br/>:8080<br/>HTTP + validación JWT"]
    end

    subgraph Discovery["Service discovery"]
        EU["Eureka Server<br/>:8761<br/>sin BD"]
    end

    subgraph Orquestacion["Orquestación"]
        BFF["BFF Gateway<br/>:8081<br/>HTTP + Circuit Breaker<br/>sin BD"]
    end

    subgraph Microservicios["Microservicios"]
        MSU["ms-usuarios<br/>:8082"]
        MSP["ms-proyectos<br/>:8083"]
        MST["ms-tareas<br/>:8084"]
    end

    subgraph Persistencia["Persistencia"]
        PG["PostgreSQL<br/>:5432<br/>JDBC"]
        DBU[("DB usuarios")]
        DBP[("DB proyectos")]
        DBT[("DB tareas")]
    end

    FE -->|"HTTP + Bearer JWT"| AG
    FE -->|"Login OIDC (password grant / redirect)"| KC
    AG -->|"Valida JWT (JWKS / issuer-uri)"| KC
    AG -->|"lb://bff-gateway"| BFF
    AG -->|"lb://ms-usuarios<br/>/api/trabajadores/**"| MSU
    AG -->|"lb://ms-tareas<br/>/api/tareas/**"| MST

    BFF -->|"HTTP RestClient + LoadBalancer"| MSU
    BFF -->|"HTTP RestClient + LoadBalancer"| MSP
    BFF -->|"HTTP RestClient + LoadBalancer"| MST

    AG -.->|"registro + consulta"| EU
    BFF -.->|"registro + consulta"| EU
    MSU -.->|"registro + consulta"| EU
    MSP -.->|"registro + consulta"| EU
    MST -.->|"registro + consulta"| EU

    MSU -->|"JDBC"| DBU
    MSP -->|"JDBC"| DBP
    MST -->|"JDBC"| DBT
    DBU --- PG
    DBP --- PG
    DBT --- PG
```

## Puertos y protocolos

| Componente | Puerto (host) | Expuesto al host | Protocolo / tecnología | Notas |
|------------|---------------|------------------|------------------------|-------|
| Frontend React | 5173 | Sí (dev local) | HTTP | `VITE_API_URL`, `VITE_KEYCLOAK_*` en `.env` |
| API Gateway | 8080 | Sí | HTTP + JWT (OAuth2 Resource Server) | Único backend expuesto al navegador |
| BFF Gateway | 8081 | No (solo red Docker) | HTTP + Resilience4j Circuit Breaker | Orquesta llamadas a los MS |
| ms-usuarios | 8082 | No | HTTP + JDBC | BD dedicada `usuarios` |
| ms-proyectos | 8083 | No | HTTP + JDBC | BD dedicada `proyectos` |
| ms-tareas | 8084 | No | HTTP + JDBC | BD dedicada `tareas` |
| Eureka Server | 8761 | Sí | HTTP (REST Eureka) | Service discovery; no persiste datos de negocio |
| Keycloak | 8180 | Sí | HTTP (OIDC) | Emite y valida tokens JWT |
| PostgreSQL | 5432 | Sí | JDBC (`postgresql://`) | Tres bases en una instancia |

### Variables del frontend (`frontend-app/.env.example`)

| Variable | Valor por defecto | Uso |
|----------|-------------------|-----|
| `VITE_API_URL` | `http://localhost:8080` | Base URL del API Gateway |
| `VITE_KEYCLOAK_URL` | `http://localhost:8180` | Servidor Keycloak |
| `VITE_KEYCLOAK_REALM` | `innovatech` | Realm OIDC |
| `VITE_KEYCLOAK_CLIENT_ID` | `innovatech-frontend` | Cliente público del SPA |

## Flujo de login (autenticación)

1. El usuario abre el **Frontend** (`login-required` vía Keycloak JS).
2. Keycloak autentica al usuario en `:8180` (realm `innovatech`, client `innovatech-frontend`).
3. Keycloak devuelve un **access token JWT** al navegador.
4. El Frontend incluye `Authorization: Bearer <token>` en cada petición a `VITE_API_URL` (`:8080`).
5. El **API Gateway** valida el JWT contra Keycloak (`issuer-uri` / `jwk-set-uri` en perfil `docker`).
6. Si el token es válido, el Gateway enruta la petición según las reglas de ruta (BFF, ms-usuarios o ms-tareas).
7. El **BFF** y los **microservicios internos** no revalidan JWT; confían en la red interna y en que solo el Gateway está expuesto.

Rutas públicas en el Gateway (sin JWT): `/actuator/health`, `/actuator/info`, `/docs/**`.

## Flujo de datos (persistencia)

Cada microservicio tiene su propia base de datos PostgreSQL (patrón **database per service**):

| Microservicio | Base de datos | JDBC (Docker) |
|---------------|---------------|---------------|
| ms-usuarios | `usuarios` | `jdbc:postgresql://postgres:5432/usuarios` |
| ms-proyectos | `proyectos` | `jdbc:postgresql://postgres:5432/proyectos` |
| ms-tareas | `tareas` | `jdbc:postgresql://postgres:5432/tareas` |

El script `docker/postgres/init/01-databases.sql` crea las tres bases en el **primer arranque** del volumen `innovatech_postgres_data`. Credenciales compartidas: usuario `innovatech` (ver `.env.example`).

## Eureka: registro de servicios

| Servicio | `spring.application.name` | Se registra en Eureka |
|----------|---------------------------|------------------------|
| Eureka Server | `eureka-server` | **No** (`register-with-eureka: false`) |
| API Gateway | `api-gateway` | **Sí** |
| BFF Gateway | `bff-gateway` | **Sí** |
| ms-usuarios | `ms-usuarios` | **Sí** |
| ms-proyectos | `ms-proyectos` | **Sí** |
| ms-tareas | `ms-tareas` | **Sí** |
| Keycloak | — | No |
| Frontend | — | No |
| PostgreSQL | — | No |

El Gateway y el BFF resuelven destinos con `lb://<nombre-servicio>` (LoadBalancer + Eureka). En Docker, el perfil `docker` del Gateway también puede usar URLs directas (`http://bff-gateway:8081`, etc.).

## Rutas del API Gateway

Orden de evaluación (menor `order` = mayor prioridad):

| Orden | Ruta entrante | Destino | Filtro |
|-------|---------------|---------|--------|
| 0 | `/api/trabajadores/**` | `ms-usuarios` | Reescribe a `/api/usuarios/**` |
| 1 | `/api/tareas/**` | `ms-tareas` | Sin reescritura (acceso directo al MS) |
| 2 | `/api/**` | `bff-gateway` | Dashboard, proyectos, usuarios vía BFF, etc. |
| — | `/docs/bff/**` | `bff-gateway` | Swagger UI del BFF |
| — | `/docs/ms-usuarios/**` | `ms-usuarios` | Swagger UI MS usuarios |
| — | `/docs/ms-proyectos/**` | `ms-proyectos` | Swagger UI MS proyectos |

## Circuit Breaker en el BFF

El BFF usa **Resilience4j** (`@CircuitBreaker`) en `MicroserviceClient` con tres instancias:

| Instancia | Microservicio | Parámetros |
|-----------|---------------|------------|
| `msUsuarios` | ms-usuarios | ventana 10, umbral fallo 50 %, abierto 10 s |
| `msProyectos` | ms-proyectos | ventana 10, umbral fallo 50 %, abierto 10 s |
| `msTareas` | ms-tareas | ventana 10, umbral fallo 50 %, abierto 10 s |

Cada operación HTTP tiene **fallback**: listas vacías o `null`, de modo que el BFF puede degradar respuestas agregadas (p. ej. dashboard con ceros) si un MS no responde.

## Componentes sin base de datos

Estos módulos **no** usan PostgreSQL ni JPA:

- **API Gateway** — enrutamiento y seguridad JWT únicamente.
- **Eureka Server** — registro en memoria de instancias.
- **BFF Gateway** — agregación en memoria; no persiste entidades.
- **Frontend** — SPA estático; estado en el navegador.

Keycloak gestiona sus propios datos de identidad (internos al contenedor); no comparte las BD `usuarios` / `proyectos` / `tareas`.

---

## Diagrama de secuencia: `GET /api/dashboard`

Ejemplo de lectura agregada: el BFF consulta los tres microservicios y consolida KPIs.

```mermaid
sequenceDiagram
    actor U as Usuario
    participant FE as Frontend :5173
    participant KC as Keycloak :8180
    participant AG as API Gateway :8080
    participant EU as Eureka :8761
    participant BFF as BFF :8081
    participant MSU as ms-usuarios :8082
    participant MSP as ms-proyectos :8083
    participant MST as ms-tareas :8084
    participant PG as PostgreSQL :5432

    Note over U,KC: Login previo (OIDC)
    U->>FE: Abre aplicación
    FE->>KC: init / login-required
    KC-->>FE: access_token (JWT)

    U->>FE: Ver dashboard
    FE->>AG: GET /api/dashboard<br/>Authorization: Bearer JWT
    AG->>KC: Validar firma JWT (JWKS)
    KC-->>AG: Token válido
    AG->>EU: Resolver lb://bff-gateway
    EU-->>AG: Instancia bff-gateway:8081
    AG->>BFF: GET /api/dashboard

    BFF->>EU: Resolver ms-usuarios
    BFF->>MSU: GET /api/usuarios<br/>(CircuitBreaker msUsuarios)
    MSU->>PG: SELECT (JDBC usuarios)
    PG-->>MSU: Filas
    MSU-->>BFF: List UsuarioDto

    BFF->>EU: Resolver ms-proyectos
    BFF->>MSP: GET /api/proyectos<br/>(CircuitBreaker msProyectos)
    MSP->>PG: SELECT (JDBC proyectos)
    PG-->>MSP: Filas
    MSP-->>BFF: List ProyectoDto

    BFF->>EU: Resolver ms-tareas
    BFF->>MST: GET /api/tareas<br/>(CircuitBreaker msTareas)
    MST->>PG: SELECT (JDBC tareas)
    PG-->>MST: Filas
    MST-->>BFF: List TareaDto

    Note over BFF: Agrega KPIs<br/>(conteos por estado, listas)
    BFF-->>AG: DashboardDto (JSON)
    AG-->>FE: 200 OK
    FE-->>U: Renderiza dashboard
```

Si algún microservicio falla de forma repetida, el Circuit Breaker correspondiente devuelve el fallback (lista vacía) y el dashboard se calcula con los datos disponibles.

---

Ver también: [Flujo de petición usuario → UI](DIAGRAMA_FLUJO_PETICION.md) · [Guía de inicio](GUIA_INICIO.md) · [README principal](../README.md)
