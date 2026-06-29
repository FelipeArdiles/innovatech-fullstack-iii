# Guion de Grabación - Innovatech Solutions

*Nota: Este documento contiene el texto a narrar por la IA de TTS. Los bloques marcados con **🎬 MOSTRAR EN PANTALLA** son indicaciones visuales con accesos directos (links) para que sepas qué archivo abrir o qué mostrar en ese momento exacto del video. Recuerda omitir esas líneas al copiar el texto para la IA.*

---

## PARTE 1 — VIDEO DE ARQUITECTURA

### Apertura
> **🎬 MOSTRAR EN PANTALLA:** Logo/título del proyecto, repositorio GitHub y el diagrama [arquitectura-logica.svg](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/docs/assets/arquitectura-logica.svg)

Hola, soy [tu nombre]. En este video presento la arquitectura de Innovatech Solutions, una plataforma fullstack para gestión integral de proyectos tecnológicos, desarrollada como entrega del curso Fullstack III. El sistema resuelve la problemática de coordinar equipos, proyectos, tareas, finanzas y comunicación en una empresa de desarrollo de software, usando microservicios, autenticación centralizada y un frontend moderno.

### I. Arquitectura de Componentes y Diseño Global

**Pregunta 1**
> **🎬 MOSTRAR EN PANTALLA:** Pantalla de inicio del sistema, dashboard admin y panel trabajador.

El caso de negocio es Innovatech Solutions, una consultora tecnológica que gestiona múltiples proyectos de innovación con equipos distribuidos. La problemática concreta es:
Primero, la falta de visibilidad: los gerentes no saben en tiempo real cuántas tareas están pendientes, en progreso o completadas, ni si los proyectos están atrasados.
Segundo, el acoplamiento y escalabilidad: un monolito dificultaría escalar solo el módulo de tareas o el de notificaciones cuando crece la carga.
Tercero, permisos diferenciados: un administrador necesita ver finanzas, capacidad del equipo y tener control completo; un trabajador solo debe ver sus proyectos, sus tareas y poder comentar, sin acceso a datos sensibles.
Y cuarto, comunicación dispersa: los avisos de proyecto, asignaciones de tareas y plazos no llegan de forma centralizada al colaborador.
Nuestra solución digitaliza el portafolio de proyectos, el tablero Kanban, las finanzas en pesos chilenos, la capacidad horaria del equipo, la membresía de proyectos, comentarios en tareas y un sistema de notificaciones por evento.

**Pregunta 2**
> **🎬 MOSTRAR EN PANTALLA:** [arquitectura-logica.svg](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/docs/assets/arquitectura-logica.svg) y sección de Arquitectura en el [README.md](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/README.md)

Descompusimos el sistema por subdominios de negocio, alineados con DDD:
Identidad y equipo en ms-usuarios, que maneja trabajadores, roles, capacidad horaria y sueldos.
Portafolio en ms-proyectos, para proyectos, estados, finanzas de contrato y miembros del equipo.
Ejecución en ms-tareas, para tareas Kanban, asignación, dificultad y categoría.
Y Comunicación en ms-notificaciones, para comentarios y alertas.
Elegimos microservicios porque cada dominio escala de forma independiente; reducimos el acoplamiento evitando que un cambio en finanzas despliegue el módulo de tareas; cada servicio tiene su propia base de datos, evitando un esquema monolítico compartido; y equipos distintos podrían mantener cada microservicio en el futuro.
La orquestación la hace un BFF, o Backend for Frontend, que agrega datos para el dashboard, finanzas y panel del trabajador. El API Gateway es el único punto de entrada HTTP hacia el backend.

**Pregunta 3**
> **🎬 MOSTRAR EN PANTALLA:** [docker-compose.yml](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/docker-compose.yml) haciendo scroll por cada servicio.

En cuanto a los componentes y sus puertos:
Frontend React con Vite en el puerto 5173, una SPA moderna con integración nativa con Keycloak JS.
Keycloak en el puerto 8180, estándar OIDC y OAuth2; emite JWT, gestiona usuarios y roles, y evita implementar autenticación propia.
API Gateway en el puerto 8080, único punto expuesto; valida el JWT, enruta a BFF o a los microservicios, y centraliza el CORS.
Eureka Server en el puerto 8761, funciona como Service Discovery; facilita Docker y escalado sin IPs fijas.
BFF Gateway en el puerto 8081, agrega KPIs, finanzas y notificaciones; incluye Circuit Breaker y adapta la API al frontend.
ms-usuarios en el puerto 8082, maneja el CRUD de trabajadores y validación de horas.
ms-proyectos en el puerto 8083, maneja proyectos, miembros y montos.
ms-tareas en el puerto 8084, para las tareas y estados Kanban.
ms-notificaciones en el puerto 8085, para comentarios persistentes y alertas.
PostgreSQL en el puerto 5432, persistencia ACID con cuatro bases de datos en un solo volumen.

