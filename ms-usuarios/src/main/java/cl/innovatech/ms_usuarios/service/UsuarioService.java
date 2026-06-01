package cl.innovatech.ms_usuarios.service;

import cl.innovatech.ms_usuarios.entity.Usuario;
import cl.innovatech.ms_usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}

	public Optional<Usuario> findById(Long id) {
		return usuarioRepository.findById(id);
	}

	public Usuario create(Usuario usuario) {
		usuario.setId(null);
		return usuarioRepository.save(usuario);
	}

	public Optional<Usuario> update(Long id, Usuario usuario) {
		return usuarioRepository.findById(id).map(existing -> {
			existing.setNombre(usuario.getNombre());
			existing.setRol(usuario.getRol());
			existing.setEmail(usuario.getEmail());
			existing.setCapacidadHoras(usuario.getCapacidadHoras());
			return usuarioRepository.save(existing);
		});
	}

	public boolean delete(Long id) {
		if (!usuarioRepository.existsById(id)) {
			return false;
		}
		usuarioRepository.deleteById(id);
		return true;
	}
}
