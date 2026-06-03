package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoFinanzasDto {
	private Long proyectoId;
	private String nombreProyecto;
	private BigDecimal presupuesto;
	private BigDecimal costoAcumulado;
	private BigDecimal costoRealRegistrado;
	private BigDecimal ingresos;
	private BigDecimal ganancia;
	private Double margenPorcentaje;
	private boolean rentable;
	private List<FinanzasCategoriaDto> desglosePorCategoria;
	private List<TareaValorDto> desgloseTareas;
}
