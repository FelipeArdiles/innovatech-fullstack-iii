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
			repository.deleteAll();
			String[][] trabajadores = {
				{"Ana Torres", "Desarrolladora Full Stack", "ana@innovatech.cl", "40"},
				{"Carlos Ruiz", "Arquitecto de Software", "carlos@innovatech.cl", "35"},
				{"María López", "Project Manager", "maria@innovatech.cl", "30"},
				{"Diego Fernández", "Desarrollador Backend", "diego@innovatech.cl", "40"},
				{"Valentina Soto", "UX/UI Designer", "valentina@innovatech.cl", "38"},
				{"Jorge Muñoz", "DevOps Engineer", "jorge@innovatech.cl", "36"},
				{"Camila Rojas", "QA Lead", "camila@innovatech.cl", "40"},
				{"Andrés Vega", "Desarrollador Mobile", "andres@innovatech.cl", "40"},
				{"Paula Herrera", "Business Analyst", "paula@innovatech.cl", "32"},
				{"Felipe Ardiles", "Tech Lead", "felipe@innovatech.cl", "38"},
				{"Ignacio Paredes", "Desarrollador Frontend", "ignacio@innovatech.cl", "40"},
				{"Sofía Mendoza", "Scrum Master", "sofia@innovatech.cl", "30"},
				{"Tomás Gil", "Ingeniero de Datos", "tomas@innovatech.cl", "36"},
				{"Daniela Castro", "Diseñadora de Producto", "daniela@innovatech.cl", "38"},
				{"Roberto Núñez", "Security Engineer", "roberto@innovatech.cl", "35"},
				{"Laura Jiménez", "Desarrolladora Backend", "laura@innovatech.cl", "40"},
				{"Miguel Álvarez", "SRE", "miguel@innovatech.cl", "36"},
				{"Francisca Morales", "QA Automation", "francisca@innovatech.cl", "40"},
				{"Sebastián Cruz", "Desarrollador Full Stack", "sebastian@innovatech.cl", "40"},
				{"Natalia Fuentes", "Product Owner", "natalia@innovatech.cl", "28"},
				{"Héctor Salinas", "DBA", "hector@innovatech.cl", "35"},
				{"Gabriela Peña", "Desarrolladora Frontend", "gabriela@innovatech.cl", "40"},
				{"Óscar Ramírez", "Cloud Architect", "oscar@innovatech.cl", "34"},
				{"Isidora Lagos", "Technical Writer", "isidora@innovatech.cl", "30"},
				{"Cristóbal Vargas", "Desarrollador Mobile", "cristobal@innovatech.cl", "40"},
				{"Javiera Orellana", "UX Researcher", "javiera@innovatech.cl", "32"},
				{"Matías Contreras", "Backend Lead", "matias@innovatech.cl", "38"},
				{"Constanza Reyes", "QA Manual", "constanza@innovatech.cl", "40"},
				{"Benjamín Silva", "DevOps", "benjamin@innovatech.cl", "36"},
				{"Antonia Espinoza", "Analista Financiero TI", "antonia@innovatech.cl", "30"},
			};
			for (String[] t : trabajadores) {
				repository.save(new Usuario(null, t[0], t[1], t[2], Integer.parseInt(t[3])));
			}
		};
	}
}
