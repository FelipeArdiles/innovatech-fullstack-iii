package cl.innovatech.ms_usuarios.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private String rol;
	private String email;
	private Integer capacidadHoras;
	/** Sueldo líquido mensual referencial en pesos chilenos (CLP). */
	private Long sueldoMensualClp;
}
