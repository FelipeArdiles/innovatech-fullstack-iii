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
			if (repository.count() == 0) {
				repository.save(new Proyecto(null, "Portal Clientes", "EN_PROGRESO",
					"Modernización del portal web", 1L,
					LocalDate.now().minusDays(14), LocalDate.now().plusDays(45), new BigDecimal("85000")));
				repository.save(new Proyecto(null, "App Móvil Retail", "PLANIFICADO",
					"Aplicación para cadena retail", 2L,
					LocalDate.now().plusDays(7), LocalDate.now().plusDays(90), new BigDecimal("120000")));
				repository.save(new Proyecto(null, "Migración Cloud", "COMPLETADO",
					"Migración de servicios legacy", 3L,
					LocalDate.now().minusMonths(3), LocalDate.now().minusDays(5), new BigDecimal("45000")));
			}
		};
	}
}
