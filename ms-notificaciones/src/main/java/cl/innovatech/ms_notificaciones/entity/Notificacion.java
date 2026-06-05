package cl.innovatech.ms_notificaciones.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long destinatarioId;
	private String tipo;
	@Column(length = 500)
	private String mensaje;
	private boolean leida;
	private Long tareaId;
	private Long proyectoId;
	private LocalDateTime fecha;
}
