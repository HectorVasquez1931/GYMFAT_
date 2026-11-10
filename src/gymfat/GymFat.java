// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - GymFat
// Descripción: Clase principal del sistema. Abre la conexión con la base de
// datos y muestra la ventana de inicio de sesión.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import javax.swing.SwingUtilities;

// Punto de entrada del sistema GymFat
public class GymFat {

    // Constructor

    // Constructor privado: la clase solo contiene el método main
    private GymFat() {
    }

    // Eventos

    // Abre la conexión desde el inicio, para avisar de inmediato si la base de datos
    // no está disponible, y después muestra la ventana de inicio de sesión
    public static void main(String[] args) {
        ConexionBD.conectar();
        SwingUtilities.invokeLater(InicioSesion::new);
    }
}