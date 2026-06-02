package cl.innovatech.bff_gateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	/** Sin load balancer: Eureka y otros clientes HTTP directos no deben resolver hosts como servicios. */
	@Bean
	@Primary
	RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	@Bean
	@LoadBalanced
	@Qualifier("loadBalancedRestClientBuilder")
	RestClient.Builder loadBalancedRestClientBuilder() {
		return RestClient.builder();
	}
}
