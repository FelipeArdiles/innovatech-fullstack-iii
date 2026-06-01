package cl.innovatech.ms_usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI msUsuariosOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Innovatech – Microservicio Usuarios")
				.description("API REST CRUD de usuarios (recursos humanos). Servicio interno; "
					+ "el cliente expuesto consume el BFF vía API Gateway.")
				.version("1.0.0")
				.contact(new Contact().name("Innovatech Solutions").email("contacto@innovatech.cl"))
				.license(new License().name("Uso académico Duoc UC")));
	}
}
