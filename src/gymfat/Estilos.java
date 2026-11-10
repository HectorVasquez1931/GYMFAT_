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

    // Color para acciones de confirmar y estados correctos (membresía activa)
    public static final Color VERDE = new Color(46, 204, 113);

    // Color para acciones de cancelar y estados con problema (membresía vencida)
    public static final Color ROJO = new Color(231, 76, 60);

    // Fondo gris claro de las pantallas de contenido
    public static final Color FONDO_PANTALLA = new Color(245, 245, 245);

    // Colores de las pantallas de lista (miembros, entrenadores, empleados)

    // Fondo de la pantalla y de la barra de búsqueda
    public static final Color FONDO_LISTA = new Color(248, 248, 248);

    // Fondo de las filas impares de las tablas
    public static final Color GRIS_FILA = new Color(252, 252, 252);

    // Líneas que separan el encabezado y el pie de página del resto de la pantalla
    public static final Color GRIS_BORDE = new Color(220, 220, 220);

    // Borde de los campos de texto
    public static final Color GRIS_BORDE_CAMPO = new Color(200, 200, 200);

    // Líneas horizontales entre las filas de las tablas
    public static final Color GRIS_CUADRICULA = new Color(235, 235, 235);

    // Fondo de botones secundarios, como "Mostrar todos"
    public static final Color FONDO_BOTON_SECUNDARIO = new Color(240, 240, 240);

    // Fondo de la fila seleccionada en las tablas
    public static final Color AMARILLO_SELECCION = new Color(255, 240, 150);

    // Texto de los títulos de las columnas
    public static final Color GRIS_TEXTO_ENCABEZADO = new Color(80, 80, 80);

    // Texto de las filas de las tablas
    public static final Color GRIS_TEXTO_FILA = new Color(40, 40, 40);

    // Colores de las ventanas oscuras (inicio de sesión y diálogos de alta)

    // Fondo del panel donde está el formulario
    public static final Color FONDO_FORMULARIO = new Color(17, 17, 17);

    // Fondo de los campos de texto del formulario
    public static final Color FONDO_CAMPO_OSCURO = new Color(26, 26, 26);

    // Borde de los campos de texto del formulario
    public static final Color BORDE_CAMPO_OSCURO = new Color(42, 42, 42);

    // Texto secundario debajo del título, como el subtítulo o el nombre del empleado
    public static final Color GRIS_SUBTITULO = new Color(68, 68, 68);

    // Nombre de la fuente que usan todas las pantallas
    public static final String FUENTE = "Arial";

    // Constructor

    // Constructor privado: la clase solo contiene constantes
    private Estilos() {
    }
}