package cl.innovatech.bff_gateway.service;

import cl.innovatech.bff_gateway.client.MicroserviceClient;
import cl.innovatech.bff_gateway.dto.CapacidadEquipoDto;
import cl.innovatech.bff_gateway.dto.CapacidadTrabajadorDto;
import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.ProyectoDetalleDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.TareaDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BffService {

	private final MicroserviceClient microserviceClient;

	public BffService(MicroserviceClient microserviceClient) {
		this.microserviceClient = microserviceClient;
	}

	public DashboardDto getDashboard() {
		List<UsuarioDto> usuarios = microserviceClient.getUsuarios();
		List<ProyectoDto> proyectos = microserviceClient.getProyectos();
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
		return microserviceClient.getProyectos();
	}

	public ProyectoDto getProyecto(Long id) {
		return microserviceClient.getProyecto(id);
	}

	public ProyectoDetalleDto getProyectoDetalle(Long id) {
		ProyectoDto proyecto = microserviceClient.getProyecto(id);
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
			atrasado,
			(int) porHacer,
			(int) enProgreso,
			(int) hechas,
			horasProyecto,
			tareas,
			trabajadores
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
			capacidades.add(new CapacidadTrabajadorDto(
				usuario.getId(),
				usuario.getNombre(),
				usuario.getRol(),
				disponibles,
				asignadas,
				Math.round(porcentaje * 10.0) / 10.0,
				sobrecargado
			));
		}

		return new CapacidadEquipoDto(
			usuarios.size(),
			sobrecargados,
			horasDisponiblesTotal,
			horasAsignadasTotal,
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
}
