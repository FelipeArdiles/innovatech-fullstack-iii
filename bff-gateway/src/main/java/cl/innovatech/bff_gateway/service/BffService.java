package cl.innovatech.bff_gateway.service;

import cl.innovatech.bff_gateway.client.MicroserviceClient;
import cl.innovatech.bff_gateway.dto.CapacidadEquipoDto;
import cl.innovatech.bff_gateway.dto.CapacidadTrabajadorDto;
import cl.innovatech.bff_gateway.dto.ComentarioTareaDto;
import cl.innovatech.bff_gateway.dto.CrearComentarioRequest;
import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.FinanzasCategoriaDto;
import cl.innovatech.bff_gateway.dto.FinanzasResumenDto;
import cl.innovatech.bff_gateway.dto.MiPanelDto;
import cl.innovatech.bff_gateway.dto.NotificacionDto;
import cl.innovatech.bff_gateway.dto.ProyectoDetalleDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.ProyectoFinanzasDto;
import cl.innovatech.bff_gateway.dto.ProyectoMiembroDto;
import cl.innovatech.bff_gateway.dto.ProyectoResumenTrabajadorDto;
import cl.innovatech.bff_gateway.dto.TareaDto;
import cl.innovatech.bff_gateway.dto.TareaValorDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
	private static final BigDecimal PCT_OTROS_GASTOS = new BigDecimal("0.12");
	private static final int DIAS_ALERTA_PLAZO = 3;

	private static final String TIPO_TAREA_ASIGNADA = "TAREA_ASIGNADA";
	private static final String TIPO_TAREA_COMPLETADA = "TAREA_COMPLETADA";
	private static final String TIPO_PLAZO_PROXIMO = "PLAZO_PROXIMO";
	private static final String TIPO_PLAZO_VENCIDO = "PLAZO_VENCIDO";
	private static final String TIPO_AVISO_PROYECTO = "AVISO_PROYECTO";
	private static final String TIPO_MIEMBRO_AGREGADO = "MIEMBRO_AGREGADO";
	private static final String TIPO_MIEMBRO_REMOVIDO = "MIEMBRO_REMOVIDO";
	private static final String TIPO_COMENTARIO_TAREA = "COMENTARIO_TAREA";

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

		Set<Long> idsEquipo = new LinkedHashSet<>();
		if (proyecto.getResponsableId() != null) {
			idsEquipo.add(proyecto.getResponsableId());
		}
		tareas.stream()
			.map(TareaDto::getAsignadoId)
			.filter(Objects::nonNull)
			.forEach(idsEquipo::add);
		microserviceClient.getMiembrosProyecto(id).stream()
			.map(ProyectoMiembroDto::getTrabajadorId)
			.filter(Objects::nonNull)
			.forEach(idsEquipo::add);

		List<UsuarioDto> trabajadores = idsEquipo.stream()
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
		TareaDto creada = microserviceClient.createTarea(tarea);
		if (creada != null && creada.getAsignadoId() != null) {
			notificarTareaAsignada(creada);
		}
		return creada;
	}

	public TareaDto updateTarea(Long id, TareaDto tarea) {
		TareaDto anterior = microserviceClient.getTarea(id);
		TareaDto actualizada = microserviceClient.updateTarea(id, tarea);
		if (actualizada != null && anterior != null) {
			if (actualizada.getAsignadoId() != null
				&& !Objects.equals(anterior.getAsignadoId(), actualizada.getAsignadoId())) {
				notificarTareaAsignada(actualizada);
			}
			if ("HECHO".equals(actualizada.getEstado()) && !"HECHO".equals(anterior.getEstado())) {
				notificarTareaCompletada(actualizada);
			}
		}
		return actualizada;
	}

	public void deleteTarea(Long id) {
		microserviceClient.deleteTarea(id);
	}

	public List<ProyectoMiembroDto> getMiembrosProyecto(Long proyectoId) {
		return microserviceClient.getMiembrosProyecto(proyectoId);
	}

	public ProyectoMiembroDto agregarMiembroProyecto(Long proyectoId, Long trabajadorId) {
		ProyectoMiembroDto miembro = microserviceClient.agregarMiembro(proyectoId, trabajadorId);
		if (miembro != null) {
			notificarMiembroAgregado(proyectoId, trabajadorId);
		}
		return miembro;
	}

	public void quitarMiembroProyecto(Long proyectoId, Long trabajadorId) {
		microserviceClient.quitarMiembro(proyectoId, trabajadorId);
		notificarMiembroRemovido(proyectoId, trabajadorId);
	}

	public int publicarAvisoProyecto(Long proyectoId, String mensaje, String email, String username) {
		if (mensaje == null || mensaje.isBlank()) {
			return 0;
		}
		ProyectoDto proyecto = microserviceClient.getProyecto(proyectoId);
		if (proyecto == null) {
			return 0;
		}
		UsuarioDto autor = resolverPerfil(email, username);
		String autorNombre = autor != null ? autor.getNombre() : "Administración";
		Set<Long> destinatarios = destinatariosProyecto(proyectoId, proyecto.getResponsableId());
		if (destinatarios.isEmpty()) {
			return 0;
		}
		String texto = truncar(mensaje.trim(), 500);
		String cuerpo = autorNombre + " publicó un aviso en \"" + proyecto.getNombre() + "\": " + texto;
		List<NotificacionDto> notificaciones = destinatarios.stream()
			.map(destId -> new NotificacionDto(
				null, destId, TIPO_AVISO_PROYECTO, cuerpo, false, null, proyectoId, LocalDateTime.now()))
			.toList();
		microserviceClient.createNotificacionesBatch(notificaciones);
		return notificaciones.size();
	}

	public List<ComentarioTareaDto> getComentariosTarea(Long tareaId) {
		return microserviceClient.getComentarios(tareaId);
	}

	public ComentarioTareaDto crearComentarioTarea(CrearComentarioRequest request, String email, String username) {
		UsuarioDto autor = resolverPerfil(email, username);
		if (autor == null || request.getTareaId() == null || request.getTexto() == null || request.getTexto().isBlank()) {
			return null;
		}
		TareaDto tarea = microserviceClient.getTarea(request.getTareaId());
		if (tarea == null) {
			return null;
		}
		ComentarioTareaDto comentario = new ComentarioTareaDto(
			null,
			request.getTareaId(),
			autor.getId(),
			autor.getNombre(),
			request.getTexto().trim(),
			LocalDateTime.now()
		);
		ComentarioTareaDto creado = microserviceClient.createComentario(comentario);
		if (creado == null) {
			return null;
		}
		enviarNotificacionesComentario(creado, tarea, autor);
		return creado;
	}

	private void enviarNotificacionesComentario(ComentarioTareaDto comentario, TareaDto tarea, UsuarioDto autor) {
		Long proyectoId = tarea.getProyectoId();
		ProyectoDto proyecto = proyectoId != null ? microserviceClient.getProyecto(proyectoId) : null;
		Set<Long> destinatarios = new LinkedHashSet<>();

		if (proyecto != null && proyecto.getResponsableId() != null) {
			destinatarios.add(proyecto.getResponsableId());
		}
		if (tarea.getAsignadoId() != null) {
			destinatarios.add(tarea.getAsignadoId());
		}
		if (proyectoId != null) {
			microserviceClient.getMiembrosProyecto(proyectoId).stream()
				.map(ProyectoMiembroDto::getTrabajadorId)
				.filter(Objects::nonNull)
				.forEach(destinatarios::add);
		}
		destinatarios.remove(autor.getId());

		String proyectoNombre = proyecto != null ? proyecto.getNombre() : "proyecto";
		String mensaje = autor.getNombre() + " comentó en \"" + tarea.getTitulo() + "\" (" + proyectoNombre + "): "
			+ truncar(comentario.getTexto(), 120);

		List<NotificacionDto> notificaciones = destinatarios.stream()
			.map(destId -> new NotificacionDto(
				null,
				destId,
				TIPO_COMENTARIO_TAREA,
				mensaje,
				false,
				tarea.getId(),
				proyectoId,
				LocalDateTime.now()
			))
			.toList();
		microserviceClient.createNotificacionesBatch(notificaciones);
	}

	private static String truncar(String texto, int max) {
		if (texto == null) {
			return "";
		}
		return texto.length() <= max ? texto : texto.substring(0, max - 3) + "...";
	}

	public List<NotificacionDto> getNotificacionesUsuario(String email, String username) {
		UsuarioDto perfil = resolverPerfil(email, username);
		if (perfil == null) {
			return List.of();
		}
		generarAlertasPlazosSeguro(perfil);
		return microserviceClient.getNotificaciones(perfil.getId());
	}

	public long getNotificacionesPendientes(String email, String username) {
		UsuarioDto perfil = resolverPerfil(email, username);
		if (perfil == null) {
			return 0L;
		}
		generarAlertasPlazosSeguro(perfil);
		return microserviceClient.getNotificacionesPendientes(perfil.getId());
	}

	public NotificacionDto marcarNotificacionLeida(Long id) {
		return microserviceClient.marcarNotificacionLeida(id);
	}

	public int marcarTodasNotificacionesLeidas(String email, String username) {
		UsuarioDto perfil = resolverPerfil(email, username);
		if (perfil == null) {
			return 0;
		}
		return microserviceClient.marcarTodasNotificacionesLeidas(perfil.getId());
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

	public MiPanelDto getMiPanel(String email, String preferredUsername) {
		UsuarioDto perfil = resolverPerfil(email, preferredUsername);
		if (perfil == null) {
			return null;
		}
		List<TareaDto> todas = microserviceClient.getTareas(null);
		List<TareaDto> asignadas = todas.stream()
			.filter(t -> perfil.getId().equals(t.getAsignadoId()))
			.sorted(Comparator.comparing(TareaDto::getEstado).thenComparing(TareaDto::getTitulo))
			.toList();

		Map<Long, ProyectoDto> proyectosPorId = microserviceClient.getProyectos().stream()
			.collect(Collectors.toMap(ProyectoDto::getId, p -> p, (a, b) -> a, LinkedHashMap::new));

		Set<Long> proyectoIds = asignadas.stream()
			.map(TareaDto::getProyectoId)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(LinkedHashSet::new));
		microserviceClient.getMiembrosPorTrabajador(perfil.getId()).stream()
			.map(ProyectoMiembroDto::getProyectoId)
			.filter(Objects::nonNull)
			.forEach(proyectoIds::add);

		List<ProyectoResumenTrabajadorDto> proyectos = proyectoIds.stream()
			.map(proyectosPorId::get)
			.filter(Objects::nonNull)
			.map(p -> buildProyectoResumenTrabajador(p, asignadas))
			.sorted(Comparator.comparing(ProyectoResumenTrabajadorDto::getNombre))
			.toList();

		int horas = asignadas.stream()
			.map(TareaDto::getHorasEstimadas)
			.filter(Objects::nonNull)
			.mapToInt(Integer::intValue)
			.sum();

		return new MiPanelDto(
			perfil,
			perfil.getRol() != null ? perfil.getRol() : "Trabajador",
			"trabajador",
			asignadas.size(),
			(int) asignadas.stream().filter(t -> "POR_HACER".equals(t.getEstado())).count(),
			(int) asignadas.stream().filter(t -> "EN_PROGRESO".equals(t.getEstado())).count(),
			(int) asignadas.stream().filter(t -> "HECHO".equals(t.getEstado())).count(),
			horas,
			asignadas,
			proyectos
		);
	}

	private UsuarioDto resolverPerfil(String email, String preferredUsername) {
		List<UsuarioDto> usuarios = microserviceClient.getUsuarios();
		if (email != null && !email.isBlank()) {
			UsuarioDto byEmail = usuarios.stream()
				.filter(u -> email.equalsIgnoreCase(u.getEmail()))
				.findFirst()
				.orElse(null);
			if (byEmail != null) {
				return byEmail;
			}
		}
		if (preferredUsername != null && !preferredUsername.isBlank()) {
			String guessed = preferredUsername.contains("@")
				? preferredUsername
				: preferredUsername + "@innovatech.cl";
			UsuarioDto byGuess = usuarios.stream()
				.filter(u -> guessed.equalsIgnoreCase(u.getEmail()))
				.findFirst()
				.orElse(null);
			if (byGuess != null) {
				return byGuess;
			}
		}
		return null;
	}

	private ProyectoResumenTrabajadorDto buildProyectoResumenTrabajador(ProyectoDto p, List<TareaDto> asignadas) {
		List<TareaDto> delProyecto = asignadas.stream()
			.filter(t -> p.getId().equals(t.getProyectoId()))
			.toList();
		Integer dias = calcularDiasRestantes(p.getFechaFin());
		boolean atrasado = dias != null && dias < 0
			&& !"COMPLETADO".equals(p.getEstado())
			&& !"CANCELADO".equals(p.getEstado());
		return new ProyectoResumenTrabajadorDto(
			p.getId(),
			p.getNombre(),
			p.getEstado(),
			p.getDescripcion(),
			p.getFechaInicio(),
			p.getFechaFin(),
			dias,
			atrasado,
			delProyecto.size(),
			(int) delProyecto.stream().filter(t -> "POR_HACER".equals(t.getEstado())).count(),
			(int) delProyecto.stream().filter(t -> "EN_PROGRESO".equals(t.getEstado())).count(),
			(int) delProyecto.stream().filter(t -> "HECHO".equals(t.getEstado())).count()
		);
	}

	private static Integer calcularDiasRestantes(LocalDate fechaFin) {
		if (fechaFin == null) {
			return null;
		}
		return (int) ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);
	}

	private void notificarTareaAsignada(TareaDto tarea) {
		if (tarea.getAsignadoId() == null) {
			return;
		}
		ProyectoDto proyecto = tarea.getProyectoId() != null
			? microserviceClient.getProyecto(tarea.getProyectoId()) : null;
		String proyectoNombre = proyecto != null ? proyecto.getNombre() : "un proyecto";
		String mensaje = "Se te asignó la tarea \"" + tarea.getTitulo() + "\" en " + proyectoNombre + ".";
		microserviceClient.createNotificacionesBatch(List.of(new NotificacionDto(
			null, tarea.getAsignadoId(), TIPO_TAREA_ASIGNADA, mensaje, false,
			tarea.getId(), tarea.getProyectoId(), LocalDateTime.now())));
	}

	private void notificarTareaCompletada(TareaDto tarea) {
		boolean importante = "ALTA".equals(tarea.getDificultad());
		if (!importante) {
			return;
		}
		Long proyectoId = tarea.getProyectoId();
		ProyectoDto proyecto = proyectoId != null ? microserviceClient.getProyecto(proyectoId) : null;
		Set<Long> destinatarios = destinatariosProyecto(proyectoId,
			proyecto != null ? proyecto.getResponsableId() : null);
		if (tarea.getAsignadoId() != null) {
			destinatarios.remove(tarea.getAsignadoId());
		}
		if (destinatarios.isEmpty()) {
			return;
		}
		String proyectoNombre = proyecto != null ? proyecto.getNombre() : "proyecto";
		String mensaje = "Tarea importante completada: \"" + tarea.getTitulo() + "\" en " + proyectoNombre + ".";
		List<NotificacionDto> notificaciones = destinatarios.stream()
			.map(destId -> new NotificacionDto(
				null, destId, TIPO_TAREA_COMPLETADA, mensaje, false,
				tarea.getId(), proyectoId, LocalDateTime.now()))
			.toList();
		microserviceClient.createNotificacionesBatch(notificaciones);
	}

	private void notificarMiembroAgregado(Long proyectoId, Long trabajadorId) {
		ProyectoDto proyecto = microserviceClient.getProyecto(proyectoId);
		if (proyecto == null) {
			return;
		}
		String mensaje = "Fuiste agregado al proyecto \"" + proyecto.getNombre() + "\".";
		microserviceClient.createNotificacionesBatch(List.of(new NotificacionDto(
			null, trabajadorId, TIPO_MIEMBRO_AGREGADO, mensaje, false,
			null, proyectoId, LocalDateTime.now())));
	}

	private void notificarMiembroRemovido(Long proyectoId, Long trabajadorId) {
		ProyectoDto proyecto = microserviceClient.getProyecto(proyectoId);
		if (proyecto == null) {
			return;
		}
		String mensaje = "Fuiste removido del proyecto \"" + proyecto.getNombre() + "\".";
		microserviceClient.createNotificacionesBatch(List.of(new NotificacionDto(
			null, trabajadorId, TIPO_MIEMBRO_REMOVIDO, mensaje, false,
			null, proyectoId, LocalDateTime.now())));
	}

	private Set<Long> destinatariosProyecto(Long proyectoId, Long responsableId) {
		Set<Long> destinatarios = new LinkedHashSet<>();
		if (responsableId != null) {
			destinatarios.add(responsableId);
		}
		if (proyectoId != null) {
			microserviceClient.getMiembrosProyecto(proyectoId).stream()
				.map(ProyectoMiembroDto::getTrabajadorId)
				.filter(Objects::nonNull)
				.forEach(destinatarios::add);
		}
		return destinatarios;
	}

	private void generarAlertasPlazosSeguro(UsuarioDto perfil) {
		try {
			generarAlertasPlazos(perfil);
		} catch (Exception ignored) {
			// Si ms-notificaciones no está disponible, no bloquear la consulta
		}
	}

	private void generarAlertasPlazos(UsuarioDto perfil) {
		List<NotificacionDto> existentes = microserviceClient.getNotificaciones(perfil.getId());
		LocalDate hoy = LocalDate.now();

		Set<Long> proyectoIds = new LinkedHashSet<>();
		microserviceClient.getTareas(null).stream()
			.filter(t -> perfil.getId().equals(t.getAsignadoId()))
			.map(TareaDto::getProyectoId)
			.filter(Objects::nonNull)
			.forEach(proyectoIds::add);
		microserviceClient.getMiembrosPorTrabajador(perfil.getId()).stream()
			.map(ProyectoMiembroDto::getProyectoId)
			.filter(Objects::nonNull)
			.forEach(proyectoIds::add);

		Map<Long, ProyectoDto> proyectos = microserviceClient.getProyectos().stream()
			.filter(p -> proyectoIds.contains(p.getId()))
			.collect(Collectors.toMap(ProyectoDto::getId, p -> p, (a, b) -> a));

		List<NotificacionDto> nuevas = new ArrayList<>();
		for (ProyectoDto proyecto : proyectos.values()) {
			if ("COMPLETADO".equals(proyecto.getEstado()) || "CANCELADO".equals(proyecto.getEstado())) {
				continue;
			}
			Integer dias = calcularDiasRestantes(proyecto.getFechaFin());
			if (dias == null) {
				continue;
			}
			if (dias < 0) {
				if (!yaNotificadoHoy(existentes, TIPO_PLAZO_VENCIDO, proyecto.getId(), null, hoy)) {
					String mensaje = "El proyecto \"" + proyecto.getNombre() + "\" está atrasado "
						+ Math.abs(dias) + " día(s).";
					nuevas.add(new NotificacionDto(null, perfil.getId(), TIPO_PLAZO_VENCIDO, mensaje,
						false, null, proyecto.getId(), LocalDateTime.now()));
				}
			} else if (dias <= DIAS_ALERTA_PLAZO) {
				if (!yaNotificadoHoy(existentes, TIPO_PLAZO_PROXIMO, proyecto.getId(), null, hoy)) {
					String mensaje = "El proyecto \"" + proyecto.getNombre() + "\" vence en " + dias + " día(s).";
					nuevas.add(new NotificacionDto(null, perfil.getId(), TIPO_PLAZO_PROXIMO, mensaje,
						false, null, proyecto.getId(), LocalDateTime.now()));
				}
			}
		}

		List<TareaDto> tareasPendientes = microserviceClient.getTareas(null).stream()
			.filter(t -> perfil.getId().equals(t.getAsignadoId()))
			.filter(t -> !"HECHO".equals(t.getEstado()))
			.toList();
		for (TareaDto tarea : tareasPendientes) {
			ProyectoDto proyecto = tarea.getProyectoId() != null ? proyectos.get(tarea.getProyectoId()) : null;
			if (proyecto == null || proyecto.getFechaFin() == null) {
				continue;
			}
			Integer dias = calcularDiasRestantes(proyecto.getFechaFin());
			if (dias == null) {
				continue;
			}
			if (dias < 0) {
				if (!yaNotificadoHoy(existentes, TIPO_PLAZO_VENCIDO, proyecto.getId(), tarea.getId(), hoy)) {
					String mensaje = "Tu tarea \"" + tarea.getTitulo() + "\" en \""
						+ proyecto.getNombre() + "\" está en proyecto atrasado.";
					nuevas.add(new NotificacionDto(null, perfil.getId(), TIPO_PLAZO_VENCIDO, mensaje,
						false, tarea.getId(), proyecto.getId(), LocalDateTime.now()));
				}
			} else if (dias <= DIAS_ALERTA_PLAZO) {
				if (!yaNotificadoHoy(existentes, TIPO_PLAZO_PROXIMO, proyecto.getId(), tarea.getId(), hoy)) {
					String mensaje = "Tu tarea \"" + tarea.getTitulo() + "\" en \""
						+ proyecto.getNombre() + "\" vence en " + dias + " día(s) (plazo del proyecto).";
					nuevas.add(new NotificacionDto(null, perfil.getId(), TIPO_PLAZO_PROXIMO, mensaje,
						false, tarea.getId(), proyecto.getId(), LocalDateTime.now()));
				}
			}
		}

		if (!nuevas.isEmpty()) {
			microserviceClient.createNotificacionesBatch(nuevas);
		}
	}

	private static boolean yaNotificadoHoy(List<NotificacionDto> existentes, String tipo,
			Long proyectoId, Long tareaId, LocalDate hoy) {
		return existentes.stream().anyMatch(n ->
			tipo.equals(n.getTipo())
				&& Objects.equals(proyectoId, n.getProyectoId())
				&& Objects.equals(tareaId, n.getTareaId())
				&& n.getFecha() != null
				&& hoy.equals(n.getFecha().toLocalDate()));
	}
}
