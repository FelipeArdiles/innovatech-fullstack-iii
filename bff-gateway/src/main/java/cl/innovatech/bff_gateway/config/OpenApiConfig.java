package cl.innovatech.bff_gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	private static final String BEARER_AUTH = "bearer-jwt";

	@Bean
	OpenAPI bffOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Innovatech – BFF (Backend for Frontend)")
				.description("Orquestación de usuarios y proyectos. Desde el exterior se accede "
					+ "por API Gateway con JWT de Keycloak (realm innovatech).")
				.version("1.0.0")
				.contact(new Contact().name("Innovatech Solutions").email("contacto@innovatech.cl"))
				.license(new License().name("Uso académico Duoc UC")))
			.servers(List.of(
				new Server().url("http://localhost:8080").description("API Gateway (producción expuesta)"),
				new Server().url("http://localhost:8081").description("BFF directo (desarrollo)")))
			.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
			.components(new Components()
				.addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
					.name(BEARER_AUTH)
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.description("Token OIDC de Keycloak. Obtener con el cliente innovatech-frontend "
						+ "(usuario demo: demo / demo123).")));
	}
}
