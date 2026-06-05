package cl.innovatech.ms_notificaciones.service;

import cl.innovatech.ms_notificaciones.entity.Notificacion;
import cl.innovatech.ms_notificaciones.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificacionService {

	private final NotificacionRepository repository;

	public NotificacionService(NotificacionRepository repository) {
		this.repository = repository;
	}

	public List<Notificacion> findByDestinatario(Long destinatarioId) {
		return repository.findByDestinatarioIdOrderByFechaDesc(destinatarioId);
	}

	public long countNoLeidas(Long destinatarioId) {
		return repository.countByDestinatarioIdAndLeidaFalse(destinatarioId);
	}

	public Notificacion create(Notificacion notificacion) {
		notificacion.setId(null);
		if (notificacion.getFecha() == null) {
			notificacion.setFecha(LocalDateTime.now());
		}
		return repository.save(notificacion);
	}

	public List<Notificacion> createBatch(List<Notificacion> notificaciones) {
		LocalDateTime now = LocalDateTime.now();
		return notificaciones.stream()
			.map(n -> {
				n.setId(null);
				if (n.getFecha() == null) {
					n.setFecha(now);
				}
				return repository.save(n);
			})
			.toList();
	}

	public Optional<Notificacion> marcarLeida(Long id) {
		return repository.findById(id).map(n -> {
			n.setLeida(true);
			return repository.save(n);
		});
	}

	public int marcarTodasLeidas(Long destinatarioId) {
		List<Notificacion> pendientes = repository
			.findByDestinatarioIdAndLeidaFalseOrderByFechaDesc(destinatarioId);
		pendientes.forEach(n -> n.setLeida(true));
		repository.saveAll(pendientes);
		return pendientes.size();
	}
}
