package cl.innovatech.ms_tareas.service;

import cl.innovatech.ms_tareas.entity.Tarea;
import cl.innovatech.ms_tareas.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TareaService {

	private final TareaRepository tareaRepository;

	public TareaService(TareaRepository tareaRepository) {
		this.tareaRepository = tareaRepository;
	}

	public List<Tarea> findAll() {
		return tareaRepository.findAll();
	}

	public Optional<Tarea> findById(Long id) {
		return tareaRepository.findById(id);
	}

	public List<Tarea> findByProyectoId(Long proyectoId) {
		return tareaRepository.findByProyectoId(proyectoId);
	}

	public Tarea create(Tarea tarea) {
		tarea.setId(null);
		return tareaRepository.save(tarea);
	}

	public Optional<Tarea> update(Long id, Tarea tarea) {
		return tareaRepository.findById(id).map(existing -> {
			existing.setTitulo(tarea.getTitulo());
			existing.setDescripcion(tarea.getDescripcion());
			existing.setEstado(tarea.getEstado());
			existing.setProyectoId(tarea.getProyectoId());
			existing.setAsignadoId(tarea.getAsignadoId());
			return tareaRepository.save(existing);
		});
	}

	public boolean delete(Long id) {
		if (!tareaRepository.existsById(id)) {
			return false;
		}
		tareaRepository.deleteById(id);
		return true;
	}
}
