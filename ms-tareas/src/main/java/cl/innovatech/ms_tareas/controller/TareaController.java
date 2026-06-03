package cl.innovatech.ms_tareas.controller;

import cl.innovatech.ms_tareas.entity.Tarea;
import cl.innovatech.ms_tareas.service.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

	private final TareaService tareaService;

	public TareaController(TareaService tareaService) {
		this.tareaService = tareaService;
	}

	@GetMapping
	public List<Tarea> list(@RequestParam(required = false) Long proyectoId) {
		if (proyectoId != null) {
			return tareaService.findByProyectoId(proyectoId);
		}
		return tareaService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Tarea> get(@PathVariable Long id) {
		return tareaService.findById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Tarea> create(@RequestBody Tarea tarea) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tareaService.create(tarea));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Tarea> update(@PathVariable Long id, @RequestBody Tarea tarea) {
		return tareaService.update(id, tarea)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		return tareaService.delete(id)
			? ResponseEntity.noContent().build()
			: ResponseEntity.notFound().build();
	}
}
