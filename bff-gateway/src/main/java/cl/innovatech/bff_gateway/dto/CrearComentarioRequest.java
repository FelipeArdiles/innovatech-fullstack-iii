package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearComentarioRequest {
	private Long tareaId;
	private String texto;
}
