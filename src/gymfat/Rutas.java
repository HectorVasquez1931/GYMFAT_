// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - Rutas
// Descripción: Reúne las carpetas del disco que usa el sistema para guardar
// las fotos de los miembros y los recibos, para declararlas una sola vez.
// Fecha: 09/11/2026
package gymfat;

// Carpetas donde el sistema guarda archivos
public final class Rutas {

    // Constantes

    // Carpeta de las fotos de los miembros; cada foto se nombra con el ID del miembro
    public static final String RUTA_FOTOS = "C:\\GymFat\\fotos\\";

    // Carpeta de los recibos que se generan al registrar un miembro
    public static final String RUTA_RECIBOS = "C:\\GymFat\\recibos\\";

    // Constructor

    // Constructor privado: la clase solo contiene constantes
    private Rutas() {
    }
}