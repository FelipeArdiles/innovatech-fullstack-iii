package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoDto {
	private Long id;
	private String nombre;
	private String estado;
	private String descripcion;
	private Long responsableId;
}
