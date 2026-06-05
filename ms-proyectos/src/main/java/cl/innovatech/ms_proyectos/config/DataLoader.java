package cl.innovatech.ms_proyectos.config;

import cl.innovatech.ms_proyectos.entity.Proyecto;
import cl.innovatech.ms_proyectos.repository.ProyectoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataLoader {

	@Bean
	CommandLineRunner initProyectos(ProyectoRepository repository) {
		return args -> {
			if (repository.count() > 0) {
				return;
			}
			LocalDate today = LocalDate.now();

			repository.save(p(3L, "Portal Clientes B2B", "EN_PROGRESO",
				"Modernización integral del portal web B2B con autenticación OIDC, dashboard analítico y módulo de facturación electrónica para clientes corporativos.",
				today.minusDays(30), today.plusDays(60),
				bd("72000000"), bd("68500000"), bd("92000000")));

			repository.save(p(9L, "App Móvil Retail", "PLANIFICADO",
				"Aplicación nativa/híbrida para cadena retail: catálogo, carrito, pagos y programa de fidelización integrado con ERP.",
				today.plusDays(14), today.plusDays(120),
				bd("120000000"), bd("18500000"), bd("145000000")));

			repository.save(p(6L, "Migración Cloud AWS", "COMPLETADO",
				"Migración de servicios legacy on-premise a AWS con contenedores, observabilidad y runbooks de DR.",
				today.minusMonths(5), today.minusDays(10),
				bd("38000000"), bd("35200000"), bd("42000000")));

			repository.save(p(13L, "Plataforma BI Analytics", "EN_PROGRESO",
				"Data warehouse en la nube, pipelines ETL y tableros ejecutivos para ventas y operaciones.",
				today.minusDays(45), today.plusDays(30),
				bd("85000000"), bd("88000000"), bd("102000000")));

			repository.save(p(10L, "API Pagos Open Banking", "EN_PROGRESO",
				"Exposición de APIs REST para pagos, conciliación y webhooks bancarios cumpliendo normativa local.",
				today.minusDays(20), today.plusDays(75),
				bd("58000000"), bd("48500000"), bd("72000000")));

			repository.save(p(12L, "Portal RRHH Interno", "PLANIFICADO",
				"Self-service de vacaciones, evaluaciones de desempeño y onboarding digital para colaboradores.",
				today.plusDays(7), today.plusDays(90),
				bd("32000000"), bd("9800000"), bd("38000000")));

			repository.save(p(5L, "Chatbot Soporte IA", "EN_PROGRESO",
				"Asistente conversacional con RAG sobre base de conocimiento y escalamiento a agentes humanos.",
				today.minusDays(15), today.plusDays(45),
				bd("48000000"), bd("41200000"), bd("62000000")));

			repository.save(p(20L, "E-commerce Marketplace", "EN_PROGRESO",
				"Marketplace multi-vendor con checkout, logística y panel de vendedores.",
				today.minusDays(60), today.minusDays(5),
				bd("95000000"), bd("102000000"), bd("88000000")));

			repository.save(p(15L, "Auditoría Ciberseguridad", "COMPLETADO",
				"Pentesting, hardening y plan de remediación para infraestructura crítica.",
				today.minusMonths(2), today.minusDays(20),
				bd("28000000"), bd("26200000"), bd("32000000")));

			repository.save(p(7L, "Integración ERP-SAP", "CANCELADO",
				"Conectores bidireccionales SAP ↔ CRM; proyecto pausado por cambio de prioridad estratégica.",
				today.minusMonths(1), today.plusDays(10),
				bd("55000000"), bd("22000000"), bd("0")));
		};
	}

	private static Proyecto p(Long responsableId, String nombre, String estado, String descripcion,
			LocalDate inicio, LocalDate fin,
			BigDecimal presupuesto, BigDecimal costoReal, BigDecimal ingresos) {
		return new Proyecto(null, nombre, estado, descripcion, responsableId, inicio, fin,
			presupuesto, costoReal, ingresos);
	}

	private static BigDecimal bd(String value) {
		return new BigDecimal(value);
	}
}
