// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - InicioSesion
// Descripción: Ventana de inicio de sesión. Valida el usuario y la contraseña
// contra la base de datos y abre la ventana principal con el nivel del usuario.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

// Ventana con la que el usuario entra al sistema
public class InicioSesion extends JFrame implements ActionListener {

    // Constantes

    // Algoritmo con el que se guardan las contraseñas en la base de datos
    private static final String ALGORITMO_HASH = "SHA-256";

    // Formato de cada byte del hash: dos dígitos hexadecimales en mayúsculas
    private static final String FORMATO_BYTE_HEXADECIMAL = "%02X";

    // Tamaños de la ventana y de sus paneles, en píxeles
    private static final int ANCHO_VENTANA = 700;
    private static final int ALTO_VENTANA = 300;
    private static final int ANCHO_PANEL_IZQUIERDO = 300;
    private static final int ANCHO_PANEL_DERECHO = 386;
    private static final int ALTO_CAMPO = 40;

    // Colores propios de esta ventana
    private static final Color NEGRO_CLARO = new Color(17, 17, 17);
    private static final Color FONDO_CAMPO = new Color(26, 26, 26);
    private static final Color BORDE_CAMPO = new Color(42, 42, 42);
    private static final Color GRIS_SUBTITULO = new Color(68, 68, 68);

