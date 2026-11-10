// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - ConexionBD
// Descripción: Abre y reutiliza la conexión con la base de datos Gimnasio en
// SQL Server. Todas las clases obtienen la conexión con conectar().
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// Administra la conexión única con la base de datos del sistema
public class ConexionBD {

    // Constantes

    // Instancia de SQL Server donde está la base de datos
    private static final String SERVIDOR = "localhost\\SQLEXPRESS01";

    // Puerto en el que escucha SQL Server
    private static final int PUERTO = 1433;

    // Nombre de la base de datos del gimnasio
    private static final String BASE_DATOS = "Gimnasio";

    // Cadena de conexión: usa la autenticación de Windows y acepta el
    // certificado local del servidor
    private static final String URL_CONEXION = "jdbc:sqlserver://" + SERVIDOR + ":" + PUERTO + ";"
            + "databaseName=" + BASE_DATOS + ";"
            + "integratedSecurity=true;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    // Atributos

    // Conexión compartida por todo el sistema; se crea la primera vez que se pide
    private static Connection conexion = null;

    // Constructor

    // Constructor privado: la clase solo se usa a través de sus métodos estáticos
    private ConexionBD() {
    }

    // Acceso a datos

    // Devuelve la conexión abierta. Si aún no existe o se cerró, la abre de nuevo.
    // Si no se puede conectar, avisa al usuario y devuelve null.
    public static Connection conectar() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL_CONEXION);
            }
        } catch (SQLException ex) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null,
                            "No se pudo conectar con la base de datos.", "Error",
                            JOptionPane.ERROR_MESSAGE));
        }
        return conexion;
    }
}