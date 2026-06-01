package cl.innovatech.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/innovatech",
	"eureka.client.enabled=false",
	"spring.cloud.discovery.enabled=false"
})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}
}
