package cl.innovatech.apigateway.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CorsConfig.class)
class CorsConfigTest {

	@Autowired
	private CorsFilter corsFilter;

	@Test
	void corsFilterAllowsLocalFrontendOrigin() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/test");
		request.addHeader(HttpHeaders.ORIGIN, "http://localhost:5173");
		request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET");

		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = (req, res) -> {};

		corsFilter.doFilter(request, response, chain);

		assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
			.isEqualTo("http://localhost:5173");
		assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))
			.contains("GET", "POST");
	}
}
