package cl.innovatech.bff_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoDetalleDto {
	private Long id;
	private String nombre;
	private String estado;
	private String descripcion;
	private Long responsableId;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private BigDecimal presupuesto;
	private boolean atrasado;
	private int tareasPorHacer;
	private int tareasEnProgreso;
	private int tareasHechas;
	private int horasProyectoEstimadas;
	private List<TareaDto> tareas;
	private List<UsuarioDto> trabajadores;
}
