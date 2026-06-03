package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaValorDto {
	private Long id;
	private String titulo;
	private String categoria;
	private String dificultad;
	private Integer horasEstimadas;
	private BigDecimal valorMonetario;
}
