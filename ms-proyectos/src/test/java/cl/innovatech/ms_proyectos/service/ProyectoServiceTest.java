package cl.innovatech.ms_proyectos.service;

import cl.innovatech.ms_proyectos.entity.Proyecto;
import cl.innovatech.ms_proyectos.repository.ProyectoRepository;
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
class ProyectoServiceTest {

	@Mock
	private ProyectoRepository proyectoRepository;

	@InjectMocks
	private ProyectoService proyectoService;

	private Proyecto proyecto;

	@BeforeEach
	void setUp() {
		proyecto = new Proyecto(1L, "Portal Clientes", "EN_PROGRESO", "Modernización", 1L, null, null, null);
	}

	@Test
	void findAllReturnsRepositoryData() {
		when(proyectoRepository.findAll()).thenReturn(List.of(proyecto));

		assertThat(proyectoService.findAll()).hasSize(1);
	}

	@Test
	void createPersistsNewProyecto() {
		when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyecto);

		Proyecto created = proyectoService.create(new Proyecto(null, "Portal Clientes", "EN_PROGRESO", "Modernización", 1L));

		assertThat(created.getEstado()).isEqualTo("EN_PROGRESO");
	}

	@Test
	void updateReturnsEmptyWhenNotFound() {
		when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThat(proyectoService.update(99L, proyecto)).isEmpty();
	}

	@Test
	void deleteReturnsTrueWhenExists() {
		when(proyectoRepository.existsById(1L)).thenReturn(true);

		assertThat(proyectoService.delete(1L)).isTrue();
		verify(proyectoRepository).deleteById(1L);
	}
}
