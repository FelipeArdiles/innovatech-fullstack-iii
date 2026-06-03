package cl.innovatech.ms_proyectos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private String estado;
	private String descripcion;
	private Long responsableId;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private BigDecimal presupuesto;
	private BigDecimal costoReal;
	private BigDecimal ingresosContrato;
}
