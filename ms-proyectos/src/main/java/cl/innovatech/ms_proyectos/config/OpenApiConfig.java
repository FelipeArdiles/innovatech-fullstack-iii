package cl.innovatech.ms_proyectos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI msProyectosOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Innovatech – Microservicio Proyectos")
				.description("API REST CRUD de proyectos tecnológicos. Servicio interno; "
					+ "el cliente expuesto consume el BFF vía API Gateway.")
				.version("1.0.0")
				.contact(new Contact().name("Innovatech Solutions").email("contacto@innovatech.cl"))
				.license(new License().name("Uso académico Duoc UC")));
	}
}
