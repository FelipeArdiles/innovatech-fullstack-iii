package cl.innovatech.ms_notificaciones.controller;

import cl.innovatech.ms_notificaciones.entity.Notificacion;
import cl.innovatech.ms_notificaciones.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

	private final NotificacionService notificacionService;

	public NotificacionController(NotificacionService notificacionService) {
		this.notificacionService = notificacionService;
	}

	@GetMapping
	public List<Notificacion> list(@RequestParam Long destinatarioId) {
		return notificacionService.findByDestinatario(destinatarioId);
	}

	@GetMapping("/pendientes")
	public Map<String, Long> pendientes(@RequestParam Long destinatarioId) {
		return Map.of("count", notificacionService.countNoLeidas(destinatarioId));
	}

	@PostMapping
	public ResponseEntity<Notificacion> create(@RequestBody Notificacion notificacion) {
		if (notificacion.getDestinatarioId() == null || notificacion.getMensaje() == null) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.status(201).body(notificacionService.create(notificacion));
	}

	@PostMapping("/batch")
	public List<Notificacion> createBatch(@RequestBody List<Notificacion> notificaciones) {
		return notificacionService.createBatch(notificaciones);
	}

	@PatchMapping("/{id}/leida")
	public ResponseEntity<Notificacion> marcarLeida(@PathVariable Long id) {
		return notificacionService.marcarLeida(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PatchMapping("/marcar-todas")
	public Map<String, Integer> marcarTodas(@RequestParam Long destinatarioId) {
		int count = notificacionService.marcarTodasLeidas(destinatarioId);
		return Map.of("marcadas", count);
	}
}
