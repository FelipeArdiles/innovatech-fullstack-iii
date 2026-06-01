package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
	private int totalUsuarios;
	private int totalProyectos;
	private List<UsuarioDto> usuarios;
	private List<ProyectoDto> proyectos;
}
