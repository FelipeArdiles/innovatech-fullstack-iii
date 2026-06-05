package cl.innovatech.ms_notificaciones.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios_tarea")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioTarea {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long tareaId;
	private Long autorId;
	private String autorNombre;
	@Column(length = 2000)
	private String texto;
	private LocalDateTime fecha;
}