**Pregunta 4**
> **🎬 MOSTRAR EN PANTALLA:** Abrir [arquitectura-logica.svg](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/docs/assets/arquitectura-logica.svg) en el navegador y señalar cada bloque.

En el diagrama de arquitectura lógica se ven:
La capa cliente con el panel de Administrador y Trabajador.
La flecha de login hacia Keycloak.
La flecha HTTP con Bearer JWT del frontend al API Gateway.
El Gateway comunicándose hacia el BFF para la mayoría de las rutas.
Las rutas punteadas directas hacia ms-usuarios y ms-tareas.
El BFF comunicándose hacia los 4 microservicios vía RestClient y Eureka.
Cada microservicio hacia su base de datos JDBC.
Eureka representado en línea discontinua verde para el registro de instancias.
Y un bloque inferior con los tipos de notificación que genera el BFF.

### Service Discovery

**Pregunta 5**
> **🎬 MOSTRAR EN PANTALLA:** Navegador en `http://localhost:8761` (Dashboard Eureka).

Eureka actúa como un registro dinámico: cada microservicio, al arrancar, se registra con su nombre lógico y su IP o puerto actual. Los consumidores como el API Gateway o el BFF no conocen la dirección local explícita, sino que consultan Eureka y obtienen la instancia disponible. Es como una agenda telefónica que se actualiza sola cuando un contenedor sube o baja.

**Pregunta 6**
Sin Eureka tendríamos URLs configuradas manualmente en el código. En Docker las IPs cambian en cada despliegue. Eureka nos permite usar balanceador de carga en las rutas del Gateway, clientes REST balanceados en el BFF, agregar réplicas del mismo microservicio en el futuro sin cambiar el código, y además realiza health checks, dejando de enviar tráfico a instancias caídas.

**Pregunta 7**
> **🎬 MOSTRAR EN PANTALLA:** El application.yml de Eureka en [application.yml](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/eureka-server/src/main/resources/application.yml) y la configuración equivalente de un cliente, por ejemplo en [application.yml (ms-usuarios)](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-usuarios/src/main/resources/application.yml).

Cómo funciona: cada cliente se configura para registrarse y buscar en Eureka. El servidor Eureka en sí no se registra a sí mismo.
Lo bueno: brinda mucha simplicidad para equipos Spring, se integra nativamente con Spring Cloud LoadBalancer y tiene un dashboard visual.
Lo malo: Eureka es más propio del ecosistema Netflix y Spring; en producción muchos migran a Kubernetes DNS o Consul; tiene latencia en la propagación del registro, y si Eureka cae, los clientes dependen de una caché local temporal para funcionar.

**Pregunta 8**
> **🎬 MOSTRAR EN PANTALLA:** El archivo yml local de Eureka [application.yml](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/eureka-server/src/main/resources/application.yml) y el bloque de `environment` de las variables EUREKA_URL en el [docker-compose.yml](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/docker-compose.yml).

Para cambiar el puerto de Eureka, editamos el archivo application yml. Para los clientes, usamos la variable de entorno EUREKA_URL en el archivo de docker compose. Para desactivar la auto preservación en desarrollo modificamos enable self preservation a false, lo cual ya está en nuestro proyecto. Tras hacer los cambios, se reconstruye y levanta con docker compose.

**Pregunta 9**
> **🎬 MOSTRAR EN PANTALLA:** Clase principal de servidor (EurekaServerApplication.java) y de algún cliente (MsUsuariosApplication.java).

Sí. Usamos el patrón de Self-Registration: cada microservicio se registra por su cuenta al iniciar, sin un agente externo. Se utiliza la anotación EnableEurekaClient y se identifica con el nombre de la aplicación. El servidor tiene su propia anotación EnableEurekaServer.

**Pregunta 10**
> **🎬 MOSTRAR EN PANTALLA:** Carpeta `eureka-server/` en tu editor y su dependencia correspondiente dentro del [pom.xml](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/eureka-server/pom.xml).

Eureka Server es un módulo dentro de nuestro repositorio, no se descarga por separado. Se utiliza a través de la dependencia de Spring Cloud Netflix Eureka vía Maven en nuestro pom xml. El repositorio completo está disponible en GitHub.

**Pregunta 11**
> **🎬 MOSTRAR EN PANTALLA:** Terminal corriendo `docker compose up` y el dashboard Eureka activo en el navegador.

Utilizar Docker Compose es la opción recomendada: el comando docker compose up build levanta todo el ecosistema, incluido Eureka en el puerto 8761. Si se desea levantar solo Eureka se especifica el servicio. De forma local sin Docker, se navega a la carpeta y se usa el comando maven spring boot run. Para verificar, simplemente se abre el navegador en el puerto 8761 y se ve el dashboard con las instancias activas.

### Microservicios

**Pregunta 12**
> **🎬 MOSTRAR EN PANTALLA:** Archivos `@Entity` dentro de la carpeta `entity/` de cada microservicio en tu IDE.

