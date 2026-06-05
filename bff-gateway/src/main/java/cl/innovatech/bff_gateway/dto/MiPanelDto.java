package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiPanelDto {
	private UsuarioDto perfil;
	private String tipoCuenta;
	private String rolKeycloak;
	private int totalTareasAsignadas;
	private int tareasPorHacer;
	private int tareasEnProgreso;
	private int tareasHechas;
	private int horasAsignadasTotal;
	private List<TareaDto> tareasAsignadas;
	private List<ProyectoResumenTrabajadorDto> proyectos;
}
