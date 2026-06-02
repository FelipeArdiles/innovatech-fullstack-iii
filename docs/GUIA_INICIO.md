# Guía de inicio – Innovatech Fullstack III

Esta guía explica cómo levantar el proyecto y abrirlo en el navegador. Está pensada para quienes empiezan con el repositorio.

## Ramas del repositorio

| Rama | Estado | ¿Qué necesitas? |
|------|--------|-----------------|
| **`master`** | Rama principal estable | Suficiente para correr frontend + backend (login, API REST, Eureka). |
| **`feature/unit-tests-coverage`** | Igual que `master` hoy | No hace falta mergearla para arrancar; solo añade/mejorará tests. |
| **`feature/swagger-openapi`** | Por delante de `master` | **Opcional.** Incluye SpringDoc y rutas `/docs/**` en el gateway. Si quieres Swagger en el navegador, haz merge o trabaja en esa rama antes de seguir la sección [Swagger](#swagger-opcional-rama-featureswagger-openapi). |

Recomendación: clona o actualiza `master` para el flujo normal. Integra `feature/swagger-openapi` solo si necesitas documentación interactiva OpenAPI.

```bash
git checkout master
git pull   # si tienes remoto configurado
```

---

## Prerrequisitos

Instala lo siguiente en tu máquina:

| Herramienta | Versión recomendada | Para qué |
|-------------|---------------------|----------|
| **Docker Desktop** (o Docker Engine + Compose v2) | Reciente | Opción A – levantar backend en contenedores |
| **Java JDK** | **17** (el proyecto usa Java 17 en los `pom.xml`) | Opción B – microservicios locales |
| **Node.js** | **20+** | Frontend React (Vite) |
| **npm** | Incluido con Node | Dependencias del frontend |

No es obligatorio instalar Maven globalmente: cada módulo Java incluye **`./mvnw`** (Maven Wrapper).

Comprueba versiones:

```bash
docker compose version
java -version    # debe mostrar 17
node -v          # v20.x o superior
```

---

## Puertos y URLs de referencia

| Servicio | Puerto (host) | URL útil |
|----------|---------------|----------|
| Frontend (Vite) | **5173** | http://localhost:5173 |
| API Gateway | **8080** | http://localhost:8080 |
| Keycloak | **8180** | http://localhost:8180 |
| Eureka Server | **8761** | http://localhost:8761 |
| BFF (solo local) | 8081 | http://localhost:8081 |
| MS Usuarios (solo local) | 8082 | http://localhost:8082 |
| MS Proyectos (solo local) | 8083 | http://localhost:8083 |

Con **Docker Compose**, solo se publican **8180**, **8761** y **8080** hacia tu PC. El BFF y los microservicios quedan en la red interna de Docker.

---

## Opción A – Docker Compose (recomendada)

Levanta Keycloak, Eureka, microservicios, BFF y API Gateway en contenedores. El frontend se ejecuta en tu máquina (es lo habitual en desarrollo).

### 1. Ir a la raíz del proyecto

```bash
cd "/ruta/a/fullstack III Innovatech"
```

Sustituye la ruta por la carpeta real del repositorio en tu equipo.

### 2. (Opcional) Variables de entorno

Para el frontend bastará con `frontend-app/.env`. El archivo raíz `.env.example` documenta variables del backend si más adelante personalizas algo:

```bash
cp .env.example .env          # opcional, referencia
```

### 3. Levantar el backend

```bash
docker compose up --build
```

- La **primera vez** puede tardar varios minutos (descarga de imágenes y compilación Maven en Docker).
- Espera a ver en los logs que **Keycloak** y **Eureka** pasan el healthcheck y que **api-gateway** arranca sin errores.
- Orden aproximado de arranque (automático por `depends_on`):
  1. Keycloak (~30–90 s)
  2. Eureka (~40–60 s tras Keycloak estable)
  3. `ms-usuarios`, `ms-proyectos`
  4. `bff-gateway`
  5. `api-gateway` en el puerto **8080**

Deja esta terminal abierta mientras desarrollas.

### 4. Comprobar el backend

Abre en el navegador:

- Eureka (instancias registradas): http://localhost:8761  
- Keycloak (realm importado): http://localhost:8180/realms/innovatech  
- Salud del gateway (si expone actuator): http://localhost:8080/actuator/health  

En Eureka deberían aparecer, tras un minuto, instancias como `BFF-GATEWAY`, `MS-USUARIOS`, `MS-PROYECTOS`, `API-GATEWAY`.

### 5. Levantar el frontend

En **otra terminal**:

```bash
cd frontend-app
cp .env.example .env
npm install
npm run dev
```

Vite mostrará la URL local (por defecto **http://localhost:5173**).

### 6. Entrar en la aplicación

1. Abre http://localhost:5173  
2. Serás redirigido al login de Keycloak.  
3. Usa el usuario de prueba (ver tabla más abajo).  
4. Tras iniciar sesión verás el dashboard de Innovatech consumiendo la API en http://localhost:8080.

---

## Opción B – Todo en local (sin Compose para el backend)

Útil si quieres depurar Java en el IDE. Necesitas **Docker solo para Keycloak** (o un Keycloak instalado por tu cuenta con el mismo realm).

### Orden de arranque (terminales separadas)

| Paso | Servicio | Comando |
|------|----------|---------|
| 1 | **Keycloak** | Ver bloque siguiente |
| 2 | **Eureka** | `cd eureka-server && ./mvnw spring-boot:run` |
| 3 | **MS Usuarios** | `cd ms-usuarios && ./mvnw spring-boot:run` |
| 4 | **MS Proyectos** | `cd ms-proyectos && ./mvnw spring-boot:run` |
| 5 | **BFF** | `cd bff-gateway && ./mvnw spring-boot:run` |
| 6 | **API Gateway** | `cd api-gateway && ./mvnw spring-boot:run` |
| 7 | **Frontend** | `cd frontend-app && cp .env.example .env && npm install && npm run dev` |

Espera a que cada servicio termine de arrancar antes del siguiente (sobre todo Keycloak y Eureka). Entre el paso 2 y el 6, deja **30–60 segundos** para que los microservicios se registren en Eureka.

### Keycloak en Docker (paso 1)

Desde la raíz del proyecto:

```bash
docker run --rm -p 8180:8180 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  -e KC_HTTP_PORT=8180 \
  -e KC_HOSTNAME=localhost \
  -e KC_HOSTNAME_PORT=8180 \
  -e KC_HOSTNAME_STRICT=false \
  -e KC_HOSTNAME_STRICT_HTTPS=false \
  -e KC_HTTP_ENABLED=true \
  -v "$(pwd)/docker/keycloak/realm-innovatech.json:/opt/keycloak/data/import/realm-innovatech.json" \
  quay.io/keycloak/keycloak:26.0 start-dev --import-realm
```

El realm `innovatech` se importa al iniciar.

### Variables útiles (local)

Los `application.yml` ya traen valores por defecto. Si hace falta, exporta antes de arrancar los servicios Java:

```bash
export EUREKA_URL=http://localhost:8761/eureka/
export KEYCLOAK_ISSUER_URI=http://localhost:8180/realms/innovatech
```

El frontend usa `frontend-app/.env` (copiar desde `.env.example`).

---

## Login en Keycloak

Configuración definida en `docker/keycloak/realm-innovatech.json` y en `frontend-app/.env.example`:

| Concepto | Valor |
|----------|-------|
| **URL** | http://localhost:8180 |
| **Realm** | `innovatech` |
| **Client ID (frontend)** | `innovatech-frontend` |
| **Usuario demo** | `demo` / `demo123` |
| **Usuario admin (realm)** | `admin` / `admin123` |

**Consola de administración de Keycloak** (usuario maestro del contenedor, no del realm):

- URL: http://localhost:8180/admin  
- Usuario: `admin`  
- Contraseña: `admin` (la que define `KC_BOOTSTRAP_ADMIN_PASSWORD` en `docker-compose.yml`)

El cliente `innovatech-frontend` es público, con PKCE y redirecciones a `http://localhost:5173/*`.

---

## URLs finales en el navegador

| Qué | URL |
|-----|-----|
| **Aplicación web** | http://localhost:5173 |
| **API REST (con JWT)** | http://localhost:8080/api/... |
| **Eureka** | http://localhost:8761 |
| **Keycloak (cuenta)** | http://localhost:8180/realms/innovatech/account |

Ejemplos de API (requieren cabecera `Authorization: Bearer <token>`):

- `GET http://localhost:8080/api/dashboard`
- `GET http://localhost:8080/api/usuarios`
- `GET http://localhost:8080/api/proyectos`

Obtener un token por línea de comandos (usuario `demo`):

```bash
curl -s -X POST "http://localhost:8180/realms/innovatech/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=innovatech-frontend" \
  -d "username=demo" \
  -d "password=demo123"
```

---

## Swagger (opcional, rama `feature/swagger-openapi`)

En **`master`** no hay rutas `/docs` ni SpringDoc en los microservicios. Tras mergear o cambiar a `feature/swagger-openapi`:

| Servicio | Swagger UI (directo, solo en local) | Vía API Gateway |
|----------|-------------------------------------|-----------------|
| BFF | http://localhost:8081/swagger-ui/index.html | http://localhost:8080/docs/bff/swagger-ui/index.html |
| MS Usuarios | http://localhost:8082/swagger-ui/index.html | http://localhost:8080/docs/ms-usuarios/swagger-ui/index.html |
| MS Proyectos | http://localhost:8083/swagger-ui/index.html | http://localhost:8080/docs/ms-proyectos/swagger-ui/index.html |

Las rutas `/docs/**` del gateway están pensadas para usarse **sin JWT**; las rutas `/api/**` sí exigen token.

---

## Solución de problemas frecuentes

### Puerto ya en uso

Algún servicio anterior sigue escuchando. En macOS/Linux:

```bash
lsof -i :8080
lsof -i :8180
lsof -i :5173
```

Detén el proceso conflictivo o cambia el puerto (en local vía `SERVER_PORT`; en Docker edita `docker-compose.yml` con cuidado).

### Error de CORS en el navegador

El API Gateway solo permite origen **http://localhost:5173** y **http://127.0.0.1:5173**. Abre el frontend con una de esas URLs exactas, no con otro host ni puerto.

### HTTP 401 en `/api/**`

- Sesión de Keycloak expirada: cierra sesión y vuelve a entrar en http://localhost:5173.  
- Token inválido al probar con curl/Postman: obtén uno nuevo con el endpoint de token.  
- En Docker, el gateway usa `KEYCLOAK_ISSUER_URI=http://localhost:8180/realms/innovatech` (correcto para peticiones desde tu navegador en el host).

### Servicios no aparecen en Eureka

1. Confirma que Eureka responde: http://localhost:8761  
2. Arranca **Eureka antes** que BFF y microservicios.  
3. Espera 30–60 s y refresca el panel.  
4. Revisa que `EUREKA_URL` sea `http://localhost:8761/eureka/` en desarrollo local.

### Keycloak no muestra el realm `innovatech`

- El import solo corre al **primer arranque** con el volumen del JSON. Si el contenedor ya existió sin import, elimina el contenedor/volumen de Keycloak y vuelve a `docker compose up`.  
- Comprueba que el archivo `docker/keycloak/realm-innovatech.json` esté montado correctamente.

### `docker compose` falla al construir

- Verifica Docker en ejecución y espacio en disco.  
- Reintenta: `docker compose build --no-cache` y luego `docker compose up`.

### Frontend no conecta con la API

- Archivo `frontend-app/.env` con `VITE_API_URL=http://localhost:8080`.  
- Tras cambiar `.env`, **reinicia** `npm run dev` (Vite lee variables al arrancar).  
- Comprueba que el gateway esté arriba: http://localhost:8080/actuator/health

---

## Resumen rápido (Docker + frontend)

```bash
# Terminal 1 – raíz del repo
docker compose up --build

# Terminal 2 – cuando el backend esté listo
cd frontend-app && cp .env.example .env && npm install && npm run dev
```

Navegador: http://localhost:5173 → login `demo` / `demo123`.

---

## Más información

- Arquitectura y módulos: [README.md](../README.md)  
- Variables: [.env.example](../.env.example), [frontend-app/.env.example](../frontend-app/.env.example)  
- Realm Keycloak: [docker/keycloak/realm-innovatech.json](../docker/keycloak/realm-innovatech.json)