El microservicio de usuarios cubre la gestión del capital humano: identifica quiénes son los trabajadores, qué rol desempeñan a partir de un catálogo fijo de roles tecnológicos, sus horas semanales disponibles y su sueldo mensual para cálculos financieros.
El microservicio de proyectos cubre el portafolio: gestionando los proyectos, estados, fechas, presupuestos y la relación de qué trabajadores pertenecen al equipo de cada proyecto.
El microservicio de tareas cubre la ejecución operativa, con detalles como título, descripción, estado en el tablero Kanban, asignación de usuarios, dificultad, categoría y horas estimadas.
El microservicio de notificaciones cubre la comunicación y alertas: guardando comentarios en las tareas y generando notificaciones en la aplicación por tipo de evento.

**Pregunta 13**
> **🎬 MOSTRAR EN PANTALLA:** El archivo enum del catálogo y el archivo [BffService.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/bff-gateway/src/main/java/cl/innovatech/bff_gateway/service/BffService.java).

En ms-usuarios el rol debe pertenecer al catálogo y la capacidad horaria debe estar entre 20 y 45 horas semanales.
En ms-proyectos se evita que un trabajador sea agregado dos veces al mismo proyecto, y los estados controlan si se marca como atrasado.
En ms-tareas, los estados deben ser válidos para un flujo Kanban, y se disparan notificaciones al cambiar asignaciones o completar tareas difíciles.
En ms-notificaciones, cada comentario exige un ID de tarea y su texto.
Y en el BFF, el cálculo de las finanzas y los márgenes obedece a reglas de negocio centralizadas, al igual que las alertas de plazos por vencer.

**Pregunta 14**
> **🎬 MOSTRAR EN PANTALLA:** Seguir el flujo en IDE: [UsuarioController.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-usuarios/src/main/java/cl/innovatech/ms_usuarios/controller/UsuarioController.java) → [UsuarioService.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-usuarios/src/main/java/cl/innovatech/ms_usuarios/service/UsuarioService.java) → [UsuarioRepository.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-usuarios/src/main/java/cl/innovatech/ms_usuarios/repository/UsuarioRepository.java). Luego mostrar `getDashboard()` en el [BffService.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/bff-gateway/src/main/java/cl/innovatech/bff_gateway/service/BffService.java).

El flujo típico de procesamiento es: el Controller recibe un objeto JSON, el Service aplica la lógica de negocio y el Repository de Spring Data JPA lo persiste en la base de datos PostgreSQL. Además, el BFF lee datos de varios microservicios de manera simultánea, los combina en DTOs y calcula métricas e indicadores en la memoria, como contabilizar tareas para el dashboard o sumar las horas de la capacidad del equipo.

**Pregunta 15**
> **🎬 MOSTRAR EN PANTALLA:** [TrabajadorValidationAdvice.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-usuarios/src/main/java/cl/innovatech/ms_usuarios/validation/TrabajadorValidationAdvice.java) y mostrar el navegador en el formulario de creación de trabajador para ver el input del rol.

En el microservicio de usuarios existe un manejador de excepciones que atrapa errores de validación, devolviendo un error 400 con un mensaje descriptivo. En los proyectos se valida la existencia del trabajador. En el API Gateway, la validación del token JWT rechaza peticiones expiradas o inválidas con un código 401. Y en la capa del cliente, el frontend también efectúa sus propias validaciones en los formularios.

**Pregunta 16**
> **🎬 MOSTRAR EN PANTALLA:** Swagger UI ejecutándose en `http://localhost:8081/swagger-ui/index.html` o la tabla de endpoints en el [README.md](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/README.md).

Los casos de uso por microservicio son:
En usuarios: CRUD del trabajador y consultar capacidad y sueldos.
En proyectos: CRUD de proyectos y administrar miembros.
En tareas: CRUD de tareas, filtrado y modificación de su estado en el Kanban.
En notificaciones: listado y creación de comentarios, y gestión de notificaciones del sistema.
Y en el BFF: la consolidación de información para el dashboard de administrador, el panel de trabajador, cálculos financieros y publicación de avisos.

**Pregunta 17**
> **🎬 MOSTRAR EN PANTALLA:** Desplegar el árbol de carpetas de tu IDE para mostrar los módulos `ms-usuarios` y `bff-gateway`.

Todos los microservicios siguen la estructura estándar de Maven, con su clase principal de aplicación y paquetes bien definidos para configuraciones, controladores REST, entidades de bases de datos, repositorios y servicios con la lógica. Además contienen sus configuraciones en archivos YAML. El BFF incluye adicionalmente clientes para orquestar la comunicación con los demás microservicios.

**Pregunta 18**
> **🎬 MOSTRAR EN PANTALLA:** Archivo [pom.xml de ms-tareas](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-tareas/pom.xml) y [pom.xml del bff-gateway](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/bff-gateway/pom.xml).

