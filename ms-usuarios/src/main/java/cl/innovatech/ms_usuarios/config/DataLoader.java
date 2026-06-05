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
			if (repository.count() > 0) {
				return;
			}
			Object[][] trabajadores = {
				{"Ana Torres", "Desarrolladora Full Stack", "ana@innovatech.cl", 40, 1_200_000L},
				{"Carlos Ruiz", "Arquitecto de Software", "carlos@innovatech.cl", 35, 3_200_000L},
				{"María López", "Project Manager", "maria@innovatech.cl", 30, 2_800_000L},
				{"Diego Fernández", "Desarrollador Backend", "diego@innovatech.cl", 40, 1_800_000L},
				{"Valentina Soto", "UX/UI Designer", "valentina@innovatech.cl", 38, 1_600_000L},
				{"Jorge Muñoz", "DevOps Engineer", "jorge@innovatech.cl", 36, 2_500_000L},
				{"Camila Rojas", "QA Lead", "camila@innovatech.cl", 40, 2_200_000L},
				{"Andrés Vega", "Desarrollador Mobile", "andres@innovatech.cl", 40, 1_750_000L},
				{"Paula Herrera", "Business Analyst", "paula@innovatech.cl", 32, 1_900_000L},
				{"Felipe Ardiles", "Tech Lead", "felipe@innovatech.cl", 38, 4_500_000L},
				{"Ignacio Paredes", "Desarrollador Frontend", "ignacio@innovatech.cl", 40, 1_050_000L},
				{"Sofía Mendoza", "Scrum Master", "sofia@innovatech.cl", 30, 2_400_000L},
				{"Tomás Gil", "Ingeniero de Datos", "tomas@innovatech.cl", 36, 2_100_000L},
				{"Daniela Castro", "Diseñadora de Producto", "daniela@innovatech.cl", 38, 1_700_000L},
				{"Roberto Núñez", "Security Engineer", "roberto@innovatech.cl", 35, 3_000_000L},
				{"Laura Jiménez", "Desarrolladora Backend", "laura@innovatech.cl", 40, 1_850_000L},
				{"Miguel Álvarez", "SRE", "miguel@innovatech.cl", 36, 2_600_000L},
				{"Francisca Morales", "QA Automation", "francisca@innovatech.cl", 40, 1_350_000L},
				{"Sebastián Cruz", "Desarrollador Full Stack", "sebastian@innovatech.cl", 40, 1_950_000L},
				{"Natalia Fuentes", "Product Owner", "natalia@innovatech.cl", 28, 2_700_000L},
				{"Héctor Salinas", "DBA", "hector@innovatech.cl", 35, 2_900_000L},
				{"Gabriela Peña", "Desarrolladora Frontend", "gabriela@innovatech.cl", 40, 1_400_000L},
				{"Óscar Ramírez", "Cloud Architect", "oscar@innovatech.cl", 34, 5_200_000L},
				{"Isidora Lagos", "Technical Writer", "isidora@innovatech.cl", 30, 950_000L},
				{"Cristóbal Vargas", "Desarrollador Mobile", "cristobal@innovatech.cl", 40, 1_650_000L},
				{"Javiera Orellana", "UX Researcher", "javiera@innovatech.cl", 32, 1_550_000L},
				{"Matías Contreras", "Backend Lead", "matias@innovatech.cl", 38, 3_800_000L},
				{"Constanza Reyes", "QA Manual", "constanza@innovatech.cl", 40, 1_000_000L},
				{"Benjamín Silva", "DevOps", "benjamin@innovatech.cl", 36, 2_000_000L},
				{"Antonia Espinoza", "Analista Financiero TI", "antonia@innovatech.cl", 30, 2_300_000L},
			};
			for (Object[] t : trabajadores) {
				repository.save(new Usuario(null, (String) t[0], (String) t[1], (String) t[2],
					(Integer) t[3], (Long) t[4]));
			}
		};
	}
}
