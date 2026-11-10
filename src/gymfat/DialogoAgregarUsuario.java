// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - DialogoAgregarUsuario
// Descripción: Diálogo para crear el usuario del sistema de un empleado. Valida
// el usuario y la contraseña, la cifra y guarda el usuario con su nivel.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

// Diálogo para dar de alta un usuario del sistema
public class DialogoAgregarUsuario extends JDialog implements ActionListener {

    // Constantes

    // Comandos de los botones
    private static final String COMANDO_CANCELAR = "Cancelar";
    private static final String COMANDO_GUARDAR = "Guardar";

    // Niveles de usuario que se pueden asignar
    private static final String[] NIVELES = {"Recepcionista", "Administrador"};

    // Número mínimo de caracteres de la contraseña
    private static final int LONGITUD_MINIMA_CONTRASENA = 4;

    // Texto de la etiqueta de error cuando no hay error; un espacio para que conserve su alto
    private static final String SIN_ERROR = " ";

    // Tamaños, en píxeles
    private static final int ANCHO_DIALOGO = 800;
    private static final int ALTO_DIALOGO = 260;
    private static final int ANCHO_PANEL_IZQUIERDO = 180;
    private static final int ALTO_FILA_CAMPOS = 58;
    private static final int ALTO_CAMPO = 36;
    private static final int ALTO_FILA_BOTONES = 40;