Las dependencias fundamentales usadas en los microservicios son las de Spring Boot para Web y API REST, Spring Data JPA para la base de datos, el conector de PostgreSQL, el cliente de Eureka, el Actuator para monitorear la salud de los servicios, y Springdoc para generar la documentación Swagger. Adicionalmente, el BFF incluye el cliente balanceador de cargas y Resilience4j para el Circuit Breaker; mientras que el Gateway utiliza las dependencias de Gateway reactivo y el servidor de recursos OAuth2 para seguridad.

**Pregunta 19**
> **🎬 MOSTRAR EN PANTALLA:** Abrir [UsuarioController.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-usuarios/src/main/java/cl/innovatech/ms_usuarios/controller/UsuarioController.java) (u otro) y apuntar a las anotaciones `@GetMapping` y `@PostMapping`.

Cada microservicio cuenta con sus propios controladores. UsuarioController maneja los trabajadores, Proyecto y ProyectoMiembro Controllers administran el portafolio y equipos. TareaController permite filtrar y gestionar las asignaciones. Notificacion y ComentarioTarea Controllers manejan la mensajería interna. Y el BffController centraliza y expone toda la información combinada al frontend.

**Pregunta 20**
> **🎬 MOSTRAR EN PANTALLA:** [SecurityConfig.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/api-gateway/src/main/java/cl/innovatech/apigateway/config/SecurityConfig.java), y los archivos web [roles.js](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/frontend-app/src/auth/roles.js) y [RoleGuard.jsx](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/frontend-app/src/components/RoleGuard.jsx).

Los microservicios internos no validan el JWT directamente porque se encuentran aislados en una red interna de Docker. La seguridad perimetral recae completamente en el API Gateway mediante el Resource Server de OAuth2. Keycloak se encarga de emitir el token, el Gateway valida su firma y autoriza, y el BFF se encarga de extraer la información como el correo del usuario para pasarlo como contexto al resto del sistema. El frontend aplica guardias de ruta para evitar que un trabajador acceda a pantallas de administrador.

**Pregunta 21**
Aplicamos una gran variedad de patrones:
Arquitectura de Microservicios con servicios independientes.
Patrón de API Gateway como punto de acceso único.
Patrón BFF para simplificar y adaptar la API consumida por el frontend.
Service Discovery usando Eureka.
Circuit Breaker para tolerancia a fallos.
Patrón Database per Service para evitar dependencias de datos.
Uso extensivo de DTOs en el BFF.
Y separación en capas de Controladores, Servicios y Repositorios.

**Pregunta 22**
> **🎬 MOSTRAR EN PANTALLA:** Archivos YAML locales de `ms-proyectos` (application.yml y application-docker.yml).

Cada microservicio posee un archivo de configuración principal de tipo YAML que define el puerto, la conexión local y las credenciales de base de datos. También cuentan con un perfil específico para Docker. De forma global, contamos con el archivo docker-compose, scripts de inicialización SQL para la base de datos y la configuración del reino en Keycloak.

**Pregunta 23**
> **🎬 MOSTRAR EN PANTALLA:** Terminal corriendo `docker compose ps`, un navegador en el puerto actuator health y el panel dashboard de Eureka.

Para ejecutar todo el ecosistema al mismo tiempo, usamos docker compose up build, lo que levanta todo el stack. Para comprobar que funcionan correctamente, revisamos los endpoints de salud de actuator de cada servicio, confirmamos su presencia en el panel de control de Eureka, probamos sus rutas de Swagger UI o realizamos peticiones HTTP con un token JWT válido.

**Pregunta 24**
> **🎬 MOSTRAR EN PANTALLA:** [TrabajadorValidationAdvice.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-usuarios/src/main/java/cl/innovatech/ms_usuarios/validation/TrabajadorValidationAdvice.java) y lanzar a propósito en el frontend una creación de trabajador errónea para enseñar la alerta HTTP 400.

El manejo de excepciones está centralizado. Si hay errores de negocio, se devuelve una respuesta 400 de Bad Request con un mensaje de validación. Excepciones de red en el BFF abren el Circuit Breaker, utilizando métodos de respaldo o fallback que devuelven estructuras vacías en lugar de causar errores 500 en cadena. En el frontend se unifican las validaciones y se presentan mensajes de alerta y notificaciones flotantes con el detalle del problema.

**Pregunta 25**
Entre las buenas prácticas implementadas se incluyen: la separación estricta por capas de software; el uso de una base de datos propia por cada servicio; el diseño de componentes sin estado; el versionado del código en GitFlow; documentación activa con OpenAPI; perfiles para desarrollo local frente a contenedores; pruebas unitarias usando Mockito, y despliegues seguros utilizando DTOs.

**Pregunta 26**
> **🎬 MOSTRAR EN PANTALLA:** Configuración YAML de resilience4j en el BFF y la clase de cliente web [MicroserviceClient.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/bff-gateway/src/main/java/cl/innovatech/bff_gateway/client/MicroserviceClient.java).

