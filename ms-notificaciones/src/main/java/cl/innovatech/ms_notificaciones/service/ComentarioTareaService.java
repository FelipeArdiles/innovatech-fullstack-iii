package cl.innovatech.ms_notificaciones.service;

import cl.innovatech.ms_notificaciones.entity.ComentarioTarea;
import cl.innovatech.ms_notificaciones.repository.ComentarioTareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ComentarioTareaService {

	private final ComentarioTareaRepository repository;

	public ComentarioTareaService(ComentarioTareaRepository repository) {
		this.repository = repository;
	}

	public List<ComentarioTarea> findByTarea(Long tareaId) {
		return repository.findByTareaIdOrderByFechaAsc(tareaId);
	}

	public ComentarioTarea create(ComentarioTarea comentario) {
		comentario.setId(null);
		if (comentario.getFecha() == null) {
			comentario.setFecha(LocalDateTime.now());
		}
		return repository.save(comentario);
	}

	public Optional<ComentarioTarea> findById(Long id) {
		return repository.findById(id);
	}
}
