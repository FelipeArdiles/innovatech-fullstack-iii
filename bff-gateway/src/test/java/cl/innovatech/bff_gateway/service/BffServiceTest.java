package cl.innovatech.bff_gateway.service;

import cl.innovatech.bff_gateway.client.MicroserviceClient;
import cl.innovatech.bff_gateway.dto.CapacidadEquipoDto;
import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.ProyectoDetalleDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.TareaDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
			new ProyectoDto(1L, "Portal", "EN_PROGRESO", "Desc", 1L, null, null, null)
		));
		when(microserviceClient.getTareas()).thenReturn(List.of(
			new TareaDto(1L, "UI", "Mockups", "POR_HACER", 1L, 1L, 8),
			new TareaDto(2L, "API", "Keycloak", "EN_PROGRESO", 1L, 2L, 16),
			new TareaDto(3L, "Deploy", "CI/CD", "HECHO", 2L, 3L, 4)
		));

		DashboardDto dashboard = bffService.getDashboard();

		assertThat(dashboard.getTotalUsuarios()).isEqualTo(1);
		assertThat(dashboard.getTotalProyectos()).isEqualTo(1);
		assertThat(dashboard.getTotalTareas()).isEqualTo(3);
		assertThat(dashboard.getTareasPorHacer()).isEqualTo(1);
		assertThat(dashboard.getTareasEnProgreso()).isEqualTo(1);
		assertThat(dashboard.getTareasHechas()).isEqualTo(1);
	}

	@Test
	void getProyectoDetalleAggregatesTareasYTrabajadores() {
		LocalDate fin = LocalDate.now().plusDays(30);
		when(microserviceClient.getProyecto(1L)).thenReturn(
			new ProyectoDto(1L, "Portal", "EN_PROGRESO", "Desc", 1L, LocalDate.now(), fin, null)
		);
		when(microserviceClient.getTareas(1L)).thenReturn(List.of(
			new TareaDto(1L, "UI", "Mockups", "POR_HACER", 1L, 1L, 10),
			new TareaDto(2L, "API", "Keycloak", "HECHO", 1L, 2L, 5)
		));
		when(microserviceClient.getUsuarios()).thenReturn(List.of(
			new UsuarioDto(1L, "Ana", "Dev", "ana@innovatech.cl", 40),
			new UsuarioDto(2L, "Carlos", "Arq", "carlos@innovatech.cl", 35)
		));

		ProyectoDetalleDto detalle = bffService.getProyectoDetalle(1L);

		assertThat(detalle).isNotNull();
		assertThat(detalle.getTareas()).hasSize(2);
		assertThat(detalle.getTrabajadores()).hasSize(2);
		assertThat(detalle.getTareasPorHacer()).isEqualTo(1);
		assertThat(detalle.getTareasHechas()).isEqualTo(1);
		assertThat(detalle.getHorasProyectoEstimadas()).isEqualTo(15);
		assertThat(detalle.isAtrasado()).isFalse();
	}

	@Test
	void getCapacidadEquipoCalculaSobrecarga() {
		when(microserviceClient.getUsuarios()).thenReturn(List.of(
			new UsuarioDto(1L, "Ana", "Dev", "ana@innovatech.cl", 10)
		));
		when(microserviceClient.getTareas(null)).thenReturn(List.of(
			new TareaDto(1L, "UI", "Mockups", "POR_HACER", 1L, 1L, 15)
		));

		CapacidadEquipoDto capacidad = bffService.getCapacidadEquipo();

		assertThat(capacidad.getTrabajadoresSobrecargados()).isEqualTo(1);
		assertThat(capacidad.getTrabajadores().get(0).isSobrecargado()).isTrue();
		assertThat(capacidad.getTrabajadores().get(0).getPorcentajeCarga()).isEqualTo(150.0);
	}
}
