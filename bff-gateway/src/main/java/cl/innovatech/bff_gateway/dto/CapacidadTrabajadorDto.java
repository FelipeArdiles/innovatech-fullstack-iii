package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacidadTrabajadorDto {
	private Long id;
	private String nombre;
	private String rol;
	private int horasDisponibles;
	private int horasAsignadas;
	private double porcentajeCarga;
	private boolean sobrecargado;
	private Long sueldoMensualClp;
	private BigDecimal costoHorasAsignadasClp;
}
