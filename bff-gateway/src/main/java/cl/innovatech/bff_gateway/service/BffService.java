package cl.innovatech.bff_gateway.service;

import cl.innovatech.bff_gateway.client.MicroserviceClient;
import cl.innovatech.bff_gateway.dto.CapacidadEquipoDto;
import cl.innovatech.bff_gateway.dto.CapacidadTrabajadorDto;
import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.FinanzasCategoriaDto;
import cl.innovatech.bff_gateway.dto.FinanzasResumenDto;
import cl.innovatech.bff_gateway.dto.ProyectoDetalleDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.ProyectoFinanzasDto;
import cl.innovatech.bff_gateway.dto.TareaDto;
import cl.innovatech.bff_gateway.dto.TareaValorDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BffService {

	private static final int HORAS_MES_LABORALES = 160;
	private static final BigDecimal PCT_OTROS_GASTOS = new BigDecimal("0.08");

	private final MicroserviceClient microserviceClient;

	public BffService(MicroserviceClient microserviceClient) {
		this.microserviceClient = microserviceClient;
	}

	public DashboardDto getDashboard() {
		List<UsuarioDto> usuarios = microserviceClient.getUsuarios();
		List<ProyectoDto> proyectos = enrichProyectos(microserviceClient.getProyectos());
		List<TareaDto> tareas = microserviceClient.getTareas();
		long porHacer = tareas.stream().filter(t -> "POR_HACER".equals(t.getEstado())).count();
		long enProgreso = tareas.stream().filter(t -> "EN_PROGRESO".equals(t.getEstado())).count();
		long hechas = tareas.stream().filter(t -> "HECHO".equals(t.getEstado())).count();
		return new DashboardDto(
			usuarios.size(),
			proyectos.size(),
			tareas.size(),
			(int) porHacer,
			(int) enProgreso,
			(int) hechas,
			usuarios,
			proyectos,
			tareas
		);
	}

	public List<UsuarioDto> getUsuarios() {
		return microserviceClient.getUsuarios();
	}

	public UsuarioDto getUsuario(Long id) {
		return microserviceClient.getUsuario(id);
	}

	public UsuarioDto createUsuario(UsuarioDto usuario) {
		return microserviceClient.createUsuario(usuario);
	}

	public UsuarioDto updateUsuario(Long id, UsuarioDto usuario) {
		return microserviceClient.updateUsuario(id, usuario);
	}

	public void deleteUsuario(Long id) {
		microserviceClient.deleteUsuario(id);
	}

	public List<ProyectoDto> getProyectos() {
		return enrichProyectos(microserviceClient.getProyectos());
	}

	public ProyectoDto getProyecto(Long id) {
		ProyectoDto proyecto = microserviceClient.getProyecto(id);
		if (proyecto == null) {
			return null;
		}
		List<TareaDto> tareas = microserviceClient.getTareas(id);
		return enrichProyecto(proyecto, tareas, usuariosIndexados());
	}

	public ProyectoDetalleDto getProyectoDetalle(Long id) {
		ProyectoDto proyecto = getProyecto(id);
		if (proyecto == null) {
			return null;
		}
		List<TareaDto> tareas = microserviceClient.getTareas(id);
		List<UsuarioDto> todosUsuarios = microserviceClient.getUsuarios();
		Map<Long, UsuarioDto> usuariosPorId = todosUsuarios.stream()
			.collect(Collectors.toMap(UsuarioDto::getId, u -> u, (a, b) -> a));

		Set<Long> idsAsignados = new LinkedHashSet<>();
		if (proyecto.getResponsableId() != null) {
			idsAsignados.add(proyecto.getResponsableId());
		}
		tareas.stream()
			.map(TareaDto::getAsignadoId)
			.filter(Objects::nonNull)
			.forEach(idsAsignados::add);

		List<UsuarioDto> trabajadores = idsAsignados.stream()
			.map(usuariosPorId::get)
			.filter(Objects::nonNull)
			.toList();

		long porHacer = tareas.stream().filter(t -> "POR_HACER".equals(t.getEstado())).count();
		long enProgreso = tareas.stream().filter(t -> "EN_PROGRESO".equals(t.getEstado())).count();
		long hechas = tareas.stream().filter(t -> "HECHO".equals(t.getEstado())).count();
		int horasProyecto = tareas.stream()
			.map(TareaDto::getHorasEstimadas)
			.filter(Objects::nonNull)
			.mapToInt(Integer::intValue)
			.sum();

		boolean atrasado = proyecto.getFechaFin() != null
			&& proyecto.getFechaFin().isBefore(LocalDate.now())
			&& !"COMPLETADO".equals(proyecto.getEstado())
			&& !"CANCELADO".equals(proyecto.getEstado());

		return new ProyectoDetalleDto(
			proyecto.getId(),
			proyecto.getNombre(),
			proyecto.getEstado(),
			proyecto.getDescripcion(),
			proyecto.getResponsableId(),
			proyecto.getFechaInicio(),
			proyecto.getFechaFin(),
			proyecto.getPresupuesto(),
			proyecto.getCostoReal(),
			proyecto.getIngresosContrato(),
			proyecto.getMargenPorcentaje(),
			atrasado,
			(int) porHacer,
			(int) enProgreso,
			(int) hechas,
			horasProyecto,
			countDificultad(tareas, "BAJA"),
			countDificultad(tareas, "MEDIA"),
			countDificultad(tareas, "ALTA"),
			tareas,
			trabajadores
		);
	}

	public ProyectoFinanzasDto getProyectoFinanzas(Long id) {
		ProyectoDto proyecto = microserviceClient.getProyecto(id);
		if (proyecto == null) {
			return null;
		}
		List<TareaDto> tareas = microserviceClient.getTareas(id);
		Map<Long, UsuarioDto> usuariosPorId = usuariosIndexados();
		return buildProyectoFinanzas(proyecto, tareas, usuariosPorId);
	}

	public FinanzasResumenDto getFinanzasResumen() {
		List<ProyectoDto> proyectos = microserviceClient.getProyectos();
		List<TareaDto> todasTareas = microserviceClient.getTareas(null);
		Map<Long, UsuarioDto> usuariosPorId = usuariosIndexados();
		Map<Long, List<TareaDto>> tareasPorProyecto = todasTareas.stream()
			.filter(t -> t.getProyectoId() != null)
			.collect(Collectors.groupingBy(TareaDto::getProyectoId));

		List<ProyectoFinanzasDto> finanzasProyectos = proyectos.stream()
			.filter(p -> !"CANCELADO".equals(p.getEstado()))
			.map(p -> buildProyectoFinanzas(p, tareasPorProyecto.getOrDefault(p.getId(), List.of()), usuariosPorId))
			.sorted(Comparator.comparing(ProyectoFinanzasDto::getNombreProyecto))
			.toList();

		BigDecimal ingresosTotales = finanzasProyectos.stream()
			.map(ProyectoFinanzasDto::getIngresos)
			.filter(Objects::nonNull)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal costosTotales = finanzasProyectos.stream()
			.map(ProyectoFinanzasDto::getCostoAcumulado)
			.filter(Objects::nonNull)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal gananciaNeta = ingresosTotales.subtract(costosTotales);
		int rentables = (int) finanzasProyectos.stream().filter(ProyectoFinanzasDto::isRentable).count();

		return new FinanzasResumenDto(
			ingresosTotales,
			costosTotales,
			gananciaNeta,
			calcularMargenPorcentaje(gananciaNeta, ingresosTotales),
			proyectos.size(),
			rentables,
			finanzasProyectos
		);
	}

	public CapacidadEquipoDto getCapacidadEquipo() {
		List<UsuarioDto> usuarios = microserviceClient.getUsuarios();
		List<TareaDto> tareas = microserviceClient.getTareas(null);

		Map<Long, Integer> horasPorTrabajador = tareas.stream()
			.filter(t -> t.getAsignadoId() != null && t.getHorasEstimadas() != null)
			.collect(Collectors.groupingBy(
				TareaDto::getAsignadoId,
				Collectors.summingInt(TareaDto::getHorasEstimadas)
			));

		List<CapacidadTrabajadorDto> capacidades = new ArrayList<>();
		int sobrecargados = 0;
		int horasDisponiblesTotal = 0;
		int horasAsignadasTotal = 0;

		BigDecimal costoNominaMensual = BigDecimal.ZERO;
		BigDecimal costoHorasAsignadasTotal = BigDecimal.ZERO;

		for (UsuarioDto usuario : usuarios) {
			int disponibles = usuario.getCapacidadHoras() != null ? usuario.getCapacidadHoras() : 0;
			int asignadas = horasPorTrabajador.getOrDefault(usuario.getId(), 0);
			double porcentaje = disponibles > 0 ? (asignadas * 100.0) / disponibles : (asignadas > 0 ? 100.0 : 0.0);
			boolean sobrecargado = disponibles > 0 && asignadas > disponibles;
			if (sobrecargado) {
				sobrecargados++;
			}
			horasDisponiblesTotal += disponibles;
			horasAsignadasTotal += asignadas;
			Long sueldo = usuario.getSueldoMensualClp();
			if (sueldo != null) {
				costoNominaMensual = costoNominaMensual.add(BigDecimal.valueOf(sueldo));
			}
			BigDecimal costoHoras = costoHorasProrrateadas(asignadas, sueldo);
			costoHorasAsignadasTotal = costoHorasAsignadasTotal.add(costoHoras);
			capacidades.add(new CapacidadTrabajadorDto(
				usuario.getId(),
				usuario.getNombre(),
				usuario.getRol(),
				disponibles,
				asignadas,
				Math.round(porcentaje * 10.0) / 10.0,
				sobrecargado,
				sueldo,
				costoHoras
			));
		}

		return new CapacidadEquipoDto(
			usuarios.size(),
			sobrecargados,
			horasDisponiblesTotal,
			horasAsignadasTotal,
			costoNominaMensual,
			costoHorasAsignadasTotal,
			capacidades
		);
	}

	public ProyectoDto createProyecto(ProyectoDto proyecto) {
		return microserviceClient.createProyecto(proyecto);
	}

	public ProyectoDto updateProyecto(Long id, ProyectoDto proyecto) {
		return microserviceClient.updateProyecto(id, proyecto);
	}

	public void deleteProyecto(Long id) {
		microserviceClient.deleteProyecto(id);
	}

	public List<TareaDto> getTareas(Long proyectoId) {
		return microserviceClient.getTareas(proyectoId);
	}

	public TareaDto getTarea(Long id) {
		return microserviceClient.getTarea(id);
	}

	public TareaDto createTarea(TareaDto tarea) {
		return microserviceClient.createTarea(tarea);
	}

	public TareaDto updateTarea(Long id, TareaDto tarea) {
		return microserviceClient.updateTarea(id, tarea);
	}

	public void deleteTarea(Long id) {
		microserviceClient.deleteTarea(id);
	}

	private List<ProyectoDto> enrichProyectos(List<ProyectoDto> proyectos) {
		List<TareaDto> todasTareas = microserviceClient.getTareas(null);
		Map<Long, UsuarioDto> usuariosPorId = usuariosIndexados();
		Map<Long, List<TareaDto>> porProyecto = todasTareas.stream()
			.filter(t -> t.getProyectoId() != null)
			.collect(Collectors.groupingBy(TareaDto::getProyectoId));
		return proyectos.stream()
			.map(p -> enrichProyecto(p, porProyecto.getOrDefault(p.getId(), List.of()), usuariosPorId))
			.toList();
	}

	private ProyectoDto enrichProyecto(ProyectoDto proyecto, List<TareaDto> tareas, Map<Long, UsuarioDto> usuariosPorId) {
		BigDecimal costoAcumulado = calcularCostoAcumulado(proyecto, tareas, usuariosPorId);
		BigDecimal ingresos = zeroIfNull(proyecto.getIngresosContrato());
		BigDecimal ganancia = ingresos.subtract(costoAcumulado);
		proyecto.setMargenPorcentaje(calcularMargenPorcentaje(ganancia, ingresos));
		return proyecto;
	}

	private Map<Long, UsuarioDto> usuariosIndexados() {
		return microserviceClient.getUsuarios().stream()
			.collect(Collectors.toMap(UsuarioDto::getId, u -> u, (a, b) -> a));
	}

	private ProyectoFinanzasDto buildProyectoFinanzas(ProyectoDto proyecto, List<TareaDto> tareas,
			Map<Long, UsuarioDto> usuariosPorId) {
		BigDecimal costoTareas = sumValorTareas(tareas);
		BigDecimal costoSueldos = calcularCostoSueldosProyecto(tareas, usuariosPorId);
		BigDecimal otrosGastos = calcularOtrosGastos(proyecto.getPresupuesto());
		BigDecimal costoReal = zeroIfNull(proyecto.getCostoReal());
		BigDecimal costoCalculado = costoSueldos.add(costoTareas).add(otrosGastos);
		BigDecimal costoAcumulado = costoCalculado.max(costoReal);
		BigDecimal ingresos = zeroIfNull(proyecto.getIngresosContrato());
		BigDecimal ganancia = ingresos.subtract(costoAcumulado);
		Double margen = calcularMargenPorcentaje(ganancia, ingresos);

		Map<String, List<TareaDto>> porCategoria = tareas.stream()
			.filter(t -> t.getCategoria() != null)
			.collect(Collectors.groupingBy(TareaDto::getCategoria, LinkedHashMap::new, Collectors.toList()));

		List<FinanzasCategoriaDto> desgloseCategoria = porCategoria.entrySet().stream()
			.map(e -> new FinanzasCategoriaDto(
				e.getKey(),
				sumValorTareas(e.getValue()),
				e.getValue().size()
			))
			.sorted(Comparator.comparing(FinanzasCategoriaDto::getValor).reversed())
			.toList();

		List<TareaValorDto> desgloseTareas = tareas.stream()
			.sorted(Comparator.comparing(TareaDto::getValorMonetario, Comparator.nullsLast(Comparator.reverseOrder())))
			.map(t -> new TareaValorDto(
				t.getId(),
				t.getTitulo(),
				t.getCategoria(),
				t.getDificultad(),
				t.getHorasEstimadas(),
				t.getValorMonetario()
			))
			.toList();

		return new ProyectoFinanzasDto(
			proyecto.getId(),
			proyecto.getNombre(),
			proyecto.getPresupuesto(),
			costoSueldos,
			costoTareas,
			otrosGastos,
			costoAcumulado,
			costoReal,
			ingresos,
			ganancia,
			margen,
			ganancia.compareTo(BigDecimal.ZERO) > 0,
			desgloseCategoria,
			desgloseTareas
		);
	}

	private BigDecimal calcularCostoAcumulado(ProyectoDto proyecto, List<TareaDto> tareas,
			Map<Long, UsuarioDto> usuariosPorId) {
		BigDecimal costoSueldos = calcularCostoSueldosProyecto(tareas, usuariosPorId);
		BigDecimal costoTareas = sumValorTareas(tareas);
		BigDecimal otrosGastos = calcularOtrosGastos(proyecto.getPresupuesto());
		BigDecimal costoCalculado = costoSueldos.add(costoTareas).add(otrosGastos);
		return costoCalculado.max(zeroIfNull(proyecto.getCostoReal()));
	}

	private static BigDecimal calcularCostoSueldosProyecto(List<TareaDto> tareas, Map<Long, UsuarioDto> usuariosPorId) {
		Map<Long, Integer> horasPorTrabajador = tareas.stream()
			.filter(t -> t.getAsignadoId() != null && t.getHorasEstimadas() != null)
			.collect(Collectors.groupingBy(
				TareaDto::getAsignadoId,
				Collectors.summingInt(TareaDto::getHorasEstimadas)
			));
		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<Long, Integer> entry : horasPorTrabajador.entrySet()) {
			UsuarioDto usuario = usuariosPorId.get(entry.getKey());
			Long sueldo = usuario != null ? usuario.getSueldoMensualClp() : null;
			total = total.add(costoHorasProrrateadas(entry.getValue(), sueldo));
		}
		return total;
	}

	private static BigDecimal costoHorasProrrateadas(int horas, Long sueldoMensualClp) {
		if (horas <= 0 || sueldoMensualClp == null || sueldoMensualClp <= 0) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(sueldoMensualClp)
			.multiply(BigDecimal.valueOf(horas))
			.divide(BigDecimal.valueOf(HORAS_MES_LABORALES), 0, RoundingMode.HALF_UP);
	}

	private static BigDecimal calcularOtrosGastos(BigDecimal presupuesto) {
		if (presupuesto == null || presupuesto.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}
		return presupuesto.multiply(PCT_OTROS_GASTOS).setScale(0, RoundingMode.HALF_UP);
	}

	private static BigDecimal sumValorTareas(List<TareaDto> tareas) {
		return tareas.stream()
			.map(TareaDto::getValorMonetario)
			.filter(Objects::nonNull)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private static BigDecimal zeroIfNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	private static Double calcularMargenPorcentaje(BigDecimal ganancia, BigDecimal ingresos) {
		if (ingresos == null || ingresos.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}
		return ganancia.multiply(BigDecimal.valueOf(100))
			.divide(ingresos, 1, RoundingMode.HALF_UP)
			.doubleValue();
	}

	private static int countDificultad(List<TareaDto> tareas, String dificultad) {
		return (int) tareas.stream().filter(t -> dificultad.equals(t.getDificultad())).count();
	}
}
