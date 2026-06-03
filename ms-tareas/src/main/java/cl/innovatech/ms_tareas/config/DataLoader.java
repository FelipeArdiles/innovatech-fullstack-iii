package cl.innovatech.ms_tareas.config;

import cl.innovatech.ms_tareas.entity.CategoriaTarea;
import cl.innovatech.ms_tareas.entity.DificultadTarea;
import cl.innovatech.ms_tareas.entity.Tarea;
import cl.innovatech.ms_tareas.repository.TareaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Configuration
public class DataLoader {

	private static final int HORAS_MES = 160;

	/** Sueldos líquidos mensuales CLP alineados con ms-usuarios (id → sueldo). */
	private static final Map<Long, Long> SUELDOS_CLP = Map.ofEntries(
		Map.entry(1L, 1_200_000L), Map.entry(2L, 3_200_000L), Map.entry(3L, 2_800_000L),
		Map.entry(4L, 1_800_000L), Map.entry(5L, 1_600_000L), Map.entry(6L, 2_500_000L),
		Map.entry(7L, 2_200_000L), Map.entry(8L, 1_750_000L), Map.entry(9L, 1_900_000L),
		Map.entry(10L, 4_500_000L), Map.entry(11L, 1_050_000L), Map.entry(12L, 2_400_000L),
		Map.entry(13L, 2_100_000L), Map.entry(14L, 1_700_000L), Map.entry(15L, 3_000_000L),
		Map.entry(16L, 1_850_000L), Map.entry(17L, 2_600_000L), Map.entry(18L, 1_350_000L),
		Map.entry(19L, 1_950_000L), Map.entry(20L, 2_700_000L), Map.entry(21L, 2_900_000L),
		Map.entry(22L, 1_400_000L), Map.entry(23L, 5_200_000L), Map.entry(24L, 950_000L),
		Map.entry(25L, 1_650_000L), Map.entry(26L, 1_550_000L), Map.entry(27L, 3_800_000L),
		Map.entry(28L, 1_000_000L), Map.entry(29L, 2_000_000L), Map.entry(30L, 2_300_000L)
	);

