package gymfat;

public class GymFat {

public static void main(String[] args) {
    ConnectionDB.connect();
    javax.swing.SwingUtilities.invokeLater(() -> new Login());
}
}
