package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoResumenTrabajadorDto {
	private Long id;
	private String nombre;
	private String estado;
	private String descripcion;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private Integer diasRestantes;
	private boolean atrasado;
	private int misTareasTotal;
	private int misTareasPorHacer;
	private int misTareasEnProgreso;
	private int misTareasHechas;
}
