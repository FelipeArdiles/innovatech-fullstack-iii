package cl.innovatech.bff_gateway.service;

import cl.innovatech.bff_gateway.client.MicroserviceClient;
import cl.innovatech.bff_gateway.dto.CapacidadEquipoDto;
import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.FinanzasResumenDto;
import cl.innovatech.bff_gateway.dto.ProyectoDetalleDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.ProyectoFinanzasDto;
import cl.innovatech.bff_gateway.dto.TareaDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
			usuarioDto(1L, "Ana", "Dev", "ana@innovatech.cl", 40, 1_200_000L)
		));
		when(microserviceClient.getProyectos()).thenReturn(List.of(
			proyectoDto(1L, "Portal", "EN_PROGRESO", "Desc", 1L, null, null,
				new BigDecimal("72000000"), new BigDecimal("68000000"), new BigDecimal("92000000"))
		));
		List<TareaDto> tareas = List.of(
			tareaDto(1L, "UI", "POR_HACER", 1L, 1L, 8, "MEDIA", "6900", "DISENO"),
			tareaDto(2L, "API", "EN_PROGRESO", 1L, 2L, 16, "ALTA", "43200", "DESARROLLO"),
			tareaDto(3L, "Deploy", "HECHO", 2L, 3L, 4, "BAJA", "2000", "DEVOPS")
		);
		when(microserviceClient.getTareas()).thenReturn(tareas);
		when(microserviceClient.getTareas(null)).thenReturn(tareas);

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
		ProyectoDto proyecto = proyectoDto(1L, "Portal", "EN_PROGRESO", "Desc larga del portal", 1L, LocalDate.now(), fin,
			new BigDecimal("50000000"), new BigDecimal("30000000"), new BigDecimal("80000000"));
		List<TareaDto> tareasProyecto = List.of(
			tareaDto(1L, "UI", "POR_HACER", 1L, 1L, 10, "BAJA", "75000", "DISENO"),
			tareaDto(2L, "API", "HECHO", 1L, 2L, 5, "ALTA", "135000", "DESARROLLO")
		);
		when(microserviceClient.getProyecto(1L)).thenReturn(proyecto);
		when(microserviceClient.getTareas(1L)).thenReturn(tareasProyecto);
		when(microserviceClient.getUsuarios()).thenReturn(List.of(
			usuarioDto(1L, "Ana", "Dev", "ana@innovatech.cl", 40, 1_200_000L),
			usuarioDto(2L, "Carlos", "Arq", "carlos@innovatech.cl", 35, 3_200_000L)
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
	void getCapacidadEquipoCalculaSobrecargaYCosto() {
		when(microserviceClient.getUsuarios()).thenReturn(List.of(
			usuarioDto(1L, "Ana", "Dev", "ana@innovatech.cl", 10, 1_600_000L)
		));
		when(microserviceClient.getTareas(null)).thenReturn(List.of(
			tareaDto(1L, "UI", "POR_HACER", 1L, 1L, 15, "MEDIA", "172500", "DESARROLLO")
		));

		CapacidadEquipoDto capacidad = bffService.getCapacidadEquipo();

		assertThat(capacidad.getTrabajadoresSobrecargados()).isEqualTo(1);
		assertThat(capacidad.getTrabajadores().get(0).isSobrecargado()).isTrue();
		assertThat(capacidad.getTrabajadores().get(0).getPorcentajeCarga()).isEqualTo(150.0);
		assertThat(capacidad.getCostoMensualNominaClp()).isEqualByComparingTo(new BigDecimal("1600000"));
		assertThat(capacidad.getTrabajadores().get(0).getCostoHorasAsignadasClp())
			.isEqualByComparingTo(new BigDecimal("150000"));
	}

	@Test
	void getProyectoFinanzasCalculaMargenYDesglose() {
		when(microserviceClient.getUsuarios()).thenReturn(List.of(
			usuarioDto(1L, "Ana", "Dev", "ana@innovatech.cl", 40, 1_200_000L),
			usuarioDto(2L, "Carlos", "Arq", "carlos@innovatech.cl", 35, 3_200_000L)
		));
		when(microserviceClient.getProyecto(1L)).thenReturn(
			proyectoDto(1L, "Portal", "EN_PROGRESO", "Desc", 1L, null, null,
				new BigDecimal("72000000"), new BigDecimal("68000000"), new BigDecimal("92000000"))
		);
		when(microserviceClient.getTareas(1L)).thenReturn(List.of(
			tareaDto(1L, "UI", "POR_HACER", 1L, 1L, 8, "MEDIA", "69000", "DISENO"),
			tareaDto(2L, "API", "HECHO", 1L, 2L, 16, "ALTA", "432000", "DESARROLLO")
		));

		ProyectoFinanzasDto finanzas = bffService.getProyectoFinanzas(1L);

		assertThat(finanzas).isNotNull();
		assertThat(finanzas.getCostoTareasOperacional()).isEqualByComparingTo(new BigDecimal("501000"));
		assertThat(finanzas.getCostoSueldos()).isEqualByComparingTo(new BigDecimal("380000"));
		assertThat(finanzas.getOtrosGastos()).isEqualByComparingTo(new BigDecimal("8640000"));
		assertThat(finanzas.getCostoAcumulado()).isEqualByComparingTo(new BigDecimal("68000000"));
		assertThat(finanzas.getGanancia()).isEqualByComparingTo(new BigDecimal("24000000"));
		assertThat(finanzas.getMargenPorcentaje()).isEqualTo(26.1);
		assertThat(finanzas.getDesglosePorCategoria()).hasSize(2);
		assertThat(finanzas.isRentable()).isTrue();
	}

	@Test
	void getFinanzasResumenAgregaKpisEmpresa() {
		when(microserviceClient.getUsuarios()).thenReturn(List.of(
			usuarioDto(1L, "Ana", "Dev", "ana@innovatech.cl", 40, 1_200_000L)
		));
		when(microserviceClient.getProyectos()).thenReturn(List.of(
			proyectoDto(1L, "A", "EN_PROGRESO", "D", 1L, null, null,
				new BigDecimal("50000000"), new BigDecimal("30000000"), new BigDecimal("100000000")),
			proyectoDto(2L, "B", "CANCELADO", "D", 2L, null, null,
				null, null, new BigDecimal("50000000"))
		));
		when(microserviceClient.getTareas(null)).thenReturn(List.of(
			tareaDto(1L, "T1", "HECHO", 1L, 1L, 4, "BAJA", "30000", "DESARROLLO"),
			tareaDto(2L, "T2", "HECHO", 2L, 2L, 4, "BAJA", "10000", "QA")
		));

		FinanzasResumenDto resumen = bffService.getFinanzasResumen();

		assertThat(resumen.getIngresosTotales()).isEqualByComparingTo(new BigDecimal("100000000"));
		assertThat(resumen.getCostosTotales()).isEqualByComparingTo(new BigDecimal("30000000"));
		assertThat(resumen.getProyectosRentables()).isEqualTo(1);
		assertThat(resumen.getProyectos()).hasSize(1);
	}

	private static UsuarioDto usuarioDto(Long id, String nombre, String rol, String email, int horas, Long sueldo) {
		return new UsuarioDto(id, nombre, rol, email, horas, sueldo);
	}

	private static ProyectoDto proyectoDto(Long id, String nombre, String estado, String desc, Long resp,
			LocalDate inicio, LocalDate fin, BigDecimal presupuesto, BigDecimal costoReal, BigDecimal ingresos) {
		return new ProyectoDto(id, nombre, estado, desc, resp, inicio, fin, presupuesto, costoReal, ingresos, null);
	}

	private static TareaDto tareaDto(Long id, String titulo, String estado, long pid, long aid, int horas,
			String dificultad, String valor, String categoria) {
		return new TareaDto(id, titulo, "Desc", estado, pid, aid, horas, dificultad, new BigDecimal(valor), categoria);
	}
}
