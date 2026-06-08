package cl.perfulandia.usuarios.controller;

import cl.perfulandia.usuarios.dto.LoginRequest;
import cl.perfulandia.usuarios.dto.LoginResponse;
import cl.perfulandia.usuarios.model.Usuario;
import cl.perfulandia.usuarios.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return usuarioService.validarLogin(loginRequest.getCorreo(), loginRequest.getPassword())
                .map(this::crearLoginCorrecto)
                .orElseGet(this::crearLoginIncorrecto);
    }

    private ResponseEntity<LoginResponse> crearLoginCorrecto(Usuario usuario) {
        String nombreRol = usuario.getRol() != null
                ? usuario.getRol().getNombreRol()
                : "SIN_ROL";

        LoginResponse response = new LoginResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                nombreRol,
                "Inicio de sesión correcto"
        );

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<LoginResponse> crearLoginIncorrecto() {
        LoginResponse response = new LoginResponse(
                null,
                null,
                null,
                null,
                "Credenciales incorrectas o usuario inactivo"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}