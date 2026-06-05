package cl.innovatech.ms_notificaciones.repository;

import cl.innovatech.ms_notificaciones.entity.ComentarioTarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioTareaRepository extends JpaRepository<ComentarioTarea, Long> {
	List<ComentarioTarea> findByTareaIdOrderByFechaAsc(Long tareaId);
}
