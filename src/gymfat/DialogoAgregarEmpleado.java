// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - DialogoAgregarEmpleado
// Descripción: Diálogo para registrar un empleado con sus nombres, apellidos,
// datos de contacto y puesto. La fecha de contratación es la del día actual.
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

// Diálogo para registrar un empleado
public class DialogoAgregarEmpleado extends JDialog implements ActionListener {

    // Constantes

    // Comandos de los botones
    private static final String COMANDO_CANCELAR = "Cancelar";
    private static final String COMANDO_GUARDAR = "Guardar";

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
    private static final Font FUENTE_SUBTITULO = new Font(Estilos.FUENTE, Font.PLAIN, 11);
    private static final Font FUENTE_ETIQUETA = new Font(Estilos.FUENTE, Font.BOLD, 10);
    private static final Font FUENTE_CAMPO = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_ERROR = new Font(Estilos.FUENTE, Font.PLAIN, 11);
    private static final Font FUENTE_BOTON_CANCELAR = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_BOTON_GUARDAR = new Font(Estilos.FUENTE, Font.BOLD, 13);

    // Atributos y componentes de la interfaz

    private JTextField txtNombres;
    private JTextField txtApellidoPaterno;
    private JTextField txtApellidoMaterno;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtPuesto;

    // Muestra los errores de validación debajo de los campos
    private JLabel lblError;

    // Indica si el empleado se guardó; la lista lo consulta para saber si debe recargarse
    private boolean fueGuardado = false;

    // Constructor

    // Crea el diálogo modal, centrado en la pantalla, y lo muestra
    public DialogoAgregarEmpleado(Window ventanaPadre) {
        super(ventanaPadre, "Nuevo empleado", ModalityType.APPLICATION_MODAL);
        setSize(ANCHO_DIALOGO, ALTO_DIALOGO);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        construirInterfaz();
        setVisible(true);
    }

    // Devuelve true si el empleado se guardó antes de cerrar el diálogo
    public boolean isFueGuardado() {
        return fueGuardado;
    }

    // Construcción de la interfaz

    // Coloca el título a la izquierda y el formulario a la derecha
    private void construirInterfaz() {
        add(crearPanelTitulo(), BorderLayout.WEST);
        add(crearPanelFormulario(), BorderLayout.CENTER);
    }

    // Crea el panel negro con el título "Nuevo empleado" y el nombre del sistema
    private JPanel crearPanelTitulo() {
        JPanel pnlTitulo = new JPanel();
        pnlTitulo.setLayout(new BoxLayout(pnlTitulo, BoxLayout.Y_AXIS));
        pnlTitulo.setBackground(Color.BLACK);
        pnlTitulo.setPreferredSize(new Dimension(ANCHO_PANEL_IZQUIERDO, 0));

        JLabel lblSubtitulo = new JLabel("GymFat", SwingConstants.CENTER);
        lblSubtitulo.setFont(FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(Estilos.GRIS_SUBTITULO);
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

        pnlTitulo.add(Box.createVerticalGlue());
        pnlTitulo.add(crearLineaTitulo("Nuevo"));
        pnlTitulo.add(crearLineaTitulo("empleado"));
        pnlTitulo.add(Box.createRigidArea(new Dimension(0, 6)));
        pnlTitulo.add(lblSubtitulo);
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

    // Crea el formulario: dos filas de tres campos, mensaje de error y botones
    private JPanel crearPanelFormulario() {
        JPanel pnlFormulario = new JPanel();
        pnlFormulario.setLayout(new BoxLayout(pnlFormulario, BoxLayout.Y_AXIS));
        pnlFormulario.setBackground(Estilos.FONDO_FORMULARIO);
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        pnlFormulario.add(Box.createVerticalGlue());

        txtNombres = new JTextField();
        txtApellidoPaterno = new JTextField();
        txtApellidoMaterno = new JTextField();
        pnlFormulario.add(crearFila(
                crearCampo("NOMBRES *", txtNombres),
                crearCampo("APELLIDO P. *", txtApellidoPaterno),
                crearCampo("APELLIDO M.", txtApellidoMaterno)));
        pnlFormulario.add(Box.createRigidArea(new Dimension(0, 12)));

        txtCorreo = new JTextField();
        txtTelefono = new JTextField();
        txtPuesto = new JTextField();
        pnlFormulario.add(crearFila(
                crearCampo("CORREO", txtCorreo),
                crearCampo("TELÉFONO", txtTelefono),
                crearCampo("PUESTO *", txtPuesto)));
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

    // Coloca los campos recibidos lado a lado en una fila
    private JPanel crearFila(JPanel... campos) {
        JPanel pnlFila = new JPanel(new GridLayout(1, campos.length, 12, 0));
        pnlFila.setOpaque(false);
        pnlFila.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_FILA_CAMPOS));
        for (JPanel pnlCampo : campos) {
            pnlFila.add(pnlCampo);
        }
        return pnlFila;
    }

    // Crea un campo de texto oscuro con su etiqueta gris arriba
    private JPanel crearCampo(String etiqueta, JTextField campo) {
        JPanel pnlCampo = new JPanel();
        pnlCampo.setLayout(new BoxLayout(pnlCampo, BoxLayout.Y_AXIS));
        pnlCampo.setOpaque(false);

        JLabel lblCampo = new JLabel(etiqueta);
        lblCampo.setFont(FUENTE_ETIQUETA);
        lblCampo.setForeground(Color.GRAY);
        lblCampo.setAlignmentX(LEFT_ALIGNMENT);

        campo.setBackground(Estilos.FONDO_CAMPO_OSCURO);
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Estilos.AMARILLO);
        campo.setFont(FUENTE_CAMPO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE_CAMPO_OSCURO, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_CAMPO));

