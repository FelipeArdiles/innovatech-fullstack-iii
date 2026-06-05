package cl.innovatech.ms_proyectos.service;

import cl.innovatech.ms_proyectos.entity.ProyectoMiembro;
import cl.innovatech.ms_proyectos.repository.ProyectoMiembroRepository;
import cl.innovatech.ms_proyectos.repository.ProyectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProyectoMiembroService {

	private final ProyectoMiembroRepository miembroRepository;
	private final ProyectoRepository proyectoRepository;

	public ProyectoMiembroService(ProyectoMiembroRepository miembroRepository,
			ProyectoRepository proyectoRepository) {
		this.miembroRepository = miembroRepository;
		this.proyectoRepository = proyectoRepository;
	}

	public List<ProyectoMiembro> findByProyecto(Long proyectoId) {
		return miembroRepository.findByProyectoId(proyectoId);
	}

	public List<ProyectoMiembro> findByTrabajador(Long trabajadorId) {
		return miembroRepository.findByTrabajadorId(trabajadorId);
	}

	public Optional<ProyectoMiembro> agregar(Long proyectoId, Long trabajadorId) {
		if (!proyectoRepository.existsById(proyectoId)) {
			return Optional.empty();
		}
		if (miembroRepository.existsByProyectoIdAndTrabajadorId(proyectoId, trabajadorId)) {
			return miembroRepository.findByProyectoIdAndTrabajadorId(proyectoId, trabajadorId);
		}
		return Optional.of(miembroRepository.save(new ProyectoMiembro(null, proyectoId, trabajadorId)));
	}

	public boolean quitar(Long proyectoId, Long trabajadorId) {
		if (!miembroRepository.existsByProyectoIdAndTrabajadorId(proyectoId, trabajadorId)) {
			return false;
		}
		miembroRepository.deleteByProyectoIdAndTrabajadorId(proyectoId, trabajadorId);
		return true;
	}
}