	@Bean
	CommandLineRunner initTareas(TareaRepository repository) {
		return args -> {
			if (repository.count() > 0) {
				return;
			}

			// Proyecto 1 — Portal Clientes B2B
			save(repository, "Diseñar UI login corporativo", "Mockups Figma y design system", "POR_HACER", 1L, 5L, 20,
				DificultadTarea.MEDIA, CategoriaTarea.DISENO);
			save(repository, "API autenticación OIDC", "Integrar Keycloak y refresh tokens", "EN_PROGRESO", 1L, 1L, 32,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "Módulo facturación electrónica", "SII y PDF dinámicos", "POR_HACER", 1L, 4L, 40,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "Dashboard analítico clientes", "KPIs y export CSV", "EN_PROGRESO", 1L, 11L, 24,
				DificultadTarea.MEDIA, CategoriaTarea.DESARROLLO);
			save(repository, "Pruebas regresión portal", "Suite automatizada Playwright", "POR_HACER", 1L, 7L, 16,
				DificultadTarea.MEDIA, CategoriaTarea.QA);
			save(repository, "Plan de despliegue producción", "Blue-green y rollback", "HECHO", 1L, 6L, 8,
				DificultadTarea.BAJA, CategoriaTarea.DEVOPS);

			// Proyecto 2 — App Móvil Retail
			save(repository, "Arquitectura app híbrida", "Evaluación React Native vs Flutter", "POR_HACER", 2L, 2L, 16,
				DificultadTarea.ALTA, CategoriaTarea.ANALISIS);
			save(repository, "Wireframes checkout", "Flujo pago y carrito", "POR_HACER", 2L, 5L, 12,
				DificultadTarea.MEDIA, CategoriaTarea.DISENO);
			save(repository, "Integración ERP inventario", "API stock en tiempo real", "POR_HACER", 2L, 4L, 28,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "Programa fidelización", "Puntos y cupones", "POR_HACER", 2L, 8L, 20,
				DificultadTarea.MEDIA, CategoriaTarea.DESARROLLO);

			// Proyecto 3 — Migración Cloud AWS
			save(repository, "Inventario servicios legacy", "Catálogo y dependencias", "HECHO", 3L, 9L, 12,
				DificultadTarea.BAJA, CategoriaTarea.ANALISIS);
			save(repository, "Contenedores ECS/Fargate", "Dockerfiles y task defs", "HECHO", 3L, 6L, 36,
				DificultadTarea.ALTA, CategoriaTarea.DEVOPS);
			save(repository, "Observabilidad CloudWatch", "Dashboards y alertas", "HECHO", 3L, 17L, 16,
				DificultadTarea.MEDIA, CategoriaTarea.DEVOPS);
			save(repository, "Runbook disaster recovery", "RTO/RPO documentados", "HECHO", 3L, 3L, 8,
				DificultadTarea.BAJA, CategoriaTarea.GESTION);

			// Proyecto 4 — BI Analytics (margen ajustado)
			save(repository, "Modelo dimensional ventas", "Star schema en Snowflake", "EN_PROGRESO", 4L, 13L, 40,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "Pipeline ETL diario", "Airflow + dbt", "EN_PROGRESO", 4L, 6L, 32,
				DificultadTarea.ALTA, CategoriaTarea.DEVOPS);
			save(repository, "Tablero ejecutivo Power BI", "Métricas EBITDA y churn", "POR_HACER", 4L, 30L, 20,
				DificultadTarea.MEDIA, CategoriaTarea.ANALISIS);
			save(repository, "Validación calidad datos", "Great Expectations", "EN_PROGRESO", 4L, 18L, 16,
				DificultadTarea.MEDIA, CategoriaTarea.QA);
			save(repository, "Capacitación usuarios BI", "Talleres área negocio", "POR_HACER", 4L, 12L, 6,
				DificultadTarea.BAJA, CategoriaTarea.GESTION);

			// Proyecto 5 — API Pagos
			save(repository, "Especificación OpenAPI v3", "Contratos y ejemplos", "HECHO", 5L, 10L, 10,
				DificultadTarea.MEDIA, CategoriaTarea.ANALISIS);
			save(repository, "Endpoint transferencias", "Idempotencia y conciliación", "EN_PROGRESO", 5L, 4L, 36,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "Webhooks bancarios", "Firma HMAC y reintentos", "EN_PROGRESO", 5L, 16L, 24,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "Pruebas carga API pagos", "k6 500 RPS", "POR_HACER", 5L, 7L, 12,
				DificultadTarea.MEDIA, CategoriaTarea.QA);
			save(repository, "Cumplimiento normativa CMF", "Checklist y evidencias", "POR_HACER", 5L, 30L, 8,
				DificultadTarea.BAJA, CategoriaTarea.GESTION);

			// Proyecto 6 — Portal RRHH
			save(repository, "Relevamiento procesos RRHH", "Entrevistas stakeholders", "POR_HACER", 6L, 9L, 12,
				DificultadTarea.BAJA, CategoriaTarea.ANALISIS);
			save(repository, "Prototipo onboarding", "Flujo día 1 colaborador", "POR_HACER", 6L, 14L, 16,
				DificultadTarea.MEDIA, CategoriaTarea.DISENO);
			save(repository, "Módulo vacaciones", "Workflow aprobaciones", "POR_HACER", 6L, 19L, 24,
				DificultadTarea.MEDIA, CategoriaTarea.DESARROLLO);

			// Proyecto 7 — Chatbot IA
			save(repository, "Base conocimiento RAG", "Embeddings y chunking", "EN_PROGRESO", 7L, 13L, 28,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "UI widget chat", "React embebible", "EN_PROGRESO", 7L, 11L, 16,
				DificultadTarea.MEDIA, CategoriaTarea.DESARROLLO);
			save(repository, "Escalamiento a agentes", "Integración Zendesk", "POR_HACER", 7L, 4L, 20,
				DificultadTarea.MEDIA, CategoriaTarea.DESARROLLO);
			save(repository, "Evaluación respuestas LLM", "Métricas precisión", "POR_HACER", 7L, 18L, 12,
				DificultadTarea.MEDIA, CategoriaTarea.QA);

			// Proyecto 8 — E-commerce (atrasado / pérdida demo)
			save(repository, "Checkout multi-vendor", "Split payments", "EN_PROGRESO", 8L, 19L, 40,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "Panel vendedores", "CRUD productos y stock", "EN_PROGRESO", 8L, 1L, 32,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);
			save(repository, "Integración courier", "Tracking y etiquetas", "POR_HACER", 8L, 8L, 24,
				DificultadTarea.MEDIA, CategoriaTarea.DESARROLLO);
			save(repository, "Optimización SEO catálogo", "SSR y metadatos", "HECHO", 8L, 11L, 12,
				DificultadTarea.BAJA, CategoriaTarea.DESARROLLO);
			save(repository, "Sprint retrospectiva", "Lecciones aprendidas Q2", "HECHO", 8L, 12L, 4,
				DificultadTarea.BAJA, CategoriaTarea.GESTION);
			save(repository, "Pruebas penetración tienda", "OWASP top 10", "POR_HACER", 8L, 15L, 16,
				DificultadTarea.ALTA, CategoriaTarea.QA);

			// Proyecto 9 — Ciberseguridad
			save(repository, "Pentest aplicaciones web", "Informe hallazgos", "HECHO", 9L, 15L, 24,
				DificultadTarea.ALTA, CategoriaTarea.QA);
			save(repository, "Hardening servidores", "CIS benchmarks", "HECHO", 9L, 6L, 20,
				DificultadTarea.MEDIA, CategoriaTarea.DEVOPS);
			save(repository, "Plan remediación crítico", "Roadmap 90 días", "HECHO", 9L, 3L, 8,
				DificultadTarea.BAJA, CategoriaTarea.GESTION);

			// Proyecto 10 — ERP-SAP (cancelado)
			save(repository, "Análisis conectores SAP", "Mapeo entidades", "HECHO", 10L, 9L, 16,
				DificultadTarea.MEDIA, CategoriaTarea.ANALISIS);
			save(repository, "POC sincronización CRM", "Prueba bidireccional", "CANCELADO", 10L, 4L, 20,
				DificultadTarea.ALTA, CategoriaTarea.DESARROLLO);

			// Tareas transversales
			save(repository, "Documentación API interna", "Swagger agregado BFF", "HECHO", 1L, 29L, 6,
				DificultadTarea.BAJA, CategoriaTarea.GESTION);
			save(repository, "Refactor módulo notificaciones", "Cola async Redis", "POR_HACER", 5L, 27L, 18,
				DificultadTarea.MEDIA, CategoriaTarea.DESARROLLO);
			save(repository, "Diseño iconografía app retail", "Kit UI móvil", "POR_HACER", 2L, 14L, 10,
				DificultadTarea.BAJA, CategoriaTarea.DISENO);
			save(repository, "Monitoreo SLO pagos", "SLI latencia p99", "EN_PROGRESO", 5L, 17L, 8,
				DificultadTarea.MEDIA, CategoriaTarea.DEVOPS);
			save(repository, "Capacitación equipo BI", "SQL avanzado", "POR_HACER", 4L, 22L, 6,
				DificultadTarea.BAJA, CategoriaTarea.GESTION);
		};
	}

	private static void save(TareaRepository repository, String titulo, String descripcion, String estado,
			long proyectoId, long asignadoId, int horas, DificultadTarea dificultad,
			CategoriaTarea categoria) {
		repository.save(new Tarea(null, titulo, descripcion, estado, proyectoId, asignadoId, horas,
			dificultad, valorPorHoras(asignadoId, horas, dificultad), categoria));
	}

	private static BigDecimal valorPorHoras(long asignadoId, int horas, DificultadTarea dificultad) {
		long sueldo = SUELDOS_CLP.getOrDefault(asignadoId, 1_500_000L);
		double factor = switch (dificultad) {
			case BAJA -> 1.0;
			case MEDIA -> 1.15;
			case ALTA -> 1.35;
		};
		double tarifaHora = sueldo / (double) HORAS_MES;
		return BigDecimal.valueOf(Math.round(horas * tarifaHora * factor))
			.setScale(0, RoundingMode.HALF_UP);
	}
}
