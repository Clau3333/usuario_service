package cl.perfulandia.usuarios.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    private final PasswordUtil passwordUtil = new PasswordUtil();

    @Test
    void generarHash_deberiaRetornarHashCuandoPasswordEsValida() {
        // Given: tenemos una contraseña normal
        String password = "admin123";

        // When: generamos el hash
        String hash = passwordUtil.generarHash(password);

        // Then: el hash existe y no es igual a la contraseña original
        assertNotNull(hash);
        assertFalse(hash.isBlank());
        assertNotEquals(password, hash);
    }

    @Test
    void generarHash_deberiaRetornarMismoHashParaMismaPassword() {
        // Given: usamos la misma contraseña dos veces
        String password = "cliente123";

        // When: generamos dos hashes
        String hashUno = passwordUtil.generarHash(password);
        String hashDos = passwordUtil.generarHash(password);

        // Then: como usa SHA-256, ambos hashes deben ser iguales
        assertEquals(hashUno, hashDos);
    }

    @Test
    void generarHash_deberiaLanzarExcepcionCuandoPasswordEsNull() {
        // Given: contraseña nula
        String password = null;

        // When / Then: debe lanzar error
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordUtil.generarHash(password)
        );

        assertTrue(exception.getMessage().contains("contraseña"));
    }

    @Test
    void generarHash_deberiaLanzarExcepcionCuandoPasswordEstaVacia() {
        // Given: contraseña vacía
        String password = "   ";

        // When / Then: debe lanzar error
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordUtil.generarHash(password)
        );

        assertTrue(exception.getMessage().contains("contraseña"));
    }

    @Test
    void verificarPassword_deberiaRetornarTrueCuandoPasswordCoincide() {
        // Given: contraseña y hash guardado
        String password = "ventas123";
        String hashGuardado = passwordUtil.generarHash(password);

        // When: verificamos la contraseña
        boolean resultado = passwordUtil.verificarPassword(password, hashGuardado);

        // Then: debe coincidir
        assertTrue(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoPasswordNoCoincide() {
        // Given: contraseña correcta y otra incorrecta
        String passwordCorrecta = "ventas123";
        String passwordIncorrecta = "otraClave123";
        String hashGuardado = passwordUtil.generarHash(passwordCorrecta);

        // When: verificamos con contraseña incorrecta
        boolean resultado = passwordUtil.verificarPassword(passwordIncorrecta, hashGuardado);

        // Then: no debe coincidir
        assertFalse(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoPasswordIngresadaEsNull() {
        // Given: contraseña ingresada nula
        String hashGuardado = passwordUtil.generarHash("admin123");

        // When
        boolean resultado = passwordUtil.verificarPassword(null, hashGuardado);

        // Then
        assertFalse(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoPasswordIngresadaEstaVacia() {
        // Given: contraseña ingresada vacía
        String hashGuardado = passwordUtil.generarHash("admin123");

        // When
        boolean resultado = passwordUtil.verificarPassword(" ", hashGuardado);

        // Then
        assertFalse(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoHashGuardadoEsNull() {
        // Given: no existe hash guardado

        // When
        boolean resultado = passwordUtil.verificarPassword("admin123", null);

        // Then
        assertFalse(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoHashGuardadoEstaVacio() {
        // Given: hash guardado vacío

        // When
        boolean resultado = passwordUtil.verificarPassword("admin123", " ");

        // Then
        assertFalse(resultado);
    }
}