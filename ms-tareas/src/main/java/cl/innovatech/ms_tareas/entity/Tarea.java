package cl.innovatech.ms_tareas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tareas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String titulo;
	private String descripcion;
	private String estado;
	private Long proyectoId;
	private Long asignadoId;
	private Integer horasEstimadas;
	@Enumerated(EnumType.STRING)
	private DificultadTarea dificultad;
	private BigDecimal valorMonetario;
	@Enumerated(EnumType.STRING)
	private CategoriaTarea categoria;
}
