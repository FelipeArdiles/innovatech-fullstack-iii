package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioTareaDto {
	private Long id;
	private Long tareaId;
	private Long autorId;
	private String autorNombre;
	private String texto;
	private LocalDateTime fecha;
}