Utilizamos Resilience4j en el BFF para implementar el Circuit Breaker. Está configurado para abrir el circuito si la tasa de fallos alcanza el 50 por ciento en una ventana deslizante de 10 peticiones, manteniéndose abierto por 10 segundos. Esto significa que si un microservicio interno se cae, las llamadas adicionales al mismo devuelven una respuesta por defecto inmediatamente, evitando así bloquear el sistema por tiempos de espera excesivos.

**Pregunta 27**
> **🎬 MOSTRAR EN PANTALLA:** Ver el método `create` en el [UsuarioController.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/ms-usuarios/src/main/java/cl/innovatech/ms_usuarios/controller/UsuarioController.java) devolviendo código de respuesta 201 Created.

El sistema respeta y aplica los códigos estándar del protocolo HTTP: 200 OK para lecturas y actualizaciones normales, 201 Created cuando se insertan nuevos registros, 204 No Content luego de borrar elementos, 400 Bad Request para validaciones fallidas, 401 Unauthorized para peticiones no autenticadas en el Gateway, 404 Not Found para recursos inexistentes y 500 para excepciones internas.

**Pregunta 28**
> **🎬 MOSTRAR EN PANTALLA:** Correr `docker logs innovatech-bff --tail 30` en tu consola.

Aprovechamos el sistema de registro estándar integrado en Spring Boot mediante Logback. Los registros se escriben en las salidas estándar y se consultan fácilmente mediante los comandos de logs de Docker. Aunque no integramos trazabilidad distribuida ni archivos externos, esto resulta adecuado y suficiente para la etapa de desarrollo y depuración actual del proyecto.

**Pregunta 29**
> **🎬 MOSTRAR EN PANTALLA:** Comando `curl http://localhost:8081/actuator/health` en consola o pestaña de navegador.

Cada módulo integra Spring Boot Actuator que expone rutas para revisar la salud de la aplicación, las cuales son aprovechadas activamente por los healthchecks de Docker y Eureka. En esta versión no se exponen métricas hacia Prometheus ni tableros en Grafana, quedando como mejoras propuestas para escalar la observabilidad a nivel de producción.

**Pregunta 30**
> **🎬 MOSTRAR EN PANTALLA:** Comando `./mvnw test` desde el directorio `ms-usuarios` comprobando el estado de SUCCESS.

Realizamos pruebas unitarias en el backend utilizando JUnit y Mockito, enfocadas principalmente en los servicios de negocio de usuarios, proyectos y tareas. Cubrimos validaciones importantes como asignación de capacidades o validación de roles. También implementamos pruebas de contexto. En total hay unos 31 métodos de pruebas en el sistema, asegurando el núcleo del código.

### Seguridad

**Pregunta 31**
El sistema maneja datos financieros, sueldos de empleados y asignaciones laborales, por lo cual aplicar seguridad estricta es esencial. Keycloak centraliza la autenticación; los tokens JWT permiten interacciones sin guardar estado en el servidor; el Gateway realiza una validación eficiente; y la asignación de roles evita que un trabajador regular acceda o modifique paneles de administración o finanzas.

**Pregunta 32**
> **🎬 MOSTRAR EN PANTALLA:** Página web del inicio de sesión (Login de Keycloak).

Para la generación del JWT, el usuario entra a la aplicación e inicia sesión redirigido a Keycloak. Keycloak evalúa las credenciales y, si son correctas, emite un access token en formato JWT firmado criptográficamente. Este token es almacenado en la sesión del frontend e incluye datos clave en su contenido, como el correo electrónico del usuario y la lista de sus roles asignados.

**Pregunta 33**
> **🎬 MOSTRAR EN PANTALLA:** Mostrar el [realm-innovatech.json](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/docker/keycloak/realm-innovatech.json), las variables en [frontend-app/.env](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/frontend-app/.env.example) y la ruta del resource server en el application.yml del API Gateway.

Para configurar el JWT, Keycloak se inicializa usando un archivo exportado en JSON que incluye a los usuarios de prueba, el reino, y el cliente configurado. En el frontend se definen las variables de entorno necesarias con la URL de Keycloak. Finalmente, en el API Gateway se configura el URI de Keycloak en el Resource Server, permitiendo al Gateway consultar las llaves públicas para validar cada firma del JWT recibido.

**Pregunta 34**
> **🎬 MOSTRAR EN PANTALLA:** Clase [SecurityConfig.java](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/api-gateway/src/main/java/cl/innovatech/apigateway/config/SecurityConfig.java) apuntando al bloque `.oauth2ResourceServer()`.

La validación se realiza interceptando cada petición en el API Gateway. Spring Security lee la cabecera de Autorización, busca el token, extrae las llaves públicas desde Keycloak y verifica tanto la firma como la fecha de caducidad. Si el token es inválido se deniega la petición. La información interna del token es simplemente decodificada por el BFF para identificar qué empleado hizo la petición.

**Pregunta 35**
> **🎬 MOSTRAR EN PANTALLA:** Pestaña "Network" / Red del navegador (DevTools) comprobando el envío de la cabecera "Authorization".

