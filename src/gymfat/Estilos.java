// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - Estilos
// Descripción: Reúne los colores y la fuente que comparten varias pantallas
// del sistema, para declararlos una sola vez.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.Color;

// Colores y fuente comunes de la interfaz de GymFat
public final class Estilos {

    // Constantes

    // Color distintivo del sistema: título, botón activo y botones principales
    public static final Color AMARILLO = new Color(255, 220, 0);

    // Color del texto normal sobre fondos oscuros
    public static final Color GRIS_TEXTO = new Color(180, 180, 180);

    // Fondo de los botones cuando el cursor pasa sobre ellos
    public static final Color GRIS_OSCURO = new Color(40, 40, 40);

    // Nombre de la fuente que usan todas las pantallas
    public static final String FUENTE = "Arial";

    // Constructor

    // Constructor privado: la clase solo contiene constantes
    private Estilos() {
    }
}