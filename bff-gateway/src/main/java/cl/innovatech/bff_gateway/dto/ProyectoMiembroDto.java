package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoMiembroDto {
	private Long id;
	private Long proyectoId;
	private Long trabajadorId;
}
