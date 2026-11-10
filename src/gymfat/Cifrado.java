// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - Cifrado
// Descripción: Cifra las contraseñas con SHA-256 en el mismo formato en que se
// guardan en la base de datos. La usan el inicio de sesión y el alta de usuarios.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Cifrado de contraseñas compartido por las pantallas del sistema
public final class Cifrado {

    // Constantes

    // Algoritmo con el que se guardan las contraseñas en la base de datos
    private static final String ALGORITMO_HASH = "SHA-256";

    // Formato de cada byte del hash: dos dígitos hexadecimales en mayúsculas
    private static final String FORMATO_BYTE_HEXADECIMAL = "%02X";

    // Constructor

    // Constructor privado: la clase solo se usa a través de su método estático
    private Cifrado() {
    }

    // Métodos auxiliares

    // Cifra la contraseña con SHA-256 y la devuelve en hexadecimal en mayúsculas
    public static String generarHash(String contrasena) throws NoSuchAlgorithmException {
        MessageDigest cifrador = MessageDigest.getInstance(ALGORITMO_HASH);
        byte[] bytesHash = cifrador.digest(contrasena.getBytes(StandardCharsets.UTF_8));

        StringBuilder hashHexadecimal = new StringBuilder();
        for (byte valorByte : bytesHash) {
            hashHexadecimal.append(String.format(FORMATO_BYTE_HEXADECIMAL, valorByte));
        }
        return hashHexadecimal.toString();
    }
}