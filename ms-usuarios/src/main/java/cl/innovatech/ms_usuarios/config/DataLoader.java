package cl.innovatech.ms_usuarios.config;

import cl.innovatech.ms_usuarios.entity.Usuario;
import cl.innovatech.ms_usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

	@Bean
	CommandLineRunner initUsuarios(UsuarioRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				repository.save(new Usuario(null, "Ana Torres", "Desarrolladora", "ana@innovatech.cl", 40));
				repository.save(new Usuario(null, "Carlos Ruiz", "Arquitecto", "carlos@innovatech.cl", 35));
				repository.save(new Usuario(null, "María López", "PM", "maria@innovatech.cl", 30));
			}
		};
	}
}
