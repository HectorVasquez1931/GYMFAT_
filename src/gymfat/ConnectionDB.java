package gymfat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static Connection connection = null;

    public static Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                String servidor = "localhost\\SQLEXPRESS01";
                String baseDatos = "Gimnasio";
                String url = "jdbc:sqlserver://" + servidor + ":1433;"
                        + "databaseName=" + baseDatos + ";"
                        + "integratedSecurity=true;"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;";
                connection = DriverManager.getConnection(url);
                System.out.println("Conexion exitosa");
            }
        } catch (SQLException e) {
            System.out.println("Conexion fallida");
            System.out.println("Error: " + e.getMessage());
        }
        return connection;
    }
}