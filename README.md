# Innovatech Solutions – Plataforma Fullstack III

Arquitectura de microservicios para la gestión integral de proyectos tecnológicos.

**¿Primera vez con el proyecto?** Sigue la [guía de inicio paso a paso](docs/GUIA_INICIO.md) (Docker Compose o local, login Keycloak y URLs en el navegador).

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
- **Microservicios**: persistencia JPA/H2, no expuestos al cliente.

## Requisitos

- Java 17+
- Maven 3.9+
- Node.js 20+
- Docker & Docker Compose (opcional)

## Levantar con Docker Compose

```bash
cd "/Users/felipeardiles/Documents/Duoc UC/Proyectos/fullstack III Innovatech"
docker compose up --build
```

Servicios:
| Componente     | Puerto |
|----------------|--------|
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

# 3. Microservicios
cd ms-usuarios && ./mvnw spring-boot:run
cd ms-proyectos && ./mvnw spring-boot:run
cd ms-tareas && ./mvnw spring-boot:run

# 4. BFF
cd bff-gateway && ./mvnw spring-boot:run

# 5. API Gateway
cd api-gateway && ./mvnw spring-boot:run

# 6. Frontend
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