El enrutamiento seguro se compone del siguiente flujo: el navegador adjunta el token JWT a las peticiones hacia el API Gateway. El Gateway autoriza y según la ruta redirige la petición: los paths de tareas van directamente al microservicio de tareas, las rutas de trabajadores se reescriben para el microservicio de usuarios, y todas las rutas complejas o agregadas van hacia el BFF.

### API Gateway

**Pregunta 36**
El API Gateway utiliza Spring Cloud Gateway, que es un servidor proxy reactivo diseñado para manejar alto tráfico. Su función es aplicar los filtros globales para habilitar peticiones CORS y validar la seguridad mediante el JWT. Además actúa como balanceador de cargas consultando a Eureka, siendo el único punto de contacto que el frontend conoce, ocultando el resto de la topología interna.

**Pregunta 37**
Los componentes del API Gateway incluyen su clase principal de ejecución, un archivo de configuración de seguridad que parametriza las rutas públicas y habilita el servidor de recursos JWT, una configuración CORS para autorizar al frontend, y archivos YAML donde se definen de manera declarativa todas las reglas de redirección y las URIs de los destinos.

**Pregunta 38**
> **🎬 MOSTRAR EN PANTALLA:** Bloque de enrutamiento `spring.cloud.gateway.routes` en el `application.yml` de Api Gateway.

Filtra las peticiones aplicando primero el chequeo de seguridad JWT. Luego evalúa los filtros de rutas ordenados por prioridad: ciertas rutas específicas se mandan directamente a sus respectivos microservicios para mayor eficiencia, y si existe un nivel de agregación, las envía al BFF. También se encarga de reescribir y limpiar las URL si la ruta interna de un microservicio difiere de la ruta expuesta al frontend.

### Monitoreo

**Pregunta 39**
El monitoreo es fundamental. Con una arquitectura distribuida que contiene cuatro microservicios y un BFF, un fallo en cualquier parte es difícil de localizar de forma aislada. Gracias a herramientas como los indicadores de salud de Docker, el panel de instancias caídas de Eureka y las mitigaciones del Circuit Breaker, el sistema puede identificar incidentes antes de que derriben toda la aplicación.

**Pregunta 40**
Si un microservicio falla, suceden varias cosas: el componente Eureka lo retira de su lista de instancias, y el Circuit Breaker del BFF intercepta los fallos abriendo el circuito. De esta manera la aplicación no colapsa, mostrando al usuario información vacía o mensajes controlados. Para solucionarlo, revisaríamos los logs del contenedor afectado en Docker y procederíamos a reiniciarlo.

**Pregunta 41**
> **🎬 MOSTRAR EN PANTALLA:** El dashboard de Eureka y lanzar el comando `docker compose ps` para comprobar qué contenedores están activos.

Detectamos los problemas a través de varios canales: Docker marca el contenedor afectado como no saludable, Eureka elimina su visualización, la terminal de logs muestra excepciones detalladas, y el usuario final visualizará avisos flotantes de error proporcionados por el frontend.

**Pregunta 42**
El monitoreo es una base para mejoras continuas. Entre los próximos pasos propuestos están: exponer endpoints de métricas en Actuator, implementar recolección de métricas mediante Prometheus, usar rastreo distribuido con Zipkin para observar transacciones a través de los microservicios, y crear tableros gráficos de administración en Grafana.

### Frontend

**Pregunta 43**
> **🎬 MOSTRAR EN PANTALLA:** Abrir el [package.json](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/frontend-app/package.json) del frontend y dar un recorrido libre rápido visual de la aplicación.

El frontend es una aplicación moderna construida con React y Vite, lo cual asegura compilaciones y desarrollos rápidos. Incorpora React Router para navegación, y se integra de forma directa con Keycloak JS. Utiliza bibliotecas gráficas para los paneles, y agrupa en una única interfaz todas las funcionalidades de gestión, tablero Kanban y métricas.

**Pregunta 44**
> **🎬 MOSTRAR EN PANTALLA:** Iniciar sesión paralela o sucesiva para comparar la vista del admin (demo) frente a la de la trabajadora (ana).

El frontend aplica fuertes reglas de negocio: el trabajador tiene su acceso limitado y no puede ver tableros de finanzas o manipular proyectos. Solo puede ver sus asignaciones y mover tareas de estado en su panel Kanban. En la creación de trabajadores, el rol es forzosamente escogido de un catálogo válido, y sus capacidades horarias están controladas mediante formularios.

**Pregunta 45**
Se emplearon excelentes prácticas de desarrollo en React. El código modulariza los componentes y pantallas de forma lógica, centraliza todas las comunicaciones por red en un solo cliente API HTTP, carga las interfaces pesadas bajo demanda usando lazy loading y captura posibles fallos de renderizado mediante límites de error o Error Boundaries.

**Pregunta 46**
La seguridad arranca inicializando Keycloak antes siquiera de mostrar la interfaz web, e incluye la renovación automática del token de sesión en segundo plano. La funcionalidad puede ampliarse muy rápido: creamos componentes nuevos, los amarramos en el enrutador y si es necesario definimos su correspondiente caso de uso en el backend.

