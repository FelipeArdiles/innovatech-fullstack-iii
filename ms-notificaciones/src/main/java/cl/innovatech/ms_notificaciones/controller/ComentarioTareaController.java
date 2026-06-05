package cl.innovatech.ms_notificaciones.controller;

import cl.innovatech.ms_notificaciones.entity.ComentarioTarea;
import cl.innovatech.ms_notificaciones.service.ComentarioTareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioTareaController {

	private final ComentarioTareaService comentarioService;

	public ComentarioTareaController(ComentarioTareaService comentarioService) {
		this.comentarioService = comentarioService;
	}

	@GetMapping
	public List<ComentarioTarea> list(@RequestParam Long tareaId) {
		return comentarioService.findByTarea(tareaId);
	}

	@PostMapping
	public ResponseEntity<ComentarioTarea> create(@RequestBody ComentarioTarea comentario) {
		if (comentario.getTareaId() == null || comentario.getTexto() == null || comentario.getTexto().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(comentarioService.create(comentario));
	}
}
