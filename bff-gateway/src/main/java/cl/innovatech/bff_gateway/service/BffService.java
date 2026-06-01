package cl.innovatech.bff_gateway.service;

import cl.innovatech.bff_gateway.client.MicroserviceClient;
import cl.innovatech.bff_gateway.dto.DashboardDto;
import cl.innovatech.bff_gateway.dto.ProyectoDto;
import cl.innovatech.bff_gateway.dto.UsuarioDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BffService {

	private final MicroserviceClient microserviceClient;

	public BffService(MicroserviceClient microserviceClient) {
		this.microserviceClient = microserviceClient;
	}

	public DashboardDto getDashboard() {
		List<UsuarioDto> usuarios = microserviceClient.getUsuarios();
		List<ProyectoDto> proyectos = microserviceClient.getProyectos();
		return new DashboardDto(
			usuarios.size(),
			proyectos.size(),
			usuarios,
			proyectos
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

	public ProyectoDto createProyecto(ProyectoDto proyecto) {
		return microserviceClient.createProyecto(proyecto);
	}

	public ProyectoDto updateProyecto(Long id, ProyectoDto proyecto) {
		return microserviceClient.updateProyecto(id, proyecto);
	}

	public void deleteProyecto(Long id) {
		microserviceClient.deleteProyecto(id);
	}
}
