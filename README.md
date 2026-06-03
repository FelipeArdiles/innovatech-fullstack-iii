# Innovatech Solutions – Plataforma Fullstack III

Arquitectura de microservicios para la gestión integral de proyectos tecnológicos.

**¿Primera vez con el proyecto?** Sigue la [guía de inicio paso a paso](docs/GUIA_INICIO.md) (Docker Compose o local, login Keycloak y URLs en el navegador).

**Arquitectura y comunicación entre módulos:** [Diagrama de comunicación](docs/DIAGRAMA_COMUNICACION.md) (puertos, JWT, Eureka, Circuit Breaker, flujos login y datos).

## Arquitectura

```
[Frontend React :5173]
        │ Bearer JWT
        ▼
[API Gateway :8080] ──valida JWT (Keycloak)──► [Keycloak :8180]
        │ lb://bff-gateway (Eureka)
        ▼
[BFF :8081] ──RestClient + LoadBalancer──► [ms-usuarios :8082]
         ├────────────────────────────────► [ms-proyectos :8083]
         └────────────────────────────────► [ms-tareas :8084]
                        │
                        ▼
              [Eureka Server :8761]
```

- **API Gateway**: único punto expuesto al exterior; valida tokens OAuth2/OIDC.
- **BFF**: orquesta llamadas a microservicios vía discovery (sin URLs hardcodeadas).
- **Microservicios**: persistencia JPA/PostgreSQL (una BD por MS), no expuestos al cliente.

### Bases de datos (PostgreSQL)

```
                    ┌─────────────────────────────────────┐
                    │  postgres :5432 (volumen persistente) │
                    │  usuario: innovatech                 │
                    └──────────┬──────────────┬────────────┘
                               │              │
              ┌────────────────┼──────────────┼────────────────┐
              ▼                ▼              ▼                │
        DB usuarios      DB proyectos    DB tareas              │
              │                │              │                │
       ms-usuarios       ms-proyectos     ms-tareas             │
```

- **Docker Compose**: servicio `postgres` + script `docker/postgres/init/01-databases.sql` crea `usuarios`, `proyectos` y `tareas` en el primer arranque.
- **Perfil `docker`**: cada MS usa `application-docker.yml` (`jdbc:postgresql://postgres:5432/<db>`).
- **Seed (solo la primera vez)**: cada `DataLoader` carga el dataset demo completo (30 trabajadores, 10 proyectos con finanzas CLP, 47 tareas con dificultad/categoría/valor) **únicamente si** `repository.count() == 0`. Nunca ejecuta `deleteAll()` al arranque.
- **Persistencia de tus cambios**: los datos que creas o editas en la UI quedan en el volumen Docker `innovatech_postgres_data`. `docker compose restart` y `docker compose down` **sin** `-v` conservan la base; solo `docker compose down -v` elimina el volumen y fuerza un nuevo seed en el próximo `up`.
- **Local sin Compose**: levantar Postgres en `localhost:5432` con las mismas credenciales (ver `.env.example`) o usar solo `docker compose up postgres -d` y ejecutar los MS con `./mvnw spring-boot:run`.

## Requisitos

- Java 17+
- Maven 3.9+
- Node.js 20+
- Docker & Docker Compose (opcional)

## Levantar con Docker Compose

```bash
cd "/Users/felipeardiles/Documents/Duoc UC/Proyectos/fullstack III Innovatech"
cp .env.example .env   # opcional; valores por defecto en compose
docker compose up --build
```

> **Datos demo vs. tus datos**: el seed se inserta solo cuando las tablas están vacías (primer arranque o volumen nuevo). Tus altas/edits en la UI persisten en `innovatech_postgres_data`; no uses `docker compose down -v` si quieres conservarlos.

**PostgreSQL (desarrollo)** — copiar de `.env.example`, no commitear `.env` con secretos reales:

| Variable | Valor por defecto |
|----------|-------------------|
| `POSTGRES_USER` | `innovatech` |
| `POSTGRES_PASSWORD` | `innovatech_dev` |
| Host (desde el host) | `localhost:5432` |
| Bases | `usuarios`, `proyectos`, `tareas` |
| Volumen persistente | `innovatech_postgres_data` |

Servicios:
| Componente     | Puerto |
|----------------|--------|
| PostgreSQL     | 5432   |
| Keycloak       | 8180   |
| Eureka         | 8761   |
| API Gateway    | 8080   |
| BFF / MS       | internos |

Frontend (local):

```bash
cd frontend-app
cp .env.example .env
npm install
npm run dev
```

## Levantar en desarrollo local

Orden recomendado (terminales separadas):

