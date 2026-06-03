package cl.innovatech.ms_tareas.config;

import cl.innovatech.ms_tareas.entity.Tarea;
import cl.innovatech.ms_tareas.repository.TareaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

	@Bean
	CommandLineRunner initTareas(TareaRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				repository.save(new Tarea(null, "Diseñar UI login", "Mockups y prototipo Figma", "POR_HACER", 1L, 1L, 16));
				repository.save(new Tarea(null, "API autenticación", "Integrar Keycloak OIDC", "EN_PROGRESO", 1L, 2L, 24));
				repository.save(new Tarea(null, "Deploy staging", "Pipeline CI/CD en cloud", "HECHO", 2L, 3L, 8));
				repository.save(new Tarea(null, "Migrar base de datos", "Scripts y rollback plan", "POR_HACER", 3L, 1L, 20));
				repository.save(new Tarea(null, "Pruebas E2E", "Cypress sobre flujos críticos", "EN_PROGRESO", 2L, 2L, 12));
			}
		};
	}
}
