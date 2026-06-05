package cl.innovatech.ms_proyectos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proyecto_miembros", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"proyecto_id", "trabajador_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoMiembro {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long proyectoId;
	private Long trabajadorId;
}