```bash
# 1. Keycloak
docker run -p 8180:8180 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  -v "$(pwd)/docker/keycloak/realm-innovatech.json:/opt/keycloak/data/import/realm-innovatech.json" \
  quay.io/keycloak/keycloak:26.0 start-dev --import-realm

# 2. Eureka
cd eureka-server && ./mvnw spring-boot:run

# 3. PostgreSQL (bases usuarios / proyectos / tareas)
docker compose up postgres -d

# 4. Microservicios (requieren Postgres en localhost:5432)
cd ms-usuarios && ./mvnw spring-boot:run
cd ms-proyectos && ./mvnw spring-boot:run
cd ms-tareas && ./mvnw spring-boot:run

# 5. BFF
cd bff-gateway && ./mvnw spring-boot:run

# 6. API Gateway
cd api-gateway && ./mvnw spring-boot:run

# 7. Frontend
cd frontend-app && npm install && npm run dev
```

## Keycloak

| Variable | Valor |
|----------|-------|
| URL | `http://localhost:8180` |
| Realm | `innovatech` |
| Client ID | `innovatech-frontend` |
| Usuario demo | `demo` / `demo123` |
| Admin | `admin` / `admin123` |

Consola admin Keycloak: `http://localhost:8180/admin` (admin/admin)

## Variables de entorno

Ver `.env.example` y `frontend-app/.env.example`.

## Documentación OpenAPI (Swagger UI)

SpringDoc OpenAPI 3. En desarrollo local, con cada servicio levantado:

| Servicio | Swagger UI (directo) | OpenAPI JSON |
|----------|----------------------|--------------|
| BFF | http://localhost:8081/swagger-ui/index.html | http://localhost:8081/v3/api-docs |
| MS Usuarios | http://localhost:8082/swagger-ui/index.html | http://localhost:8082/v3/api-docs |
| MS Proyectos | http://localhost:8083/swagger-ui/index.html | http://localhost:8083/v3/api-docs |
| MS Tareas | http://localhost:8084/swagger-ui/index.html | http://localhost:8084/v3/api-docs |

Vía **API Gateway** (rutas proxy sin JWT; la API `/api/**` sí exige token):

| Servicio | Swagger UI (gateway :8080) |
|----------|------------------------------|
| BFF | http://localhost:8080/docs/bff/swagger-ui/index.html |
| MS Usuarios | http://localhost:8080/docs/ms-usuarios/swagger-ui/index.html |
| MS Proyectos | http://localhost:8080/docs/ms-proyectos/swagger-ui/index.html |

En el BFF, usar **Authorize** con `Bearer <token>` de Keycloak al probar contra `http://localhost:8080` (servidor por defecto en la spec). Los microservicios internos no validan JWT; conviene probarlos en su puerto directo.

Obtener token (ejemplo con usuario `demo` / `demo123`):

```bash
curl -s -X POST "http://localhost:8180/realms/innovatech/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=innovatech-frontend" \
  -d "username=demo" \
  -d "password=demo123" | jq -r .access_token
```

## API REST (vía Gateway)

Base URL: `http://localhost:8080` (requiere `Authorization: Bearer <token>`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/dashboard` | KPIs agregados (usuarios, proyectos, tareas) |
| GET/POST/PUT/DELETE | `/api/usuarios` | CRUD usuarios |
| GET/POST/PUT/DELETE | `/api/trabajadores` | CRUD trabajadores (alias usuarios) |
| GET/POST/PUT/DELETE | `/api/proyectos` | CRUD proyectos |
| GET/POST/PUT/DELETE | `/api/tareas` | CRUD tareas Kanban (`?proyectoId=` opcional) |

## Pruebas unitarias

```bash
cd eureka-server && ./mvnw test
cd api-gateway && ./mvnw test
cd bff-gateway && ./mvnw test
cd ms-usuarios && ./mvnw test
cd ms-proyectos && ./mvnw test
cd ms-tareas && ./mvnw test
```

## Flujo de ramas

El repositorio usa **GitHub Flow simplificado** sobre `master` (rama principal estable):

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| Principal | `master` | Integración y releases |
| Funcionalidad | `feature/<descripcion>` | `feature/swagger-openapi` |
| Corrección | `fix/<descripcion>` | `fix/jwt-expirado` |

**Reglas**

1. Crear ramas desde `master`: `git checkout master && git pull && git checkout -b feature/mi-cambio`
2. Commits en español o bilingüe con prefijo [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `docs:`, `chore:`
3. Integrar vía merge local o pull request hacia `master` (sin push forzado a `master`)
4. No hay rama `develop` ni scripts Git Flow en el repo; convención alineada al historial existente (`git log --oneline`)

## Módulos

- `eureka-server` – Service discovery
- `api-gateway` – Gateway + JWT
- `bff-gateway` – Backend for Frontend
- `ms-usuarios` – Recursos humanos
- `ms-proyectos` – Gestión de proyectos
- `ms-tareas` – Tareas Kanban por proyecto
- `frontend-app` – React + Keycloak JS (Dashboard KPIs, Trabajadores, Proyectos, Tablero Trello)
