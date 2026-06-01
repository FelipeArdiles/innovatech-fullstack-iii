package cl.innovatech.ms_proyectos.controller;

import cl.innovatech.ms_proyectos.entity.Proyecto;
import cl.innovatech.ms_proyectos.service.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

	private final ProyectoService proyectoService;

	public ProyectoController(ProyectoService proyectoService) {
		this.proyectoService = proyectoService;
	}

	@GetMapping
	public List<Proyecto> list() {
		return proyectoService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Proyecto> get(@PathVariable Long id) {
		return proyectoService.findById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Proyecto> create(@RequestBody Proyecto proyecto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.create(proyecto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Proyecto> update(@PathVariable Long id, @RequestBody Proyecto proyecto) {
		return proyectoService.update(id, proyecto)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		return proyectoService.delete(id)
			? ResponseEntity.noContent().build()
			: ResponseEntity.notFound().build();
	}
}
