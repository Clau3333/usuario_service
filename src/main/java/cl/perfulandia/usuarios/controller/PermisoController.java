package cl.perfulandia.usuarios.controller;

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.service.PermisoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @GetMapping
    public List<Permiso> listarPermisos() {
        return permisoService.listarPermisos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permiso> buscarPermisoPorId(@PathVariable Long id) {
        return permisoService.buscarPermisoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Permiso guardarPermiso(@Valid @RequestBody Permiso permiso) {
        return permisoService.guardarPermiso(permiso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permiso> actualizarPermiso(@PathVariable Long id, @Valid @RequestBody Permiso permiso) {
        try {
            return ResponseEntity.ok(permisoService.actualizarPermiso(id, permiso));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long id) {
        permisoService.eliminarPermiso(id);
        return ResponseEntity.noContent().build();
    }
}