package cl.innovatech.ms_notificaciones.repository;

import cl.innovatech.ms_notificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
	List<Notificacion> findByDestinatarioIdOrderByFechaDesc(Long destinatarioId);
	long countByDestinatarioIdAndLeidaFalse(Long destinatarioId);
	List<Notificacion> findByDestinatarioIdAndLeidaFalseOrderByFechaDesc(Long destinatarioId);
}
