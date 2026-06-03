package cl.innovatech.ms_tareas.config;

import cl.innovatech.ms_tareas.entity.CategoriaTarea;
import cl.innovatech.ms_tareas.entity.DificultadTarea;
import cl.innovatech.ms_tareas.entity.Tarea;
import cl.innovatech.ms_tareas.repository.TareaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

	@Bean
	CommandLineRunner initTareas(TareaRepository repository) {
		return args -> {
			repository.deleteAll();

			// Proyecto 1 — Portal Clientes B2B
			save(repository, "Diseñar UI login corporativo", "Mockups Figma y design system", "POR_HACER", 1L, 5L, 20,
				DificultadTarea.MEDIA, "8500", CategoriaTarea.DISENO);
			save(repository, "API autenticación OIDC", "Integrar Keycloak y refresh tokens", "EN_PROGRESO", 1L, 1L, 32,
				DificultadTarea.ALTA, "18000", CategoriaTarea.DESARROLLO);
			save(repository, "Módulo facturación electrónica", "SII y PDF dinámicos", "POR_HACER", 1L, 4L, 40,
				DificultadTarea.ALTA, "22000", CategoriaTarea.DESARROLLO);
			save(repository, "Dashboard analítico clientes", "KPIs y export CSV", "EN_PROGRESO", 1L, 11L, 24,
				DificultadTarea.MEDIA, "12000", CategoriaTarea.DESARROLLO);
			save(repository, "Pruebas regresión portal", "Suite automatizada Playwright", "POR_HACER", 1L, 7L, 16,
				DificultadTarea.MEDIA, "6500", CategoriaTarea.QA);
			save(repository, "Plan de despliegue producción", "Blue-green y rollback", "HECHO", 1L, 6L, 8,
				DificultadTarea.BAJA, "4200", CategoriaTarea.DEVOPS);

			// Proyecto 2 — App Móvil Retail
			save(repository, "Arquitectura app híbrida", "Evaluación React Native vs Flutter", "POR_HACER", 2L, 2L, 16,
				DificultadTarea.ALTA, "14000", CategoriaTarea.ANALISIS);
			save(repository, "Wireframes checkout", "Flujo pago y carrito", "POR_HACER", 2L, 5L, 12,
				DificultadTarea.MEDIA, "7500", CategoriaTarea.DISENO);
			save(repository, "Integración ERP inventario", "API stock en tiempo real", "POR_HACER", 2L, 4L, 28,
				DificultadTarea.ALTA, "19000", CategoriaTarea.DESARROLLO);
			save(repository, "Programa fidelización", "Puntos y cupones", "POR_HACER", 2L, 8L, 20,
				DificultadTarea.MEDIA, "11000", CategoriaTarea.DESARROLLO);

			// Proyecto 3 — Migración Cloud AWS
			save(repository, "Inventario servicios legacy", "Catálogo y dependencias", "HECHO", 3L, 9L, 12,
				DificultadTarea.BAJA, "5000", CategoriaTarea.ANALISIS);
			save(repository, "Contenedores ECS/Fargate", "Dockerfiles y task defs", "HECHO", 3L, 6L, 36,
				DificultadTarea.ALTA, "24000", CategoriaTarea.DEVOPS);
			save(repository, "Observabilidad CloudWatch", "Dashboards y alertas", "HECHO", 3L, 17L, 16,
				DificultadTarea.MEDIA, "9000", CategoriaTarea.DEVOPS);
			save(repository, "Runbook disaster recovery", "RTO/RPO documentados", "HECHO", 3L, 3L, 8,
				DificultadTarea.BAJA, "4000", CategoriaTarea.GESTION);

			// Proyecto 4 — BI Analytics
			save(repository, "Modelo dimensional ventas", "Star schema en Snowflake", "EN_PROGRESO", 4L, 13L, 40,
				DificultadTarea.ALTA, "26000", CategoriaTarea.DESARROLLO);
			save(repository, "Pipeline ETL diario", "Airflow + dbt", "EN_PROGRESO", 4L, 6L, 32,
				DificultadTarea.ALTA, "21000", CategoriaTarea.DEVOPS);
			save(repository, "Tablero ejecutivo Power BI", "Métricas EBITDA y churn", "POR_HACER", 4L, 30L, 20,
				DificultadTarea.MEDIA, "11000", CategoriaTarea.ANALISIS);
			save(repository, "Validación calidad datos", "Great Expectations", "EN_PROGRESO", 4L, 18L, 16,
				DificultadTarea.MEDIA, "8000", CategoriaTarea.QA);
			save(repository, "Capacitación usuarios BI", "Talleres área negocio", "POR_HACER", 4L, 12L, 6,
				DificultadTarea.BAJA, "3000", CategoriaTarea.GESTION);

			// Proyecto 5 — API Pagos
			save(repository, "Especificación OpenAPI v3", "Contratos y ejemplos", "HECHO", 5L, 10L, 10,
				DificultadTarea.MEDIA, "5500", CategoriaTarea.ANALISIS);
			save(repository, "Endpoint transferencias", "Idempotencia y conciliación", "EN_PROGRESO", 5L, 4L, 36,
				DificultadTarea.ALTA, "24000", CategoriaTarea.DESARROLLO);
			save(repository, "Webhooks bancarios", "Firma HMAC y reintentos", "EN_PROGRESO", 5L, 16L, 24,
				DificultadTarea.ALTA, "16000", CategoriaTarea.DESARROLLO);
			save(repository, "Pruebas carga API pagos", "k6 500 RPS", "POR_HACER", 5L, 7L, 12,
				DificultadTarea.MEDIA, "7000", CategoriaTarea.QA);
			save(repository, "Cumplimiento normativa CMF", "Checklist y evidencias", "POR_HACER", 5L, 30L, 8,
				DificultadTarea.BAJA, "4500", CategoriaTarea.GESTION);

			// Proyecto 6 — Portal RRHH
			save(repository, "Relevamiento procesos RRHH", "Entrevistas stakeholders", "POR_HACER", 6L, 9L, 12,
				DificultadTarea.BAJA, "4800", CategoriaTarea.ANALISIS);
			save(repository, "Prototipo onboarding", "Flujo día 1 colaborador", "POR_HACER", 6L, 14L, 16,
				DificultadTarea.MEDIA, "7200", CategoriaTarea.DISENO);
			save(repository, "Módulo vacaciones", "Workflow aprobaciones", "POR_HACER", 6L, 19L, 24,
				DificultadTarea.MEDIA, "10500", CategoriaTarea.DESARROLLO);

			// Proyecto 7 — Chatbot IA
			save(repository, "Base conocimiento RAG", "Embeddings y chunking", "EN_PROGRESO", 7L, 13L, 28,
				DificultadTarea.ALTA, "19500", CategoriaTarea.DESARROLLO);
			save(repository, "UI widget chat", "React embebible", "EN_PROGRESO", 7L, 11L, 16,
				DificultadTarea.MEDIA, "9000", CategoriaTarea.DESARROLLO);
			save(repository, "Escalamiento a agentes", "Integración Zendesk", "POR_HACER", 7L, 4L, 20,
				DificultadTarea.MEDIA, "11000", CategoriaTarea.DESARROLLO);
			save(repository, "Evaluación respuestas LLM", "Métricas precisión", "POR_HACER", 7L, 18L, 12,
				DificultadTarea.MEDIA, "6500", CategoriaTarea.QA);

			// Proyecto 8 — E-commerce (atrasado)
			save(repository, "Checkout multi-vendor", "Split payments", "EN_PROGRESO", 8L, 19L, 40,
				DificultadTarea.ALTA, "28000", CategoriaTarea.DESARROLLO);
			save(repository, "Panel vendedores", "CRUD productos y stock", "EN_PROGRESO", 8L, 1L, 32,
				DificultadTarea.ALTA, "22000", CategoriaTarea.DESARROLLO);
			save(repository, "Integración courier", "Tracking y etiquetas", "POR_HACER", 8L, 8L, 24,
				DificultadTarea.MEDIA, "14000", CategoriaTarea.DESARROLLO);
			save(repository, "Optimización SEO catálogo", "SSR y metadatos", "HECHO", 8L, 11L, 12,
				DificultadTarea.BAJA, "5500", CategoriaTarea.DESARROLLO);
			save(repository, "Sprint retrospectiva", "Lecciones aprendidas Q2", "HECHO", 8L, 12L, 4,
				DificultadTarea.BAJA, "2000", CategoriaTarea.GESTION);
			save(repository, "Pruebas penetración tienda", "OWASP top 10", "POR_HACER", 8L, 15L, 16,
				DificultadTarea.ALTA, "12000", CategoriaTarea.QA);

			// Proyecto 9 — Ciberseguridad
			save(repository, "Pentest aplicaciones web", "Informe hallazgos", "HECHO", 9L, 15L, 24,
				DificultadTarea.ALTA, "18000", CategoriaTarea.QA);
			save(repository, "Hardening servidores", "CIS benchmarks", "HECHO", 9L, 6L, 20,
				DificultadTarea.MEDIA, "12000", CategoriaTarea.DEVOPS);
			save(repository, "Plan remediación crítico", "Roadmap 90 días", "HECHO", 9L, 3L, 8,
				DificultadTarea.BAJA, "4000", CategoriaTarea.GESTION);

			// Proyecto 10 — ERP-SAP (cancelado)
			save(repository, "Análisis conectores SAP", "Mapeo entidades", "HECHO", 10L, 9L, 16,
				DificultadTarea.MEDIA, "8000", CategoriaTarea.ANALISIS);
			save(repository, "POC sincronización CRM", "Prueba bidireccional", "CANCELADO", 10L, 4L, 20,
				DificultadTarea.ALTA, "12000", CategoriaTarea.DESARROLLO);

			// Tareas transversales / extras
			save(repository, "Documentación API interna", "Swagger agregado BFF", "HECHO", 1L, 29L, 6,
				DificultadTarea.BAJA, "2800", CategoriaTarea.GESTION);
			save(repository, "Refactor módulo notificaciones", "Cola async Redis", "POR_HACER", 5L, 27L, 18,
				DificultadTarea.MEDIA, "9500", CategoriaTarea.DESARROLLO);
			save(repository, "Diseño iconografía app retail", "Kit UI móvil", "POR_HACER", 2L, 14L, 10,
				DificultadTarea.BAJA, "4200", CategoriaTarea.DISENO);
			save(repository, "Monitoreo SLO pagos", "SLI latencia p99", "EN_PROGRESO", 5L, 17L, 8,
				DificultadTarea.MEDIA, "5000", CategoriaTarea.DEVOPS);
			save(repository, "Capacitación equipo BI", "SQL avanzado", "POR_HACER", 4L, 22L, 6,
				DificultadTarea.BAJA, "2500", CategoriaTarea.GESTION);
		};
	}

	private static void save(TareaRepository repository, String titulo, String descripcion, String estado,
			long proyectoId, long asignadoId, int horas, DificultadTarea dificultad, String valor,
			CategoriaTarea categoria) {
		repository.save(new Tarea(null, titulo, descripcion, estado, proyectoId, asignadoId, horas,
			dificultad, new BigDecimal(valor), categoria));
	}
}
