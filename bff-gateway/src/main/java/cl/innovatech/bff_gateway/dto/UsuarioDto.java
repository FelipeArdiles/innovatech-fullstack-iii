package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
	private Long id;
	private String nombre;
	private String rol;
	private String email;
	private Integer capacidadHoras;
}