    // Fuentes de este diálogo
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 22);
    private static final Font FUENTE_EMPLEADO = new Font(Estilos.FUENTE, Font.PLAIN, 10);
    private static final Font FUENTE_ETIQUETA = new Font(Estilos.FUENTE, Font.BOLD, 10);
    private static final Font FUENTE_CAMPO = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_ERROR = new Font(Estilos.FUENTE, Font.PLAIN, 11);
    private static final Font FUENTE_BOTON_CANCELAR = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_BOTON_GUARDAR = new Font(Estilos.FUENTE, Font.BOLD, 13);

    // Atributos y componentes de la interfaz

    // Empleado al que se le crea el usuario
    private final int idEmpleado;
    private final String nombreEmpleado;

    private JTextField txtUsuario;
    private JPasswordField pwdContrasena;
    private JPasswordField pwdConfirmarContrasena;
    private JComboBox<String> cmbNivel;

    // Muestra los errores de validación debajo de los campos
    private JLabel lblError;

    // Constructor

    // Crea el diálogo modal para el empleado indicado y lo muestra
    public DialogoAgregarUsuario(Window ventanaPadre, int idEmpleado, String nombreEmpleado) {
        super(ventanaPadre, "Nuevo usuario en el sistema", ModalityType.APPLICATION_MODAL);
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        setSize(ANCHO_DIALOGO, ALTO_DIALOGO);
        setLocationRelativeTo(ventanaPadre);
        setResizable(false);
        setLayout(new BorderLayout());
        construirInterfaz();
        setVisible(true);
    }

    // Construcción de la interfaz

    // Coloca el título a la izquierda y el formulario a la derecha
    private void construirInterfaz() {
        add(crearPanelTitulo(), BorderLayout.WEST);
        add(crearPanelFormulario(), BorderLayout.CENTER);
    }

    // Crea el panel negro con el título "Nuevo usuario" y el nombre del empleado
    private JPanel crearPanelTitulo() {
        JPanel pnlTitulo = new JPanel();
        pnlTitulo.setLayout(new BoxLayout(pnlTitulo, BoxLayout.Y_AXIS));
        pnlTitulo.setBackground(Color.BLACK);
        pnlTitulo.setPreferredSize(new Dimension(ANCHO_PANEL_IZQUIERDO, 0));

        JLabel lblEmpleado = new JLabel(nombreEmpleado, SwingConstants.CENTER);
        lblEmpleado.setFont(FUENTE_EMPLEADO);
        lblEmpleado.setForeground(Estilos.GRIS_SUBTITULO);
        lblEmpleado.setAlignmentX(CENTER_ALIGNMENT);

        pnlTitulo.add(Box.createVerticalGlue());
        pnlTitulo.add(crearLineaTitulo("Nuevo"));
        pnlTitulo.add(crearLineaTitulo("usuario"));
        pnlTitulo.add(Box.createRigidArea(new Dimension(0, 6)));
        pnlTitulo.add(lblEmpleado);
        pnlTitulo.add(Box.createVerticalGlue());
        return pnlTitulo;
    }

    // Crea una línea del título en amarillo, centrada
    private JLabel crearLineaTitulo(String texto) {
        JLabel lblLineaTitulo = new JLabel(texto, SwingConstants.CENTER);
        lblLineaTitulo.setFont(FUENTE_TITULO);
        lblLineaTitulo.setForeground(Estilos.AMARILLO);
        lblLineaTitulo.setAlignmentX(CENTER_ALIGNMENT);
        return lblLineaTitulo;
    }

    // Crea el formulario: usuario y nivel, contraseña y confirmación, error y botones
    private JPanel crearPanelFormulario() {
        JPanel pnlFormulario = new JPanel();
        pnlFormulario.setLayout(new BoxLayout(pnlFormulario, BoxLayout.Y_AXIS));
        pnlFormulario.setBackground(Estilos.FONDO_FORMULARIO);
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        pnlFormulario.add(Box.createVerticalGlue());

        txtUsuario = new JTextField();
        cmbNivel = new JComboBox<>(NIVELES);
        pnlFormulario.add(crearFila(
                crearCampo("USUARIO *", darEstiloCampo(txtUsuario)),
                crearCampo("NIVEL *", darEstiloComboNivel())));
        pnlFormulario.add(Box.createRigidArea(new Dimension(0, 12)));

        pwdContrasena = new JPasswordField();
        pwdConfirmarContrasena = new JPasswordField();
        pnlFormulario.add(crearFila(
                crearCampo("CONTRASEÑA *", darEstiloCampo(pwdContrasena)),
                crearCampo("CONFIRMAR CONTRASEÑA *", darEstiloCampo(pwdConfirmarContrasena))));
        pnlFormulario.add(Box.createRigidArea(new Dimension(0, 10)));

        lblError = new JLabel(SIN_ERROR);
        lblError.setFont(FUENTE_ERROR);
        lblError.setForeground(Estilos.ROJO_ERROR);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        pnlFormulario.add(lblError);
        pnlFormulario.add(Box.createRigidArea(new Dimension(0, 8)));

        pnlFormulario.add(crearPanelBotones());
        pnlFormulario.add(Box.createVerticalGlue());
        return pnlFormulario;
    }

    // Coloca dos campos lado a lado en una fila
    private JPanel crearFila(JPanel pnlCampoIzquierdo, JPanel pnlCampoDerecho) {
        JPanel pnlFila = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlFila.setOpaque(false);
        pnlFila.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_FILA_CAMPOS));
        pnlFila.add(pnlCampoIzquierdo);
        pnlFila.add(pnlCampoDerecho);
        return pnlFila;
    }

    // Crea un campo con su etiqueta gris arriba
    private JPanel crearCampo(String etiqueta, JComponent componente) {
        JPanel pnlCampo = new JPanel();
        pnlCampo.setLayout(new BoxLayout(pnlCampo, BoxLayout.Y_AXIS));
        pnlCampo.setOpaque(false);

        JLabel lblCampo = new JLabel(etiqueta);
        lblCampo.setFont(FUENTE_ETIQUETA);
        lblCampo.setForeground(Color.GRAY);
        lblCampo.setAlignmentX(LEFT_ALIGNMENT);

        pnlCampo.add(lblCampo);
        pnlCampo.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlCampo.add(componente);
        return pnlCampo;
    }

    // Aplica el estilo oscuro a un campo de texto o de contraseña y lo devuelve
    private JTextField darEstiloCampo(JTextField campo) {
        campo.setBackground(Estilos.FONDO_CAMPO_OSCURO);
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Estilos.AMARILLO);
        campo.setFont(FUENTE_CAMPO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE_CAMPO_OSCURO, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_CAMPO));
        return campo;
    }

    // Aplica el estilo oscuro a la lista de niveles y la devuelve
    private JComboBox<String> darEstiloComboNivel() {
        cmbNivel.setBackground(Estilos.FONDO_CAMPO_OSCURO);
        cmbNivel.setForeground(Color.WHITE);
        cmbNivel.setFont(FUENTE_CAMPO);
        cmbNivel.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_CAMPO));
        cmbNivel.setAlignmentX(LEFT_ALIGNMENT);
        return cmbNivel;
    }

    // Crea los botones Cancelar y Guardar
    private JPanel crearPanelBotones() {
        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_FILA_BOTONES));
        pnlBotones.add(crearBoton(COMANDO_CANCELAR, Estilos.FONDO_BOTON_CANCELAR, Estilos.TEXTO_BOTON_CANCELAR,
                FUENTE_BOTON_CANCELAR));
        pnlBotones.add(crearBoton(COMANDO_GUARDAR, Estilos.AMARILLO, Color.BLACK,
                FUENTE_BOTON_GUARDAR));
        return pnlBotones;
    }

    // Crea un botón plano; su texto también es su comando
    private JButton crearBoton(String texto, Color colorFondo, Color colorTexto, Font fuente) {
        JButton btnAccion = new JButton(texto);
        btnAccion.setBackground(colorFondo);
        btnAccion.setForeground(colorTexto);
        btnAccion.setFont(fuente);
        btnAccion.setBorderPainted(false);
        btnAccion.setFocusPainted(false);
        btnAccion.addActionListener(this);
        return btnAccion;
    }

    // Eventos

    // Cierra el diálogo con Cancelar o guarda el usuario con Guardar
    @Override
    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();

        if (comando.equals(COMANDO_CANCELAR)) {
            dispose();
        }
        if (comando.equals(COMANDO_GUARDAR)) {
            guardar();
        }
    }

    // Valida el formulario y, si es correcto, guarda el usuario y cierra el diálogo
    private void guardar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(pwdContrasena.getPassword());
        String confirmacion = new String(pwdConfirmarContrasena.getPassword());

        String mensajeError = validarFormulario(usuario, contrasena, confirmacion);
        if (mensajeError != null) {
            lblError.setText(mensajeError);
            return;
        }

        try {
            insertarUsuario(usuario, Cifrado.generarHash(contrasena), (String) cmbNivel.getSelectedItem());
            dispose();
        } catch (SQLException ex) {
            lblError.setText(esUsuarioDuplicado(ex)
                    ? "Ese nombre de usuario ya existe."
                    : "No se pudo guardar el usuario.");
        } catch (NoSuchAlgorithmException ex) {
            lblError.setText("No se pudo cifrar la contraseña.");
        }
    }

    // Acceso a datos

    // Guarda el usuario con la contraseña ya cifrada y lo liga al empleado
    private void insertarUsuario(String usuario, String contrasenaCifrada, String nivel)
            throws SQLException {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            throw new SQLException("Sin conexión con la base de datos");
        }

        // Registra el usuario nuevo del empleado con su nivel de acceso
        String consulta = "INSERT INTO usuario (username, password, nivel, id_empleado) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, usuario);
            sentencia.setString(2, contrasenaCifrada);
            sentencia.setString(3, nivel);
            sentencia.setInt(4, idEmpleado);
            sentencia.executeUpdate();
        }
    }

    // Métodos auxiliares

    // Revisa los campos del formulario. Devuelve el mensaje del primer error encontrado,
    // o null si todo es correcto.
    private String validarFormulario(String usuario, String contrasena, String confirmacion) {
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            return "Todos los campos son obligatorios.";
        }
        if (!contrasena.equals(confirmacion)) {
            return "Las contraseñas no coinciden.";
        }
        if (contrasena.length() < LONGITUD_MINIMA_CONTRASENA) {
            return "La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENA + " caracteres.";
        }
        return null;
    }

    // Indica si el error de la base de datos se debe a que el nombre de usuario ya existe
    // (SQL Server menciona la restricción UNIQUE en el mensaje)
    private boolean esUsuarioDuplicado(SQLException ex) {
        String mensajeBaseDatos = ex.getMessage();
        return mensajeBaseDatos != null
                && (mensajeBaseDatos.contains("UNIQUE") || mensajeBaseDatos.contains("unique"));
    }
}