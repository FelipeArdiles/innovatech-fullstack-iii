package cl.innovatech.ms_proyectos.service;

import cl.innovatech.ms_proyectos.entity.Proyecto;
import cl.innovatech.ms_proyectos.repository.ProyectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProyectoService {

	private final ProyectoRepository proyectoRepository;

	public ProyectoService(ProyectoRepository proyectoRepository) {
		this.proyectoRepository = proyectoRepository;
	}

	public List<Proyecto> findAll() {
		return proyectoRepository.findAll();
	}

	public Optional<Proyecto> findById(Long id) {
		return proyectoRepository.findById(id);
	}

	public Proyecto create(Proyecto proyecto) {
		proyecto.setId(null);
		return proyectoRepository.save(proyecto);
	}

	public Optional<Proyecto> update(Long id, Proyecto proyecto) {
		return proyectoRepository.findById(id).map(existing -> {
			existing.setNombre(proyecto.getNombre());
			existing.setEstado(proyecto.getEstado());
			existing.setDescripcion(proyecto.getDescripcion());
			existing.setResponsableId(proyecto.getResponsableId());
			return proyectoRepository.save(existing);
		});
	}

	public boolean delete(Long id) {
		if (!proyectoRepository.existsById(id)) {
			return false;
		}
		proyectoRepository.deleteById(id);
		return true;
	}
}
