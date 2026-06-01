package cl.innovatech.ms_proyectos.config;

import cl.innovatech.ms_proyectos.entity.Proyecto;
import cl.innovatech.ms_proyectos.repository.ProyectoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

	@Bean
	CommandLineRunner initProyectos(ProyectoRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				repository.save(new Proyecto(null, "Portal Clientes", "EN_PROGRESO", "Modernización del portal web", 1L));
				repository.save(new Proyecto(null, "App Móvil Retail", "PLANIFICADO", "Aplicación para cadena retail", 2L));
				repository.save(new Proyecto(null, "Migración Cloud", "COMPLETADO", "Migración de servicios legacy", 3L));
			}
		};
	}
}
