package cl.innovatech.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CorsConfig.class)
class CorsConfigTest {

	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

	@Test
	void corsConfigurationAllowsLocalFrontendOrigin() {
		MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/test");
		request.addHeader(HttpHeaders.ORIGIN, "http://localhost:5173");
		request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET");

		CorsConfiguration config = corsConfigurationSource.getCorsConfiguration(request);

		assertThat(config).isNotNull();
		assertThat(config.getAllowedOrigins()).contains("http://localhost:5173");
		assertThat(config.getAllowedMethods()).contains("GET", "POST", "OPTIONS");
	}
}
