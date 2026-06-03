package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoDto {
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
	private Double margenPorcentaje;
}
