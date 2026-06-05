package cl.innovatech.ms_proyectos.repository;

import cl.innovatech.ms_proyectos.entity.ProyectoMiembro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProyectoMiembroRepository extends JpaRepository<ProyectoMiembro, Long> {
	List<ProyectoMiembro> findByProyectoId(Long proyectoId);
	List<ProyectoMiembro> findByTrabajadorId(Long trabajadorId);
	Optional<ProyectoMiembro> findByProyectoIdAndTrabajadorId(Long proyectoId, Long trabajadorId);
	boolean existsByProyectoIdAndTrabajadorId(Long proyectoId, Long trabajadorId);
	void deleteByProyectoIdAndTrabajadorId(Long proyectoId, Long trabajadorId);
}
