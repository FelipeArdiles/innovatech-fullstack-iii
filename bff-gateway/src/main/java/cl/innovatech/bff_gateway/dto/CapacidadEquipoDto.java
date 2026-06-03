package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacidadEquipoDto {
	private int totalTrabajadores;
	private int trabajadoresSobrecargados;
	private int horasDisponiblesTotal;
	private int horasAsignadasTotal;
	private List<CapacidadTrabajadorDto> trabajadores;
}