**Pregunta 47**
Ante una petición sin respuesta, el cliente API captura el error de conexión o un timeout excesivo y emite un aviso visual en la pantalla para notificar al usuario. Otros componentes críticos como la campana de notificaciones están hechos para tolerar fallos silenciando excepciones de red temporales. Además, los fallbacks del BFF evitan que la interfaz se quede cargando permanentemente.

**Pregunta 48**
Los tiempos de respuesta de peticiones complejas como el dashboard pueden ser un poco mayores debido a que el BFF interroga secuencialmente a distintos microservicios; un trade-off aceptable en esta versión. Se hace encuestas constantes en segundo plano cada 30 segundos para revisar si existen nuevas notificaciones para el usuario.

**Pregunta 49**
Los estándares de rendimiento para la interfaz buscan inmediatez. Se muestran animaciones de carga mientras se recuperan las credenciales y se usan esqueletos visuales para las tablas de datos. El diseño prioriza la resiliencia: la aplicación no se congela por completo si un servicio interno se cae, y posee un formato completamente responsivo para distintas pantallas.

**Pregunta 50**
Los errores de autenticación o caídas de red se canalizan hacia mensajes en la interfaz web, de modo que el usuario sepa que ocurrió un problema. Además, cualquier error de renderizado de React se atrapa e imprime en la consola de navegador por el ErrorBoundary, quedando el sistema preparado a futuro para incluir integraciones de reportes como Sentry.

**Pregunta 51**
> **🎬 MOSTRAR EN PANTALLA:** Error rojo visual en formulario de creación en React, y cómo se ve un panel para trabajadores cuando se entra.

Los errores internos de los microservicios son interceptados, y si corresponden a problemas de negocio, el cliente extrae el detalle y lo presenta limpiamente en notificaciones de interfaz, en lugar de mostrar códigos de error genéricos. Así el usuario recibe textos claros como "El rol no es permitido" o "Aún no hay comentarios".

**Pregunta 52**
> **🎬 MOSTRAR EN PANTALLA:** El archivo [ErrorBoundary.jsx](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/frontend-app/src/components/ErrorBoundary.jsx) y la gestión de carga en [App.jsx](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/frontend-app/src/App.jsx).

El frontend maneja un sistema robusto de captura de excepciones utilizando ErrorBoundary para vistas, bloques try-catch para transacciones asincrónicas con el backend, e implementando un manejo unificado de errores HTTP y autenticaciones en su cliente web. Las pantallas validan intensivamente los datos de entrada en todos los formularios mostrando errores descriptivos campo por campo.

### Cierre Video Arquitectura
En resumen, Innovatech implementa una arquitectura de microservicios con un diseño ligero basado en dominio, enrutamiento mediante Eureka, un API Gateway con seguridad JWT, un orquestador BFF protegido por un Circuit Breaker y almacenamiento distribuido. Además de contar con un frontend React de alto nivel y roles segregados. El código está disponible en GitHub y la documentación en su respectiva carpeta. En el siguiente video demostraré el uso práctico del sistema.

---

## PARTE 2 — VIDEO DE USO

### Apertura
En este video muestro cómo instalar, configurar y usar Innovatech Solutions, tanto desde la perspectiva del usuario final como desde la vista de un administrador.

### Pregunta 1
> **🎬 MOSTRAR EN PANTALLA:** Diapositiva conceptual con diagrama antes vs después.

Reitero la problemática principal: gestionar proyectos tecnológicos en múltiples equipos, integrando fechas, plazos y recursos financieros de forma unificada. La solución que entregamos es una plataforma completa con inicio de sesión restringido, un panel administrativo de indicadores clave, control de operarios, tablero de trabajo tipo Kanban integrado, finanzas, métricas de capacidad de los equipos y un sistema centralizado de notificaciones.

### Pregunta 2
> **🎬 MOSTRAR EN PANTALLA:** Sistema online corriendo, habiendo iniciado sesión con el usuario "demo".

Innovatech Solutions es el producto demostrativo para una empresa ficticia, pero desarrollado con las tecnologías líderes de mercado como Spring Boot, Spring Cloud, PostgreSQL, Keycloak y React. El proyecto ya incluye un poblamiento de prueba con trabajadores, proyectos y tareas generadas. Se ofrecen principalmente dos accesos para interactuar con el sistema: el administrador cuyo nombre de usuario es demo, y un perfil de trabajador de prueba llamado ana.

### Pregunta 3
> **🎬 MOSTRAR EN PANTALLA:** La tabla en el archivo [GUIA_INICIO.md](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/docs/GUIA_INICIO.md).

En cuanto a los requisitos técnicos, se recomienda ampliamente el uso de Docker Desktop y Docker Compose para levantar todos los sistemas del backend rápidamente. Para el frontend se necesita tener instalado Node JS. Se sugieren 8 Gigabytes de memoria RAM para el levantamiento total y es necesario tener disponibles puertos locales como el 5173, 8080, 8180 y 5432.