    // Fuentes de esta ventana
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 30);
    private static final Font FUENTE_ETIQUETA = new Font(Estilos.FUENTE, Font.BOLD, 10);
    private static final Font FUENTE_CAMPO = new Font(Estilos.FUENTE, Font.PLAIN, 12);
    private static final Font FUENTE_BOTON = new Font(Estilos.FUENTE, Font.BOLD, 14);

    // Atributos y componentes de la interfaz

    private JTextField txtUsuario;
    private JPasswordField pwdContrasena;

    // Constructor

    // Crea la ventana de inicio de sesión y la muestra
    public InicioSesion() {
        super("GymFat");
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        construirInterfaz();
        setVisible(true);
    }

    // Construcción de la interfaz

    // Coloca el nombre del sistema a la izquierda y el formulario a la derecha
    private void construirInterfaz() {
        add(crearPanelLogotipo(), BorderLayout.WEST);
        add(crearPanelFormulario(), BorderLayout.EAST);
    }

    // Crea el panel negro con el título "GymFat" y el subtítulo, centrados verticalmente
    private JPanel crearPanelLogotipo() {
        JPanel pnlLogotipo = new JPanel();
        pnlLogotipo.setLayout(new BoxLayout(pnlLogotipo, BoxLayout.Y_AXIS));
        pnlLogotipo.setBackground(Color.BLACK);
        pnlLogotipo.setPreferredSize(new Dimension(ANCHO_PANEL_IZQUIERDO, 0));

        JLabel lblTitulo = new JLabel("GymFat", SwingConstants.CENTER);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(Estilos.AMARILLO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Sistema de gestión", SwingConstants.CENTER);
        lblSubtitulo.setFont(FUENTE_ETIQUETA);
        lblSubtitulo.setForeground(GRIS_SUBTITULO);
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

        pnlLogotipo.add(Box.createVerticalGlue());
        pnlLogotipo.add(lblTitulo);
        pnlLogotipo.add(lblSubtitulo);
        pnlLogotipo.add(Box.createVerticalGlue());
        return pnlLogotipo;
    }

    // Crea el formulario con los campos de usuario y contraseña y el botón Entrar
    private JPanel crearPanelFormulario() {
        JPanel pnlFormulario = new JPanel();
        pnlFormulario.setLayout(new BoxLayout(pnlFormulario, BoxLayout.Y_AXIS));
        pnlFormulario.setBackground(NEGRO_CLARO);
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        pnlFormulario.setPreferredSize(new Dimension(ANCHO_PANEL_DERECHO, 0));
        pnlFormulario.add(Box.createVerticalGlue());

        txtUsuario = new JTextField();
        pnlFormulario.add(crearEtiquetaCampo("USUARIO"));
        pnlFormulario.add(darEstiloCampo(txtUsuario));
        pnlFormulario.add(Box.createRigidArea(new Dimension(0, 14)));

        pwdContrasena = new JPasswordField();
        pnlFormulario.add(crearEtiquetaCampo("CONTRASEÑA"));
        pnlFormulario.add(darEstiloCampo(pwdContrasena));
        pnlFormulario.add(Box.createRigidArea(new Dimension(0, 20)));

        pnlFormulario.add(crearBotonEntrar());
        pnlFormulario.add(Box.createVerticalGlue());
        return pnlFormulario;
    }

    // Crea el botón Entrar, que ocupa todo el ancho del formulario
    private JButton crearBotonEntrar() {
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_CAMPO));
        btnEntrar.setBackground(Estilos.AMARILLO);
        btnEntrar.setForeground(Color.BLACK);
        btnEntrar.setFont(FUENTE_BOTON);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setAlignmentX(LEFT_ALIGNMENT);
        btnEntrar.addActionListener(this);
        return btnEntrar;
    }

    // Eventos

    // Se ejecuta al presionar el botón Entrar
    @Override
    public void actionPerformed(ActionEvent evento) {
        iniciarSesion();
    }

    // Valida que los campos no estén vacíos y busca al usuario en la base de datos.
    // Si es correcto, cierra esta ventana y abre la ventana principal.
    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(pwdContrasena.getPassword()).trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa usuario y contraseña.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String[] datosUsuario = consultarUsuario(usuario, generarHash(contrasena));
            if (datosUsuario == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                pwdContrasena.setText("");
                return;
            }
            dispose();
            new VentanaPrincipal(datosUsuario[0], datosUsuario[1]);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo iniciar sesión.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchAlgorithmException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo validar la contraseña.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Acceso a datos

    // Busca al usuario con la contraseña ya cifrada. Devuelve su nivel y su nombre
    // (en ese orden), o null si el usuario o la contraseña no coinciden.
    private String[] consultarUsuario(String usuario, String contrasenaCifrada) throws SQLException {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            throw new SQLException("Sin conexión con la base de datos");
        }

        // Obtiene el nivel del usuario y el nombre del empleado al que pertenece
        String consulta = "SELECT u.nivel, e.nombres, e.apellido_paterno "
                + "FROM usuario u "
                + "JOIN empleado e ON u.id_empleado = e.id_empleado "
                + "WHERE u.username = ? AND u.password = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, usuario);
            sentencia.setString(2, contrasenaCifrada);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }
                String nivel = resultado.getString("nivel");
                String nombreCompleto = resultado.getString("nombres") + " "
                        + resultado.getString("apellido_paterno");
                return new String[]{nivel, nombreCompleto};
            }
        }
    }

    // Métodos auxiliares

    // Cifra la contraseña con SHA-256 y la devuelve en hexadecimal, igual que
    // como está guardada en la base de datos
    private String generarHash(String contrasena) throws NoSuchAlgorithmException {
        MessageDigest cifrador = MessageDigest.getInstance(ALGORITMO_HASH);
        byte[] bytesHash = cifrador.digest(contrasena.getBytes(StandardCharsets.UTF_8));

        StringBuilder hashHexadecimal = new StringBuilder();
        for (byte valorByte : bytesHash) {
            hashHexadecimal.append(String.format(FORMATO_BYTE_HEXADECIMAL, valorByte));
        }
        return hashHexadecimal.toString();
    }

    // Crea la etiqueta gris que va arriba de cada campo
    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lblCampo = new JLabel(texto);
        lblCampo.setFont(FUENTE_ETIQUETA);
        lblCampo.setForeground(Color.GRAY);
        lblCampo.setAlignmentX(LEFT_ALIGNMENT);
        return lblCampo;
    }

    // Aplica el estilo oscuro a un campo de texto y lo devuelve
    private JComponent darEstiloCampo(JTextField campo) {
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_CAMPO));
        campo.setBackground(FONDO_CAMPO);
        campo.setForeground(Color.WHITE);
        campo.setFont(FUENTE_CAMPO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        campo.setAlignmentX(LEFT_ALIGNMENT);
        return campo;
    }
}