        pnlCampo.add(lblCampo);
        pnlCampo.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlCampo.add(campo);
        return pnlCampo;
    }

    // Crea los botones Cancelar y Guardar
    private JPanel crearPanelBotones() {
        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_FILA_BOTONES));
        pnlBotones.add(crearBoton(COMANDO_CANCELAR, Estilos.FONDO_BOTON_CANCELAR,
                Estilos.TEXTO_BOTON_CANCELAR, FUENTE_BOTON_CANCELAR));
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

    // Cierra el diálogo con Cancelar o guarda el empleado con Guardar
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

    // Valida los campos obligatorios y, si son correctos, guarda y cierra el diálogo
    private void guardar() {
        String nombres = txtNombres.getText().trim();
        String apellidoPaterno = txtApellidoPaterno.getText().trim();
        String puesto = txtPuesto.getText().trim();

        if (nombres.isEmpty() || apellidoPaterno.isEmpty() || puesto.isEmpty()) {
            lblError.setText("Llena los campos obligatorios (*).");
            return;
        }

        try {
            insertarEmpleado(nombres, apellidoPaterno, puesto);
            fueGuardado = true;
            dispose();
        } catch (SQLException ex) {
            lblError.setText("No se pudo guardar el empleado.");
        }
    }

    // Acceso a datos

    // Guarda el empleado; los campos opcionales vacíos se guardan como NULL
    private void insertarEmpleado(String nombres, String apellidoPaterno, String puesto)
            throws SQLException {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            throw new SQLException("Sin conexión con la base de datos");
        }

        // Registra un empleado nuevo con la fecha de hoy como fecha de contratación
        String consulta = "INSERT INTO empleado (nombres, apellido_paterno, apellido_materno, "
                + "correo_electronico, telefono, puesto, fecha_contratacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, CAST(GETDATE() AS DATE))";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, nombres);
            sentencia.setString(2, apellidoPaterno);
            sentencia.setString(3, textoONulo(txtApellidoMaterno));
            sentencia.setString(4, textoONulo(txtCorreo));
            sentencia.setString(5, textoONulo(txtTelefono));
            sentencia.setString(6, puesto);
            sentencia.executeUpdate();
        }
    }

    // Métodos auxiliares

    // Devuelve el texto del campo sin espacios, o null si quedó vacío,
    // para que los campos opcionales se guarden como NULL
    private String textoONulo(JTextField txtCampo) {
        String texto = txtCampo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }
}