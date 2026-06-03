package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanzasCategoriaDto {
	private String categoria;
	private BigDecimal valor;
	private int cantidadTareas;
}
