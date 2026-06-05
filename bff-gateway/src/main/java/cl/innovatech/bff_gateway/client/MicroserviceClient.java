package cl.innovatech.bff_gateway.client;

import cl.innovatech.bff_gateway.dto.ComentarioTareaDto;
import cl.innovatech.bff_gateway.dto.NotificacionDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.ProyectoMiembroDto;
import cl.innovatech.bff_gateway.dto.TareaDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
public class MicroserviceClient {

	private final RestClient usuariosClient;
	private final RestClient proyectosClient;
	private final RestClient tareasClient;
	private final RestClient notificacionesClient;

	public MicroserviceClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
		this.usuariosClient = restClientBuilder.baseUrl("http://ms-usuarios").build();
		this.proyectosClient = restClientBuilder.baseUrl("http://ms-proyectos").build();
		this.tareasClient = restClientBuilder.baseUrl("http://ms-tareas").build();
		this.notificacionesClient = restClientBuilder.baseUrl("http://ms-notificaciones").build();
	}

	@CircuitBreaker(name = "msUsuarios", fallbackMethod = "usuariosFallback")
	public List<UsuarioDto> getUsuarios() {
		return usuariosClient.get()
			.uri("/api/usuarios")
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@CircuitBreaker(name = "msUsuarios", fallbackMethod = "usuarioFallback")
	public UsuarioDto getUsuario(Long id) {
		return usuariosClient.get()
			.uri("/api/usuarios/{id}", id)
			.retrieve()
			.body(UsuarioDto.class);
	}

	@CircuitBreaker(name = "msUsuarios", fallbackMethod = "usuarioFallback")
	public UsuarioDto createUsuario(UsuarioDto usuario) {
		return usuariosClient.post()
			.uri("/api/usuarios")
			.body(usuario)
			.retrieve()
			.body(UsuarioDto.class);
	}

	@CircuitBreaker(name = "msUsuarios", fallbackMethod = "usuarioFallback")
	public UsuarioDto updateUsuario(Long id, UsuarioDto usuario) {
		return usuariosClient.put()
			.uri("/api/usuarios/{id}", id)
			.body(usuario)
			.retrieve()
			.body(UsuarioDto.class);
	}

	@CircuitBreaker(name = "msUsuarios")
	public void deleteUsuario(Long id) {
		usuariosClient.delete()
			.uri("/api/usuarios/{id}", id)
			.retrieve()
			.toBodilessEntity();
	}

	@CircuitBreaker(name = "msProyectos", fallbackMethod = "proyectosFallback")
	public List<ProyectoDto> getProyectos() {
		return proyectosClient.get()
			.uri("/api/proyectos")
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@CircuitBreaker(name = "msProyectos", fallbackMethod = "proyectoFallback")
	public ProyectoDto getProyecto(Long id) {
		return proyectosClient.get()
			.uri("/api/proyectos/{id}", id)
			.retrieve()
			.body(ProyectoDto.class);
	}

	@CircuitBreaker(name = "msProyectos", fallbackMethod = "proyectoFallback")
	public ProyectoDto createProyecto(ProyectoDto proyecto) {
		return proyectosClient.post()
			.uri("/api/proyectos")
			.body(proyecto)
			.retrieve()
			.body(ProyectoDto.class);
	}

	@CircuitBreaker(name = "msProyectos", fallbackMethod = "proyectoFallback")
	public ProyectoDto updateProyecto(Long id, ProyectoDto proyecto) {
		return proyectosClient.put()
			.uri("/api/proyectos/{id}", id)
			.body(proyecto)
			.retrieve()
			.body(ProyectoDto.class);
	}

	@CircuitBreaker(name = "msProyectos")
	public void deleteProyecto(Long id) {
		proyectosClient.delete()
			.uri("/api/proyectos/{id}", id)
			.retrieve()
			.toBodilessEntity();
	}

	@CircuitBreaker(name = "msTareas", fallbackMethod = "tareasFallback")
	public List<TareaDto> getTareas() {
		return getTareas(null);
	}

	@CircuitBreaker(name = "msTareas", fallbackMethod = "tareasFallback")
	public List<TareaDto> getTareas(Long proyectoId) {
		String uri = UriComponentsBuilder.fromPath("/api/tareas")
			.queryParamIfPresent("proyectoId", java.util.Optional.ofNullable(proyectoId))
			.build()
			.toUriString();
		return tareasClient.get()
			.uri(uri)
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@CircuitBreaker(name = "msTareas", fallbackMethod = "tareaFallback")
	public TareaDto getTarea(Long id) {
		return tareasClient.get()
			.uri("/api/tareas/{id}", id)
			.retrieve()
			.body(TareaDto.class);
	}

	@CircuitBreaker(name = "msTareas", fallbackMethod = "tareaFallback")
	public TareaDto createTarea(TareaDto tarea) {
		return tareasClient.post()
			.uri("/api/tareas")
			.body(tarea)
			.retrieve()
			.body(TareaDto.class);
	}

	@CircuitBreaker(name = "msTareas", fallbackMethod = "tareaFallback")
	public TareaDto updateTarea(Long id, TareaDto tarea) {
		return tareasClient.put()
			.uri("/api/tareas/{id}", id)
			.body(tarea)
			.retrieve()
			.body(TareaDto.class);
	}

	@CircuitBreaker(name = "msTareas")
	public void deleteTarea(Long id) {
		tareasClient.delete()
			.uri("/api/tareas/{id}", id)
			.retrieve()
			.toBodilessEntity();
	}

	@CircuitBreaker(name = "msProyectos", fallbackMethod = "miembrosFallback")
	public List<ProyectoMiembroDto> getMiembrosProyecto(Long proyectoId) {
		return proyectosClient.get()
			.uri("/api/proyectos/{proyectoId}/miembros", proyectoId)
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@CircuitBreaker(name = "msProyectos", fallbackMethod = "miembrosFallback")
	public List<ProyectoMiembroDto> getMiembrosPorTrabajador(Long trabajadorId) {
		return proyectosClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/proyectos/miembros/trabajador")
				.queryParam("trabajadorId", trabajadorId)
				.build())
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@CircuitBreaker(name = "msProyectos", fallbackMethod = "miembroFallback")
	public ProyectoMiembroDto agregarMiembro(Long proyectoId, Long trabajadorId) {
		return proyectosClient.post()
			.uri("/api/proyectos/{proyectoId}/miembros", proyectoId)
			.body(java.util.Map.of("trabajadorId", trabajadorId))
			.retrieve()
			.body(ProyectoMiembroDto.class);
	}

	@CircuitBreaker(name = "msProyectos")
	public void quitarMiembro(Long proyectoId, Long trabajadorId) {
		proyectosClient.delete()
			.uri("/api/proyectos/{proyectoId}/miembros/{trabajadorId}", proyectoId, trabajadorId)
			.retrieve()
			.toBodilessEntity();
	}

	@CircuitBreaker(name = "msNotificaciones", fallbackMethod = "comentariosFallback")
	public List<ComentarioTareaDto> getComentarios(Long tareaId) {
		return notificacionesClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/comentarios")
				.queryParam("tareaId", tareaId)
				.build())
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@CircuitBreaker(name = "msNotificaciones", fallbackMethod = "comentarioFallback")
	public ComentarioTareaDto createComentario(ComentarioTareaDto comentario) {
		return notificacionesClient.post()
			.uri("/api/comentarios")
			.body(comentario)
			.retrieve()
			.body(ComentarioTareaDto.class);
	}

	@CircuitBreaker(name = "msNotificaciones", fallbackMethod = "notificacionesFallback")
	public List<NotificacionDto> getNotificaciones(Long destinatarioId) {
		return notificacionesClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/notificaciones")
				.queryParam("destinatarioId", destinatarioId)
				.build())
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@CircuitBreaker(name = "msNotificaciones", fallbackMethod = "pendientesFallback")
	public long getNotificacionesPendientes(Long destinatarioId) {
		java.util.Map<String, Long> body = notificacionesClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/notificaciones/pendientes")
				.queryParam("destinatarioId", destinatarioId)
				.build())
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
		return body != null ? body.getOrDefault("count", 0L) : 0L;
	}

	@CircuitBreaker(name = "msNotificaciones")
	public void createNotificacionesBatch(List<NotificacionDto> notificaciones) {
		if (notificaciones == null || notificaciones.isEmpty()) {
			return;
		}
		notificacionesClient.post()
			.uri("/api/notificaciones/batch")
			.body(notificaciones)
			.retrieve()
			.toBodilessEntity();
	}

	@CircuitBreaker(name = "msNotificaciones", fallbackMethod = "notificacionFallback")
	public NotificacionDto marcarNotificacionLeida(Long id) {
		return notificacionesClient.patch()
			.uri("/api/notificaciones/{id}/leida", id)
			.retrieve()
			.body(NotificacionDto.class);
	}

	@CircuitBreaker(name = "msNotificaciones")
	public int marcarTodasNotificacionesLeidas(Long destinatarioId) {
		java.util.Map<String, Integer> body = notificacionesClient.patch()
			.uri(uriBuilder -> uriBuilder
				.path("/api/notificaciones/marcar-todas")
				.queryParam("destinatarioId", destinatarioId)
				.build())
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
		return body != null ? body.getOrDefault("marcadas", 0) : 0;
	}

	@SuppressWarnings("unused")
	private List<UsuarioDto> usuariosFallback(Throwable ex) {
		return List.of();
	}

	@SuppressWarnings("unused")
	private UsuarioDto usuarioFallback(Throwable ex) {
		return null;
	}

	@SuppressWarnings("unused")
	private List<ProyectoDto> proyectosFallback(Throwable ex) {
		return List.of();
	}

	@SuppressWarnings("unused")
	private ProyectoDto proyectoFallback(Throwable ex) {
		return null;
	}

	@SuppressWarnings("unused")
	private List<TareaDto> tareasFallback(Throwable ex) {
		return List.of();
	}

	@SuppressWarnings("unused")
	private TareaDto tareaFallback(Throwable ex) {
		return null;
	}

	@SuppressWarnings("unused")
	private List<ProyectoMiembroDto> miembrosFallback(Throwable ex) {
		return List.of();
	}

	@SuppressWarnings("unused")
	private ProyectoMiembroDto miembroFallback(Throwable ex) {
		return null;
	}

	@SuppressWarnings("unused")
	private List<ComentarioTareaDto> comentariosFallback(Throwable ex) {
		return List.of();
	}

	@SuppressWarnings("unused")
	private ComentarioTareaDto comentarioFallback(Throwable ex) {
		return null;
	}

	@SuppressWarnings("unused")
	private List<NotificacionDto> notificacionesFallback(Throwable ex) {
		return List.of();
	}

	@SuppressWarnings("unused")
	private long pendientesFallback(Throwable ex) {
		return 0L;
	}

	@SuppressWarnings("unused")
	private NotificacionDto notificacionFallback(Throwable ex) {
		return null;
	}
}
