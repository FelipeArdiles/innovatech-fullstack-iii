package cl.innovatech.bff_gateway.controller;

import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import cl.innovatech.bff_gateway.service.BffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BffController {

	private final BffService bffService;

	public BffController(BffService bffService) {
		this.bffService = bffService;
	}

	@GetMapping("/dashboard")
	public DashboardDto dashboard() {
		return bffService.getDashboard();
	}

	@GetMapping("/usuarios")
	public List<UsuarioDto> listUsuarios() {
		return bffService.getUsuarios();
	}

	@GetMapping("/usuarios/{id}")
	public ResponseEntity<UsuarioDto> getUsuario(@PathVariable Long id) {
		UsuarioDto usuario = bffService.getUsuario(id);
		return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
	}

	@PostMapping("/usuarios")
	public ResponseEntity<UsuarioDto> createUsuario(@RequestBody UsuarioDto usuario) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bffService.createUsuario(usuario));
	}

	@PutMapping("/usuarios/{id}")
	public ResponseEntity<UsuarioDto> updateUsuario(@PathVariable Long id, @RequestBody UsuarioDto usuario) {
		UsuarioDto updated = bffService.updateUsuario(id, usuario);
		return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/usuarios/{id}")
	public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
		bffService.deleteUsuario(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/proyectos")
	public List<ProyectoDto> listProyectos() {
		return bffService.getProyectos();
	}

	@GetMapping("/proyectos/{id}")
	public ResponseEntity<ProyectoDto> getProyecto(@PathVariable Long id) {
		ProyectoDto proyecto = bffService.getProyecto(id);
		return proyecto != null ? ResponseEntity.ok(proyecto) : ResponseEntity.notFound().build();
	}

	@PostMapping("/proyectos")
	public ResponseEntity<ProyectoDto> createProyecto(@RequestBody ProyectoDto proyecto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bffService.createProyecto(proyecto));
	}

	@PutMapping("/proyectos/{id}")
	public ResponseEntity<ProyectoDto> updateProyecto(@PathVariable Long id, @RequestBody ProyectoDto proyecto) {
		ProyectoDto updated = bffService.updateProyecto(id, proyecto);
		return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/proyectos/{id}")
	public ResponseEntity<Void> deleteProyecto(@PathVariable Long id) {
		bffService.deleteProyecto(id);
		return ResponseEntity.noContent().build();
	}
}