### Pregunta 4
> **🎬 MOSTRAR EN PANTALLA:** Pantalla compartida en dos terminales: una levantando `docker compose up --build` y otra con `npm run dev`.

El proceso de instalación es muy sencillo. Primero, se clona el repositorio desde GitHub a nuestro entorno local. Luego, dentro de la carpeta principal, ejecutamos "docker compose up build". Este comando levanta la red de contenedores con PostgreSQL, el servidor de seguridad Keycloak, Eureka, los cuatro microservicios y ambos Gateways. Todo de manera automatizada. En una terminal por separado nos movemos al frontend, instalamos las dependencias necesarias de Node e iniciamos el entorno de desarrollo web.

### Pregunta 5
> **🎬 MOSTRAR EN PANTALLA:** Escribir en navegador `localhost:5173`, dejar que salte el login de Keycloak y redirigir con cuenta de "demo" o "ana".

Para acceder al sistema nos conectamos al puerto local 5173 desde nuestro navegador. La aplicación web, al detectar que no tenemos sesión, nos redirige automáticamente al entorno seguro de Keycloak. Para probar la vista completa utilizamos las credenciales de administrador, ingresando "demo" tanto de usuario como de contraseña con el número 123. También podríamos ingresar como el usuario de nombre "ana", si queremos ver la plataforma desde los ojos de un colaborador regular.

### Pregunta 6
> **🎬 MOSTRAR EN PANTALLA:** Navegar y hacer hover a través del sidebar, cabecera (notificaciones) y widgets.

Al ingresar encontramos una interfaz muy clara: en el panel izquierdo disponemos del menú de navegación, un distintivo de nuestro perfil actual y el botón para finalizar la sesión. En la parte superior contamos con nuestra campana para el centro de notificaciones, y en el espacio principal es donde interactuamos con tarjetas gráficas informativas, tablas de datos del sistema, e interfaces complejas como el tablero de asignación Kanban.

### Pregunta 7
Repasemos sus funcionalidades principales. Tenemos el Dashboard administrativo repleto de indicadores consolidados de negocio. También el panel simplificado con foco en las tareas para los empleados regulares. El módulo central para gestionar el catálogo de los colaboradores y el catálogo del portafolio de proyectos, donde visualizamos el detalle del equipo y presupuestos. Tenemos también el tablero interactivo donde movemos nuestras tarjetas de asignación libremente, la gestión de la capacidad ocupada por nuestros empleados y todo un modelo robusto de notificaciones y comentarios interconectados.

### Pregunta 8
Para la demostración completa iniciaremos una sesión con perfil de Administrador. Entrando al Dashboard, supervisamos de un vistazo el estatus global.
Navegamos a trabajadores y validamos la creación rápida de un nuevo integrante.
Luego pasamos al portafolio de proyectos, escogemos uno, y vemos su cronograma, la asignación del personal, generamos un aviso directo para todo el equipo del proyecto y analizamos rápidamente su resumen de costos y márgenes de ganancia.
En finanzas de la empresa, confirmamos el rendimiento financiero total.
Revisamos la sección de Capacidad para notar sobrecargas operativas en el personal.
Luego vamos directo al tablero Kanban, para reasignar una tarjeta y agregar un comentario de retroalimentación en la misma.
Y cerramos validando nuestra campana con las notificaciones recibidas.

Enseguida cerramos nuestra sesión y reingresamos pero esta vez con la trabajadora de prueba, Ana.
Inmediatamente la aplicación reacciona recortando el panel izquierdo, presentándonos en su lugar nuestro panel de inicio simplificado mostrando las entregas más próximas. En la opción de mis tareas accedemos al tablero exclusivo donde vemos las tareas que se nos asignaron anteriormente y aprovechamos de contestar un comentario con una actualización del trabajo.
Finalmente, probamos el sistema de seguridad intentando forzar la URL hacia el módulo financiero y vemos que nuestro perfil es automáticamente rechazado.

### Pregunta 9
> **🎬 MOSTRAR EN PANTALLA:** [arquitectura-logica.svg](file:///c:/Users/idkra/Downloads/fullstack%20III%20Innovatech/docs/assets/arquitectura-logica.svg), consola con status UP de Docker y la página web principal.

Para concluir, Innovatech refleja un producto robusto a nivel de arquitectura y software moderno.
Su principal virtud es la escalabilidad horizontal: los microservicios están listos para multiplicar sus instancias dentro de la red a medida que el negocio lo exija; la estructura basada en React puede desplegarse en servidores eficientes en la nube; Keycloak provee seguridad nivel corporativo y las bases de datos pueden alojarse separadamente por módulo de negocio o migrarse sin afectar el código entero. 
Podemos extender las capacidades agregando mensajería por colas, despliegues por Kubernetes y pruebas E2E.
Todo el trabajo final se encuentra actualizado y empaquetado en su repositorio de GitHub. Muchas gracias por su atención y tiempo.

---
*Fin del guion.*
