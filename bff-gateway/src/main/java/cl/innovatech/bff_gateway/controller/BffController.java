package cl.innovatech.bff_gateway.controller;

import cl.innovatech.bff_gateway.dto.CapacidadEquipoDto;
import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.ProyectoDetalleDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.TareaDto;
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

	@GetMapping("/trabajadores")
	public List<UsuarioDto> listTrabajadores() {
		return bffService.getUsuarios();
	}

	@GetMapping("/trabajadores/{id}")
	public ResponseEntity<UsuarioDto> getTrabajador(@PathVariable Long id) {
		return getUsuario(id);
	}

	@PostMapping("/trabajadores")
	public ResponseEntity<UsuarioDto> createTrabajador(@RequestBody UsuarioDto trabajador) {
		return createUsuario(trabajador);
	}

	@PutMapping("/trabajadores/{id}")
	public ResponseEntity<UsuarioDto> updateTrabajador(@PathVariable Long id, @RequestBody UsuarioDto trabajador) {
		return updateUsuario(id, trabajador);
	}

	@DeleteMapping("/trabajadores/{id}")
	public ResponseEntity<Void> deleteTrabajador(@PathVariable Long id) {
		return deleteUsuario(id);
	}

	@GetMapping("/proyectos")
	public List<ProyectoDto> listProyectos() {
		return bffService.getProyectos();
	}

	@GetMapping("/proyectos/{id}")
	public ResponseEntity<ProyectoDetalleDto> getProyecto(@PathVariable Long id) {
		ProyectoDetalleDto proyecto = bffService.getProyectoDetalle(id);
		return proyecto != null ? ResponseEntity.ok(proyecto) : ResponseEntity.notFound().build();
	}

	@GetMapping("/equipo/capacidad")
	public CapacidadEquipoDto getCapacidadEquipo() {
		return bffService.getCapacidadEquipo();
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

	@GetMapping("/tareas")
	public List<TareaDto> listTareas(@RequestParam(required = false) Long proyectoId) {
		return bffService.getTareas(proyectoId);
	}

	@GetMapping("/tareas/{id}")
	public ResponseEntity<TareaDto> getTarea(@PathVariable Long id) {
		TareaDto tarea = bffService.getTarea(id);
		return tarea != null ? ResponseEntity.ok(tarea) : ResponseEntity.notFound().build();
	}

	@PostMapping("/tareas")
	public ResponseEntity<TareaDto> createTarea(@RequestBody TareaDto tarea) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bffService.createTarea(tarea));
	}

	@PutMapping("/tareas/{id}")
	public ResponseEntity<TareaDto> updateTarea(@PathVariable Long id, @RequestBody TareaDto tarea) {
		TareaDto updated = bffService.updateTarea(id, tarea);
		return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/tareas/{id}")
	public ResponseEntity<Void> deleteTarea(@PathVariable Long id) {
		bffService.deleteTarea(id);
		return ResponseEntity.noContent().build();
	}
}
