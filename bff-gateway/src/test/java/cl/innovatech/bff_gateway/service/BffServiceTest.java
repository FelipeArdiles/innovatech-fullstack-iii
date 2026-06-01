package cl.innovatech.bff_gateway.service;

import cl.innovatech.bff_gateway.client.MicroserviceClient;
import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BffServiceTest {

	@Mock
	private MicroserviceClient microserviceClient;

	@InjectMocks
	private BffService bffService;

	@Test
	void getDashboardAggregatesMicroserviceData() {
		when(microserviceClient.getUsuarios()).thenReturn(List.of(
			new UsuarioDto(1L, "Ana", "Dev", "ana@innovatech.cl", 40)
		));
		when(microserviceClient.getProyectos()).thenReturn(List.of(
			new ProyectoDto(1L, "Portal", "EN_PROGRESO", "Desc", 1L)
		));

		DashboardDto dashboard = bffService.getDashboard();

		assertThat(dashboard.getTotalUsuarios()).isEqualTo(1);
		assertThat(dashboard.getTotalProyectos()).isEqualTo(1);
		assertThat(dashboard.getUsuarios()).hasSize(1);
		assertThat(dashboard.getProyectos()).hasSize(1);
	}
}
