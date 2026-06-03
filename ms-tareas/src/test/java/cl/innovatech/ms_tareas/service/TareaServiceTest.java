package cl.innovatech.ms_tareas.service;

import cl.innovatech.ms_tareas.entity.Tarea;
import cl.innovatech.ms_tareas.repository.TareaRepository;
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
class TareaServiceTest {

	@Mock
	private TareaRepository tareaRepository;

	@InjectMocks
	private TareaService tareaService;

	private Tarea tarea;

	@BeforeEach
	void setUp() {
		tarea = new Tarea(1L, "Diseñar UI", "Mockups", "POR_HACER", 1L, 1L);
	}

	@Test
	void findAllReturnsRepositoryData() {
		when(tareaRepository.findAll()).thenReturn(List.of(tarea));

		assertThat(tareaService.findAll()).hasSize(1);
	}

	@Test
	void createPersistsNewTarea() {
		when(tareaRepository.save(any(Tarea.class))).thenReturn(tarea);

		Tarea created = tareaService.create(new Tarea(null, "Diseñar UI", "Mockups", "POR_HACER", 1L, 1L));

		assertThat(created.getEstado()).isEqualTo("POR_HACER");
	}

	@Test
	void updateReturnsEmptyWhenNotFound() {
		when(tareaRepository.findById(99L)).thenReturn(Optional.empty());

		assertThat(tareaService.update(99L, tarea)).isEmpty();
	}

	@Test
	void deleteReturnsTrueWhenExists() {
		when(tareaRepository.existsById(1L)).thenReturn(true);

		assertThat(tareaService.delete(1L)).isTrue();
		verify(tareaRepository).deleteById(1L);
	}
}
