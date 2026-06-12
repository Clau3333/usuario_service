package cl.perfulandia.usuarios.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.perfulandia.usuarios.dto.CambiarEstadoRequest;
import cl.perfulandia.usuarios.dto.CambiarPasswordRequest;
import cl.perfulandia.usuarios.dto.CambiarRolRequest;
import cl.perfulandia.usuarios.dto.UsuarioResponse;
import cl.perfulandia.usuarios.model.Usuario;
import cl.perfulandia.usuarios.service.UsuarioService;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public CollectionModel<EntityModel<UsuarioResponse>> listarUsuarios() {
        List<EntityModel<UsuarioResponse>> usuarios = usuarioService.listarUsuarios()
                .stream()
                .map(usuarioService::convertirAUsuarioResponse)
                .map(this::agregarLinks)
                .toList();

        return CollectionModel.of(
                usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel()
        );
    }

    @GetMapping("/estado/{estado}")
    public CollectionModel<EntityModel<UsuarioResponse>> listarUsuariosPorEstado(@PathVariable Boolean estado) {
        List<EntityModel<UsuarioResponse>> usuarios = usuarioService.listarUsuariosPorEstado(estado)
                .stream()
                .map(usuarioService::convertirAUsuarioResponse)
                .map(this::agregarLinks)
                .toList();

        return CollectionModel.of(
                usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuariosPorEstado(estado)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponse>> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(usuarioService::convertirAUsuarioResponse)
                .map(this::agregarLinks)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<UsuarioResponse>> guardarUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario);
        UsuarioResponse response = usuarioService.convertirAUsuarioResponse(usuarioGuardado);

        return ResponseEntity
                .created(
                        linkTo(methodOn(UsuarioController.class)
                                .buscarUsuarioPorId(usuarioGuardado.getIdUsuario()))
                                .toUri()
                )
                .body(agregarLinks(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponse>> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {

        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        UsuarioResponse response = usuarioService.convertirAUsuarioResponse(usuarioActualizado);

        return ResponseEntity.ok(agregarLinks(response));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<EntityModel<UsuarioResponse>> cambiarEstadoUsuario(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {

        Usuario usuarioActualizado = usuarioService.cambiarEstadoUsuario(id, request.getEstado());
        UsuarioResponse response = usuarioService.convertirAUsuarioResponse(usuarioActualizado);

        return ResponseEntity.ok(agregarLinks(response));
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<EntityModel<UsuarioResponse>> cambiarRolUsuario(
            @PathVariable Long id,
            @Valid @RequestBody CambiarRolRequest request) {

        Usuario usuarioActualizado = usuarioService.cambiarRolUsuario(id, request.getIdRol());
        UsuarioResponse response = usuarioService.convertirAUsuarioResponse(usuarioActualizado);

        return ResponseEntity.ok(agregarLinks(response));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<EntityModel<UsuarioResponse>> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordRequest request) {

        Usuario usuarioActualizado = usuarioService.cambiarPassword(
                id,
                request.getPasswordActual(),
                request.getPasswordNueva()
        );

        UsuarioResponse response = usuarioService.convertirAUsuarioResponse(usuarioActualizado);

        return ResponseEntity.ok(agregarLinks(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<UsuarioResponse> agregarLinks(UsuarioResponse usuario) {
        return EntityModel.of(
                usuario,
                linkTo(methodOn(UsuarioController.class)
                        .buscarUsuarioPorId(usuario.getIdUsuario()))
                        .withSelfRel(),
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios())
                        .withRel("usuarios"),
                linkTo(methodOn(UsuarioController.class)
                        .cambiarEstadoUsuario(usuario.getIdUsuario(), new CambiarEstadoRequest()))
                        .withRel("cambiar-estado"),
                linkTo(methodOn(UsuarioController.class)
                        .cambiarRolUsuario(usuario.getIdUsuario(), new CambiarRolRequest()))
                        .withRel("cambiar-rol"),
                linkTo(methodOn(UsuarioController.class)
                        .cambiarPassword(usuario.getIdUsuario(), new CambiarPasswordRequest()))
                        .withRel("cambiar-password")
        );
    }
}