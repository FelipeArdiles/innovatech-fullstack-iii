package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaDto {
	private Long id;
	private String titulo;
	private String descripcion;
	private String estado;
	private Long proyectoId;
	private Long asignadoId;
	private Integer horasEstimadas;
}
