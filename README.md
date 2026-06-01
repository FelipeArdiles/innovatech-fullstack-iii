# Innovatech Solutions – Plataforma Fullstack III

Arquitectura de microservicios para la gestión integral de proyectos tecnológicos.

## Arquitectura

```
[Frontend React :5173]
        │ Bearer JWT
        ▼
[API Gateway :8080] ──valida JWT (Keycloak)──► [Keycloak :8180]
        │ lb://bff-gateway (Eureka)
        ▼
[BFF :8081] ──RestClient + LoadBalancer──► [ms-usuarios :8082]
         └────────────────────────────────► [ms-proyectos :8083]
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

## API REST (vía Gateway)

Base URL: `http://localhost:8080` (requiere `Authorization: Bearer <token>`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/dashboard` | KPIs agregados |
| GET/POST/PUT/DELETE | `/api/usuarios` | CRUD usuarios |
| GET/POST/PUT/DELETE | `/api/proyectos` | CRUD proyectos |

## Pruebas unitarias

```bash
cd eureka-server && ./mvnw test
cd api-gateway && ./mvnw test
cd bff-gateway && ./mvnw test
cd ms-usuarios && ./mvnw test
cd ms-proyectos && ./mvnw test
```

## Módulos

- `eureka-server` – Service discovery
- `api-gateway` – Gateway + JWT
- `bff-gateway` – Backend for Frontend
- `ms-usuarios` – Recursos humanos
- `ms-proyectos` – Gestión de proyectos
- `frontend-app` – React + Keycloak JS
