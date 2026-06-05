package cl.innovatech.ms_proyectos.controller;

import cl.innovatech.ms_proyectos.entity.ProyectoMiembro;
import cl.innovatech.ms_proyectos.service.ProyectoMiembroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoMiembroController {

	private final ProyectoMiembroService miembroService;

	public ProyectoMiembroController(ProyectoMiembroService miembroService) {
		this.miembroService = miembroService;
	}

	@GetMapping("/{proyectoId}/miembros")
	public List<ProyectoMiembro> listByProyecto(@PathVariable Long proyectoId) {
		return miembroService.findByProyecto(proyectoId);
	}

	@GetMapping("/miembros/trabajador")
	public List<ProyectoMiembro> listByTrabajador(@RequestParam Long trabajadorId) {
		return miembroService.findByTrabajador(trabajadorId);
	}

	@PostMapping("/{proyectoId}/miembros")
	public ResponseEntity<ProyectoMiembro> agregar(@PathVariable Long proyectoId,
			@RequestBody Map<String, Long> body) {
		Long trabajadorId = body.get("trabajadorId");
		if (trabajadorId == null) {
			return ResponseEntity.badRequest().build();
		}
		return miembroService.agregar(proyectoId, trabajadorId)
			.map(m -> ResponseEntity.status(HttpStatus.CREATED).body(m))
			.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{proyectoId}/miembros/{trabajadorId}")
	public ResponseEntity<Void> quitar(@PathVariable Long proyectoId, @PathVariable Long trabajadorId) {
		return miembroService.quitar(proyectoId, trabajadorId)
			? ResponseEntity.noContent().build()
			: ResponseEntity.notFound().build();
	}
}
