package cl.perfulandia.usuarios.service;

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.repository.PermisoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermisoService {

    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public List<Permiso> listarPermisos() {
        return permisoRepository.findAll();
    }

    public Optional<Permiso> buscarPermisoPorId(Long id) {
        return permisoRepository.findById(id);
    }

    public Permiso guardarPermiso(Permiso permiso) {
        return permisoRepository.save(permiso);
    }

    public Permiso actualizarPermiso(Long id, Permiso permisoActualizado) {
        Permiso permisoExistente = permisoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

        permisoExistente.setNombrePermiso(permisoActualizado.getNombrePermiso());
        permisoExistente.setDescripcion(permisoActualizado.getDescripcion());

        return permisoRepository.save(permisoExistente);
    }

    public void eliminarPermiso(Long id) {
        permisoRepository.deleteById(id);
    }
}