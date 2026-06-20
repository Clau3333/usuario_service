package cl.perfulandia.usuarios.service;

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.repository.PermisoRepository;
import cl.perfulandia.usuarios.repository.RolRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PermisoRepository permisoRepository;

    @InjectMocks
    private RolService rolService;

    @Test
    void listarRoles_deberiaRetornarLista() {
        // Given
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Administra el sistema", new HashSet<>());
        when(rolRepository.findAll()).thenReturn(List.of(rol));

        // When
        List<Rol> resultado = rolService.listarRoles();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("ADMINISTRADOR", resultado.get(0).getNombreRol());
        verify(rolRepository).findAll();
    }

    @Test
    void buscarRolPorId_deberiaRetornarRolCuandoExiste() {
        // Given
        Rol rol = new Rol(1L, "CLIENTE", "Cliente web", new HashSet<>());
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        // When
        Optional<Rol> resultado = rolService.buscarRolPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("CLIENTE", resultado.get().getNombreRol());
        verify(rolRepository).findById(1L);
    }

    @Test
    void buscarRolPorId_deberiaRetornarVacioCuandoNoExiste() {
        // Given
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Rol> resultado = rolService.buscarRolPorId(99L);

        // Then
        assertTrue(resultado.isEmpty());
        verify(rolRepository).findById(99L);
    }

    @Test
    void guardarRol_deberiaGuardarCuandoNoExisteNombre() {
        // Given
        Rol rol = new Rol(null, " ADMINISTRADOR ", " Gestiona usuarios ", new HashSet<>());
        when(rolRepository.existsByNombreRol("ADMINISTRADOR")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> {
            Rol guardado = invocation.getArgument(0);
            guardado.setIdRol(1L);
            return guardado;
        });

        // When
        Rol resultado = rolService.guardarRol(rol);

        // Then
        assertEquals(1L, resultado.getIdRol());
        assertEquals("ADMINISTRADOR", resultado.getNombreRol());
        assertEquals("Gestiona usuarios", resultado.getDescripcion());
        verify(rolRepository).save(rol);
    }

    @Test
    void guardarRol_deberiaConvertirDescripcionVaciaEnNull() {
        // Given
        Rol rol = new Rol(null, "CLIENTE", "   ", new HashSet<>());
        when(rolRepository.existsByNombreRol("CLIENTE")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rol resultado = rolService.guardarRol(rol);

        // Then
        assertNull(resultado.getDescripcion());
        verify(rolRepository).save(rol);
    }

    @Test
    void guardarRol_deberiaPermitirDescripcionNull() {
        // Given
        Rol rol = new Rol(null, "CLIENTE", null, new HashSet<>());
        when(rolRepository.existsByNombreRol("CLIENTE")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rol resultado = rolService.guardarRol(rol);

        // Then
        assertNull(resultado.getDescripcion());
        verify(rolRepository).save(rol);
    }

    @Test
    void guardarRol_deberiaLanzarExcepcionCuandoNombreYaExiste() {
        // Given
        Rol rol = new Rol(null, "ADMINISTRADOR", "Duplicado", new HashSet<>());
        when(rolRepository.existsByNombreRol("ADMINISTRADOR")).thenReturn(true);

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.guardarRol(rol)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void actualizarRol_deberiaActualizarCuandoExiste() {
        // Given
        Rol existente = new Rol(1L, "ANTIGUO", "Antes", new HashSet<>());
        Rol actualizado = new Rol(null, "NUEVO", "Después", new HashSet<>());

        when(rolRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.existsByNombreRol("NUEVO")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rol resultado = rolService.actualizarRol(1L, actualizado);

        // Then
        assertEquals("NUEVO", resultado.getNombreRol());
        assertEquals("Después", resultado.getDescripcion());
        verify(rolRepository).save(existente);
    }

    @Test
    void actualizarRol_deberiaActualizarCuandoNombreEsIgual() {
        // Given
        Rol existente = new Rol(1L, "CLIENTE", "Antes", new HashSet<>());
        Rol actualizado = new Rol(null, "CLIENTE", "Después", new HashSet<>());

        when(rolRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rol resultado = rolService.actualizarRol(1L, actualizado);

        // Then
        assertEquals("CLIENTE", resultado.getNombreRol());
        assertEquals("Después", resultado.getDescripcion());
        verify(rolRepository, never()).existsByNombreRol("CLIENTE");
    }

    @Test
    void actualizarRol_deberiaLanzarExcepcionCuandoNoExiste() {
        // Given
        Rol actualizado = new Rol(null, "NUEVO", "Después", new HashSet<>());
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.actualizarRol(99L, actualizado)
        );

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void actualizarRol_deberiaLanzarExcepcionCuandoNombreNuevoYaExiste() {
        // Given
        Rol existente = new Rol(1L, "ANTIGUO", "Antes", new HashSet<>());
        Rol actualizado = new Rol(null, "NUEVO", "Después", new HashSet<>());

        when(rolRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.existsByNombreRol("NUEVO")).thenReturn(true);

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.actualizarRol(1L, actualizado)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void agregarPermisoARol_deberiaAgregarPermisoCuandoAmbosExisten() {
        // Given
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Admin", new HashSet<>());
        Permiso permiso = new Permiso(1L, "GESTIONAR_USUARIOS", "Gestionar usuarios");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permiso));
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rol resultado = rolService.agregarPermisoARol(1L, 1L);

        // Then
        assertEquals(1, resultado.getPermisos().size());
        assertTrue(resultado.getPermisos().contains(permiso));
        verify(rolRepository).save(rol);
    }

    @Test
    void agregarPermisoARol_deberiaLanzarExcepcionCuandoRolNoExiste() {
        // Given
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.agregarPermisoARol(99L, 1L)
        );

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(permisoRepository, never()).findById(anyLong());
    }

    @Test
    void agregarPermisoARol_deberiaLanzarExcepcionCuandoPermisoNoExiste() {
        // Given
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Admin", new HashSet<>());

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.agregarPermisoARol(1L, 99L)
        );

        assertTrue(exception.getMessage().contains("Permiso no encontrado"));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void eliminarRol_deberiaEliminarCuandoExiste() {
        // Given
        when(rolRepository.existsById(1L)).thenReturn(true);

        // When
        rolService.eliminarRol(1L);

        // Then
        verify(rolRepository).deleteById(1L);
    }

    @Test
    void eliminarRol_deberiaLanzarExcepcionCuandoNoExiste() {
        // Given
        when(rolRepository.existsById(99L)).thenReturn(false);

        // When / Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.eliminarRol(99L)
        );

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(rolRepository, never()).deleteById(anyLong());
    }
}