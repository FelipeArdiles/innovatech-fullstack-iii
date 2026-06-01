package cl.innovatech.ms_usuarios.service;

import cl.innovatech.ms_usuarios.entity.Usuario;
import cl.innovatech.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@InjectMocks
	private UsuarioService usuarioService;

	private Usuario usuario;

	@BeforeEach
	void setUp() {
		usuario = new Usuario(1L, "Ana Torres", "Desarrolladora", "ana@innovatech.cl", 40);
	}

	@Test
	void findAllReturnsRepositoryData() {
		when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

		assertThat(usuarioService.findAll()).hasSize(1);
		verify(usuarioRepository).findAll();
	}

	@Test
	void createPersistsNewUsuario() {
		when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

		Usuario created = usuarioService.create(new Usuario(null, "Ana Torres", "Desarrolladora", "ana@innovatech.cl", 40));

		assertThat(created.getNombre()).isEqualTo("Ana Torres");
		verify(usuarioRepository).save(any(Usuario.class));
	}

	@Test
	void updateReturnsEmptyWhenNotFound() {
		when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

		assertThat(usuarioService.update(99L, usuario)).isEmpty();
	}

	@Test
	void deleteReturnsFalseWhenNotFound() {
		when(usuarioRepository.existsById(99L)).thenReturn(false);

		assertThat(usuarioService.delete(99L)).isFalse();
	}
}
