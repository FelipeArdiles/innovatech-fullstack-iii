package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanzasResumenDto {
	private BigDecimal ingresosTotales;
	private BigDecimal costosTotales;
	private BigDecimal gananciaNeta;
	private Double margenEmpresaPorcentaje;
	private int totalProyectos;
	private int proyectosRentables;
	private List<ProyectoFinanzasDto> proyectos;
}
