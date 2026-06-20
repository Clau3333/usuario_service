package cl.perfulandia.usuarios.service;

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.repository.PermisoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermisoServiceTest {

    @Mock
    private PermisoRepository permisoRepository;

    @InjectMocks
    private PermisoService permisoService;

    @Test
    void listarPermisos_deberiaRetornarLista() {
        // Given: el repositorio tiene permisos
        Permiso permiso = new Permiso(1L, "GESTIONAR_USUARIOS", "Permite gestionar usuarios");
        when(permisoRepository.findAll()).thenReturn(List.of(permiso));

        // When: pedimos la lista
        List<Permiso> resultado = permisoService.listarPermisos();

        // Then: retorna lo esperado
        assertEquals(1, resultado.size());
        assertEquals("GESTIONAR_USUARIOS", resultado.get(0).getNombrePermiso());
        verify(permisoRepository).findAll();
    }

    @Test
    void buscarPermisoPorId_deberiaRetornarPermisoCuandoExiste() {
        // Given
        Permiso permiso = new Permiso(1L, "CONFIGURAR_PERMISOS", "Permite configurar permisos");
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permiso));

        // When
        Optional<Permiso> resultado = permisoService.buscarPermisoPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("CONFIGURAR_PERMISOS", resultado.get().getNombrePermiso());
        verify(permisoRepository).findById(1L);
    }

    @Test
    void buscarPermisoPorId_deberiaRetornarVacioCuandoNoExiste() {
        // Given
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Permiso> resultado = permisoService.buscarPermisoPorId(99L);

        // Then
        assertTrue(resultado.isEmpty());
        verify(permisoRepository).findById(99L);
    }

    @Test
    void guardarPermiso_deberiaGuardarCuandoNoExisteNombre() {
        // Given
        Permiso permiso = new Permiso(null, " GESTIONAR_USUARIOS ", " Permite gestionar usuarios ");
        when(permisoRepository.existsByNombrePermiso("GESTIONAR_USUARIOS")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> {
            Permiso permisoGuardado = invocation.getArgument(0);
            permisoGuardado.setIdPermiso(1L);
            return permisoGuardado;
        });

        // When
        Permiso resultado = permisoService.guardarPermiso(permiso);

        // Then
        assertEquals(1L, resultado.getIdPermiso());
        assertEquals("GESTIONAR_USUARIOS", resultado.getNombrePermiso());
        assertEquals("Permite gestionar usuarios", resultado.getDescripcion());
        verify(permisoRepository).save(permiso);
    }

    @Test
    void guardarPermiso_deberiaPermitirDescripcionNull() {
        // Given
        Permiso permiso = new Permiso(null, "GESTIONAR_USUARIOS", null);
        when(permisoRepository.existsByNombrePermiso("GESTIONAR_USUARIOS")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Permiso resultado = permisoService.guardarPermiso(permiso);

        // Then
        assertNull(resultado.getDescripcion());
        verify(permisoRepository).save(permiso);
    }

    @Test
    void guardarPermiso_deberiaConvertirDescripcionVaciaEnNull() {
        // Given
        Permiso permiso = new Permiso(null, "GESTIONAR_USUARIOS", "   ");
        when(permisoRepository.existsByNombrePermiso("GESTIONAR_USUARIOS")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Permiso resultado = permisoService.guardarPermiso(permiso);

        // Then
        assertNull(resultado.getDescripcion());
        verify(permisoRepository).save(permiso);
    }

    @Test
    void guardarPermiso_deberiaLanzarExcepcionCuandoNombreYaExiste() {
        // Given
        Permiso permiso = new Permiso(null, "GESTIONAR_USUARIOS", "Duplicado");
        when(permisoRepository.existsByNombrePermiso("GESTIONAR_USUARIOS")).thenReturn(true);

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> permisoService.guardarPermiso(permiso)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(permisoRepository, never()).save(any(Permiso.class));
    }

    @Test
    void actualizarPermiso_deberiaActualizarCuandoExiste() {
        // Given
        Permiso existente = new Permiso(1L, "ANTIGUO", "Descripción antigua");
        Permiso actualizado = new Permiso(null, "NUEVO", "Descripción nueva");

        when(permisoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(permisoRepository.existsByNombrePermiso("NUEVO")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Permiso resultado = permisoService.actualizarPermiso(1L, actualizado);

        // Then
        assertEquals("NUEVO", resultado.getNombrePermiso());
        assertEquals("Descripción nueva", resultado.getDescripcion());
        verify(permisoRepository).save(existente);
    }

    @Test
    void actualizarPermiso_deberiaActualizarCuandoNombreEsIgual() {
        // Given
        Permiso existente = new Permiso(1L, "GESTIONAR_USUARIOS", "Antes");
        Permiso actualizado = new Permiso(null, "GESTIONAR_USUARIOS", "Después");

        when(permisoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Permiso resultado = permisoService.actualizarPermiso(1L, actualizado);

        // Then
        assertEquals("GESTIONAR_USUARIOS", resultado.getNombrePermiso());
        assertEquals("Después", resultado.getDescripcion());
        verify(permisoRepository, never()).existsByNombrePermiso("GESTIONAR_USUARIOS");
    }

    @Test
    void actualizarPermiso_deberiaLanzarExcepcionCuandoNoExiste() {
        // Given
        Permiso actualizado = new Permiso(null, "NUEVO", "Descripción");
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> permisoService.actualizarPermiso(99L, actualizado)
        );

        assertTrue(exception.getMessage().contains("Permiso no encontrado"));
        verify(permisoRepository, never()).save(any(Permiso.class));
    }

    @Test
    void actualizarPermiso_deberiaLanzarExcepcionCuandoNombreNuevoYaExiste() {
        // Given
        Permiso existente = new Permiso(1L, "ANTIGUO", "Antes");
        Permiso actualizado = new Permiso(null, "NUEVO", "Después");

        when(permisoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(permisoRepository.existsByNombrePermiso("NUEVO")).thenReturn(true);

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> permisoService.actualizarPermiso(1L, actualizado)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(permisoRepository, never()).save(any(Permiso.class));
    }

    @Test
    void eliminarPermiso_deberiaEliminarCuandoExiste() {
        // Given
        when(permisoRepository.existsById(1L)).thenReturn(true);

        // When
        permisoService.eliminarPermiso(1L);

        // Then
        verify(permisoRepository).deleteById(1L);
    }

    @Test
    void eliminarPermiso_deberiaLanzarExcepcionCuandoNoExiste() {
        // Given
        when(permisoRepository.existsById(99L)).thenReturn(false);

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> permisoService.eliminarPermiso(99L)
        );

        assertTrue(exception.getMessage().contains("Permiso no encontrado"));
        verify(permisoRepository, never()).deleteById(anyLong());
    }
}