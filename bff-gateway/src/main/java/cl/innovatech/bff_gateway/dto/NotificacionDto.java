package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDto {
	private Long id;
	private Long destinatarioId;
	private String tipo;
	private String mensaje;
	private boolean leida;
	private Long tareaId;
	private Long proyectoId;
	private LocalDateTime fecha;
}
