// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - PanelRegistroMiembro
// Descripción: Formulario para registrar un miembro nuevo. Valida los datos,
// guarda el miembro con su contacto, membresía, salud, clases y días, copia
// su foto y genera el recibo de pago.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

// Pantalla con el formulario de registro de miembros
public class PanelRegistroMiembro extends JPanel implements ActionListener {

    // Constantes

    // Comando del botón para registrar
    private static final String COMANDO_REGISTRAR = "Registrar Miembro";

    // Clases y días que se pueden elegir. Se relacionan por posición con los IDs de
    // las tablas clase y dia, ordenados por ID.
    private static final String[] CLASES = {"Yoga", "CrossFit", "Spinning", "HIIT", "Zumba", "Pilates"};
    private static final String[] DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    // Horarios que se pueden elegir; se guardan con este mismo texto
    private static final String HORARIO_MANANA = "Mañana (6-12h)";
    private static final String HORARIO_TARDE = "Tarde (12-17h)";
    private static final String HORARIO_NOCHE = "Noche (17-22h)";

    // Opción del combo de entrenadores para no asignar entrenador, y su ID
    private static final String TEXTO_SIN_ENTRENADOR = "Sin entrenador";
    private static final int ID_SIN_ENTRENADOR = -1;

    // Valor del ID del miembro mientras no se obtiene el que genera la base de datos
    private static final int ID_NO_GENERADO = -1;

    // Duración que se usa si no se encuentra el plan elegido
    private static final int DURACION_PLAN_PREDETERMINADA = 30;

    // Longitud máxima de cada campo, igual que en la base de datos
    private static final int MAXIMO_NOMBRES = 100;
    private static final int MAXIMO_APELLIDO = 80;
    private static final int MAXIMO_CORREO = 150;
    private static final int MAXIMO_TELEFONO = 20;
    private static final int MAXIMO_CONTACTO = 150;
    private static final int MAXIMO_RELACION = 80;
    private static final int MAXIMO_OBJETIVO = 200;

    // Valor máximo de la estatura y del peso, por el tipo DECIMAL de la base de datos
    private static final double MAXIMO_MEDIDA = 999.99;

    // Texto del recuadro de la foto cuando no se ha elegido ninguna
    private static final String TEXTO_SIN_FOTO = "Sin foto";

    // Extensión con la que se guardan las fotos copiadas
    private static final String EXTENSION_FOTO = ".jpg";

    // Líneas que separan las partes del recibo
    private static final String LINEA_DOBLE = "========================================";
    private static final String LINEA_SIMPLE = "----------------------------------------";

    // Texto del recibo cuando el miembro no dio correo o teléfono
    private static final String TEXTO_NO_REGISTRADO = "No registrado";

    // Tamaños, en píxeles
    private static final int ANCHO_FOTO = 70;
    private static final int ALTO_FOTO = 80;
    private static final int ALTO_CAMPO = 30;
    private static final int ALTO_FILA_CAMPOS = 54;
    private static final int ANCHO_COMBO_ENTRENADOR = 220;
    private static final int ANCHO_COMBO_MEMBRESIA = 320;
    private static final int ANCHO_BOTON_REGISTRAR = 230;
    private static final int ALTO_BOTON_REGISTRAR = 44;
    private static final int INCREMENTO_SCROLL = 16;
    private static final int ESPACIO_ENTRE_SECCIONES = 10;

    // Colores propios de esta pantalla
    private static final Color NEGRO_SUAVE = new Color(20, 20, 20);
    private static final Color BORDE_SECCION = new Color(235, 235, 235);
    private static final Color GRIS_SEPARADOR = new Color(240, 240, 240);
    private static final Color GRIS_TITULO_SECCION = new Color(50, 50, 50);
    private static final Color GRIS_ETIQUETA = new Color(60, 60, 60);
    private static final Color GRIS_ETIQUETA_CAMPO = new Color(80, 80, 80);
    private static final Color GRIS_TERMINOS = new Color(90, 90, 90);
    private static final Color GRIS_INDICACION = new Color(100, 100, 100);
    private static final Color GRIS_BORDE_CLARO = new Color(210, 210, 210);

    // Fuentes de esta pantalla
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 24);
    private static final Font FUENTE_TITULO_SECCION = new Font(Estilos.FUENTE, Font.BOLD, 13);
    private static final Font FUENTE_TEXTO = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_TEXTO_NEGRITA = new Font(Estilos.FUENTE, Font.BOLD, 13);
    private static final Font FUENTE_CHICA = new Font(Estilos.FUENTE, Font.PLAIN, 12);
    private static final Font FUENTE_QUITAR_FOTO = new Font(Estilos.FUENTE, Font.PLAIN, 11);
    private static final Font FUENTE_SIN_FOTO = new Font(Estilos.FUENTE, Font.ITALIC, 10);
    private static final Font FUENTE_BOTON_REGISTRAR = new Font(Estilos.FUENTE, Font.BOLD, 14);

    // Atributos y componentes de la interfaz

    // Datos personales y contacto de emergencia
    private JTextField txtNombres;
    private JTextField txtApellidoPaterno;
    private JTextField txtApellidoMaterno;
    private JTextField txtFechaNacimiento;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtContactoEmergencia;
    private JTextField txtTelefonoEmergencia;
    private JTextField txtRelacion;

    // Foto elegida por el usuario y su vista previa
    private File fotoSeleccionada = null;
    private JLabel lblVistaPreviaFoto;

    // Entrenador, objetivo, clases, días y horario
    private JRadioButton rbSiEntrenador;
    private JRadioButton rbNoEntrenador;
    private JComboBox<String> cmbEntrenador;
    private JTextField txtObjetivo;
    private JCheckBox[] chkClases;
    private JCheckBox[] chkDias;
    private JRadioButton rbManana;
    private JRadioButton rbTarde;
    private JRadioButton rbNoche;

    // Plan de membresía
    private JComboBox<String> cmbMembresia;

    // Salud
    private JRadioButton rbSiCondicion;
    private JRadioButton rbNoCondicion;
    private JTextArea txaCondicion;
    private JRadioButton rbSiLesion;
    private JRadioButton rbNoLesion;
    private JTextArea txaLesion;
    private JTextField txtEstatura;
    private JTextField txtPeso;

    // Términos y condiciones
    private JCheckBox chkTerminos;

    // IDs de la base de datos, en el mismo orden que las opciones de la interfaz
    private final List<Integer> idsPlanes = new ArrayList<>();
    private final List<Integer> idsEntrenadores = new ArrayList<>();
    private final List<Integer> idsClases = new ArrayList<>();
    private final List<Integer> idsDias = new ArrayList<>();

    // Constructor

    // Crea el formulario y carga los planes, entrenadores, clases y días
    public PanelRegistroMiembro() {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO_LISTA);
        construirInterfaz();
        cargarPlanes();
        cargarEntrenadores();
        cargarClasesYDias();
    }

    // Construcción de la interfaz

    // Coloca el título arriba y las cinco secciones del formulario en un panel con scroll
    private void construirInterfaz() {
        JLabel lblTitulo = new JLabel("Registro de nuevo miembro");
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(NEGRO_SUAVE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(24, 40, 10, 40));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlFormulario = new JPanel();
        pnlFormulario.setLayout(new BoxLayout(pnlFormulario, BoxLayout.Y_AXIS));
        pnlFormulario.setBackground(Estilos.FONDO_LISTA);
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));

        pnlFormulario.add(crearSeccion("1. Datos Personales", crearPanelDatosPersonales()));
        pnlFormulario.add(crearEspacio(ESPACIO_ENTRE_SECCIONES));
        pnlFormulario.add(crearSeccion("2. Entrenador y Clases", crearPanelEntrenador()));
        pnlFormulario.add(crearEspacio(ESPACIO_ENTRE_SECCIONES));
        pnlFormulario.add(crearSeccion("3. Plan de Membresía", crearPanelMembresia()));
        pnlFormulario.add(crearEspacio(ESPACIO_ENTRE_SECCIONES));
        pnlFormulario.add(crearSeccion("4. Salud (Confidencial)", crearPanelSalud()));
        pnlFormulario.add(crearEspacio(ESPACIO_ENTRE_SECCIONES));
        pnlFormulario.add(crearSeccion("5. Términos y Consentimiento", crearPanelTerminos()));
        pnlFormulario.add(crearEspacio(ESPACIO_ENTRE_SECCIONES));
        pnlFormulario.add(crearPanelBotonRegistrar());

        JScrollPane scrFormulario = new JScrollPane(pnlFormulario);
        scrFormulario.setBorder(null);
        scrFormulario.getVerticalScrollBar().setUnitIncrement(INCREMENTO_SCROLL);
        add(scrFormulario, BorderLayout.CENTER);
    }

    // Envuelve el contenido de una sección en un recuadro blanco con franja amarilla y título
    private JPanel crearSeccion(String titulo, JPanel pnlContenido) {
        JLabel lblTituloSeccion = new JLabel("  " + titulo);
        lblTituloSeccion.setFont(FUENTE_TITULO_SECCION);
        lblTituloSeccion.setForeground(GRIS_TITULO_SECCION);
        lblTituloSeccion.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 10));
        lblTituloSeccion.setHorizontalAlignment(SwingConstants.LEFT);

        JSeparator separador = new JSeparator();
        separador.setForeground(GRIS_SEPARADOR);

        JPanel pnlEncabezado = new JPanel(new BorderLayout());
        pnlEncabezado.setBackground(Color.WHITE);
        pnlEncabezado.add(lblTituloSeccion, BorderLayout.CENTER);
        pnlEncabezado.add(separador, BorderLayout.SOUTH);

        pnlContenido.setBackground(Color.WHITE);
        pnlContenido.setBorder(BorderFactory.createEmptyBorder(12, 20, 16, 20));

        JPanel pnlSeccion = new JPanel(new BorderLayout());
        pnlSeccion.setBackground(Color.WHITE);
        pnlSeccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        pnlSeccion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, Estilos.AMARILLO),
                BorderFactory.createLineBorder(BORDE_SECCION, 1)));
        pnlSeccion.add(pnlEncabezado, BorderLayout.NORTH);
        pnlSeccion.add(pnlContenido, BorderLayout.CENTER);
        return pnlSeccion;
    }

    // Sección 1: fecha de registro, datos personales, contacto de emergencia y foto
    private JPanel crearPanelDatosPersonales() {
        JPanel pnlDatos = crearPanelVertical();

        JPanel pnlFecha = crearFilaFlujo();
        pnlFecha.add(crearEtiqueta("Fecha de registro: "));
        JLabel lblFechaRegistro = new JLabel(LocalDate.now().toString());
        lblFechaRegistro.setFont(FUENTE_TEXTO_NEGRITA);
        pnlFecha.add(lblFechaRegistro);
        pnlDatos.add(pnlFecha);
        pnlDatos.add(crearEspacio(8));

        txtNombres = crearCampo();
        txtApellidoPaterno = crearCampo();
        txtApellidoMaterno = crearCampo();
        txtFechaNacimiento = crearCampo();
        txtCorreo = crearCampo();
        txtTelefono = crearCampo();
        txtContactoEmergencia = crearCampo();
        txtTelefonoEmergencia = crearCampo();
        txtRelacion = crearCampo();

        pnlDatos.add(crearFilaCampos(new String[]{"Nombres *"},
                new JTextField[]{txtNombres}));
        pnlDatos.add(crearEspacio(8));
        pnlDatos.add(crearFilaCampos(new String[]{"Apellido paterno *", "Apellido materno"},
                new JTextField[]{txtApellidoPaterno, txtApellidoMaterno}));
        pnlDatos.add(crearEspacio(8));
        pnlDatos.add(crearFilaCampos(new String[]{"Fecha nacimiento (YYYY-MM-DD)", "Correo electrónico"},
                new JTextField[]{txtFechaNacimiento, txtCorreo}));
        pnlDatos.add(crearEspacio(8));
        pnlDatos.add(crearFilaCampos(new String[]{"Teléfono"},
                new JTextField[]{txtTelefono}));
        pnlDatos.add(crearEspacio(8));
        pnlDatos.add(crearFilaCampos(new String[]{"Contacto de emergencia *"},
                new JTextField[]{txtContactoEmergencia}));
        pnlDatos.add(crearEspacio(8));
        pnlDatos.add(crearFilaCampos(new String[]{"Tel. emergencia *", "Relación"},
                new JTextField[]{txtTelefonoEmergencia, txtRelacion}));
        pnlDatos.add(crearEspacio(12));

        pnlDatos.add(crearFilaFoto());
        return pnlDatos;
    }

    // Crea la fila de la foto: vista previa y botones para seleccionar o quitar la foto
    private JPanel crearFilaFoto() {
        JPanel pnlFoto = crearFilaFlujo();
        pnlFoto.add(crearEtiqueta("Foto del miembro:"));

        lblVistaPreviaFoto = new JLabel(TEXTO_SIN_FOTO, SwingConstants.CENTER);
        lblVistaPreviaFoto.setPreferredSize(new Dimension(ANCHO_FOTO, ALTO_FOTO));
        lblVistaPreviaFoto.setFont(FUENTE_SIN_FOTO);
        lblVistaPreviaFoto.setForeground(Color.GRAY);
        lblVistaPreviaFoto.setBorder(BorderFactory.createLineBorder(GRIS_BORDE_CLARO));
        pnlFoto.add(lblVistaPreviaFoto);

        JButton btnSeleccionarFoto = new JButton("Seleccionar...");
        btnSeleccionarFoto.setFont(FUENTE_CHICA);
        btnSeleccionarFoto.setBackground(Estilos.FONDO_BOTON_SECUNDARIO);
        btnSeleccionarFoto.setFocusPainted(false);
        btnSeleccionarFoto.addActionListener(evento -> seleccionarFoto());

        JButton btnQuitarFoto = new JButton("Quitar");
        btnQuitarFoto.setFont(FUENTE_QUITAR_FOTO);
        btnQuitarFoto.setForeground(Color.RED);
        btnQuitarFoto.setBorderPainted(false);
        btnQuitarFoto.setContentAreaFilled(false);
        btnQuitarFoto.setFocusPainted(false);
        btnQuitarFoto.addActionListener(evento -> quitarFoto());

        pnlFoto.add(btnSeleccionarFoto);
        pnlFoto.add(btnQuitarFoto);
        return pnlFoto;
    }

    // Sección 2: entrenador, objetivo, clases de interés, días disponibles y horario
    private JPanel crearPanelEntrenador() {
        JPanel pnlEntrenador = crearPanelVertical();

        JPanel pnlQuiereEntrenador = crearFilaFlujo();
        rbSiEntrenador = new JRadioButton("Sí, deseo entrenador personal");
        rbNoEntrenador = new JRadioButton("No por el momento");
        rbNoEntrenador.setSelected(true);
        agruparOpciones(pnlQuiereEntrenador, rbSiEntrenador, rbNoEntrenador);
        pnlEntrenador.add(pnlQuiereEntrenador);
        pnlEntrenador.add(crearEspacio(8));

        JPanel pnlComboEntrenador = crearFilaFlujo();
        pnlComboEntrenador.add(crearEtiqueta("Entrenador:"));
        cmbEntrenador = new JComboBox<>();
        cmbEntrenador.setPreferredSize(new Dimension(ANCHO_COMBO_ENTRENADOR, ALTO_CAMPO));
        pnlComboEntrenador.add(cmbEntrenador);
        pnlEntrenador.add(pnlComboEntrenador);
        pnlEntrenador.add(crearEspacio(8));

        txtObjetivo = crearCampo();
        pnlEntrenador.add(crearFilaCampos(new String[]{"Objetivo principal"},
                new JTextField[]{txtObjetivo}));
        pnlEntrenador.add(crearEspacio(10));

        chkClases = crearCasillas(CLASES);
        pnlEntrenador.add(crearFilaCasillas("Clases de interés:  ", chkClases));
        pnlEntrenador.add(crearEspacio(6));

        chkDias = crearCasillas(DIAS);
        pnlEntrenador.add(crearFilaCasillas("Días disponibles:  ", chkDias));
        pnlEntrenador.add(crearEspacio(6));

        JPanel pnlHorario = crearFilaFlujo();
        pnlHorario.add(crearEtiqueta("Horario preferido:  "));
        rbManana = new JRadioButton(HORARIO_MANANA);
        rbTarde = new JRadioButton(HORARIO_TARDE);
        rbNoche = new JRadioButton(HORARIO_NOCHE);
        agruparOpciones(pnlHorario, rbManana, rbTarde, rbNoche);
        pnlEntrenador.add(pnlHorario);

        return pnlEntrenador;
    }

    // Sección 3: plan de membresía
    private JPanel crearPanelMembresia() {
        JPanel pnlMembresia = crearPanelVertical();
        JPanel pnlPlan = crearFilaFlujo();
        pnlPlan.add(crearEtiqueta("Plan de membresía *:  "));
        cmbMembresia = new JComboBox<>();
        cmbMembresia.setPreferredSize(new Dimension(ANCHO_COMBO_MEMBRESIA, ALTO_CAMPO));
        pnlPlan.add(cmbMembresia);
        pnlMembresia.add(pnlPlan);
        return pnlMembresia;
    }

    // Sección 4: condición médica, lesiones, estatura y peso
    private JPanel crearPanelSalud() {
        JPanel pnlSalud = crearPanelVertical();

        JPanel pnlCondicion = crearFilaFlujo();
        pnlCondicion.add(crearEtiqueta("¿Padece alguna condición médica?  "));
        rbSiCondicion = new JRadioButton("Sí");
        rbNoCondicion = new JRadioButton("No");
        rbNoCondicion.setSelected(true);
        agruparOpciones(pnlCondicion, rbSiCondicion, rbNoCondicion);
        pnlSalud.add(pnlCondicion);
        pnlSalud.add(crearEspacio(6));

        txaCondicion = new JTextArea(2, 0);
        pnlSalud.add(crearAreaDescripcion("En caso afirmativo, especifique:", txaCondicion));
        pnlSalud.add(crearEspacio(10));

        JPanel pnlLesion = crearFilaFlujo();
        pnlLesion.add(crearEtiqueta("¿Ha tenido lesiones recientes?  "));
        rbSiLesion = new JRadioButton("Sí");
        rbNoLesion = new JRadioButton("No");
        rbNoLesion.setSelected(true);
        agruparOpciones(pnlLesion, rbSiLesion, rbNoLesion);
        pnlSalud.add(pnlLesion);
        pnlSalud.add(crearEspacio(6));

        txaLesion = new JTextArea(2, 0);
        pnlSalud.add(crearAreaDescripcion("En caso afirmativo, describa:", txaLesion));
        pnlSalud.add(crearEspacio(10));

        txtEstatura = crearCampo();
        txtPeso = crearCampo();
        pnlSalud.add(crearFilaCampos(new String[]{"Estatura (cm)", "Peso (kg)"},
                new JTextField[]{txtEstatura, txtPeso}));

        return pnlSalud;
    }

    // Sección 5: texto de los términos y casilla para aceptarlos
    private JPanel crearPanelTerminos() {
        JPanel pnlTerminos = crearPanelVertical();

        JTextArea txaTerminos = new JTextArea(
                "He leído y acepto el reglamento interno de GymFat, las políticas de uso de "
                + "instalaciones y las condiciones de mi plan de membresía. Declaro que la "
                + "información proporcionada es verdadera y completa.");
        txaTerminos.setEditable(false);
        txaTerminos.setLineWrap(true);
        txaTerminos.setWrapStyleWord(true);
        txaTerminos.setOpaque(false);
        txaTerminos.setFont(FUENTE_CHICA);
        txaTerminos.setForeground(GRIS_TERMINOS);
        txaTerminos.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTerminos.add(txaTerminos);
        pnlTerminos.add(crearEspacio(10));

        JPanel pnlAceptar = crearFilaFlujo();
        chkTerminos = new JCheckBox("Acepto los términos y condiciones");
        chkTerminos.setFont(FUENTE_TEXTO_NEGRITA);
        chkTerminos.setOpaque(false);
        pnlAceptar.add(chkTerminos);
        pnlTerminos.add(pnlAceptar);
        return pnlTerminos;
    }

    // Crea el botón Registrar Miembro alineado a la derecha
    private JPanel crearPanelBotonRegistrar() {
        JButton btnRegistrar = new JButton(COMANDO_REGISTRAR);
        btnRegistrar.setBackground(NEGRO_SUAVE);
        btnRegistrar.setForeground(Estilos.AMARILLO);
        btnRegistrar.setFont(FUENTE_BOTON_REGISTRAR);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setPreferredSize(new Dimension(ANCHO_BOTON_REGISTRAR, ALTO_BOTON_REGISTRAR));
        btnRegistrar.addActionListener(this);

        JPanel pnlBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBoton.setOpaque(false);
        pnlBoton.add(btnRegistrar);
        return pnlBoton;
    }

    // Crea una fila con uno o más campos lado a lado, cada uno con su etiqueta arriba
    private JPanel crearFilaCampos(String[] etiquetas, JTextField[] campos) {
        JPanel pnlFila = new JPanel(new GridLayout(1, etiquetas.length, 12, 0));
        pnlFila.setOpaque(false);
        pnlFila.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFila.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_FILA_CAMPOS));

        for (int i = 0; i < etiquetas.length; i++) {
            JLabel lblCampo = new JLabel(etiquetas[i]);
            lblCampo.setFont(FUENTE_CHICA);
            lblCampo.setForeground(GRIS_ETIQUETA_CAMPO);
            lblCampo.setAlignmentX(Component.LEFT_ALIGNMENT);
            campos[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            campos[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_CAMPO));

            JPanel pnlColumna = new JPanel();
            pnlColumna.setLayout(new BoxLayout(pnlColumna, BoxLayout.Y_AXIS));
            pnlColumna.setOpaque(false);
            pnlColumna.add(lblCampo);
            pnlColumna.add(Box.createRigidArea(new Dimension(0, 3)));
            pnlColumna.add(campos[i]);
            pnlFila.add(pnlColumna);
        }
        return pnlFila;
    }

    // Crea una fila con una etiqueta seguida de casillas de verificación
    private JPanel crearFilaCasillas(String etiqueta, JCheckBox[] casillas) {
        JPanel pnlFila = crearFilaFlujo();
        pnlFila.add(crearEtiqueta(etiqueta));
        for (JCheckBox chkOpcion : casillas) {
            pnlFila.add(chkOpcion);
        }
        return pnlFila;
    }

    // Crea una casilla transparente por cada texto
    private JCheckBox[] crearCasillas(String[] textos) {
        JCheckBox[] casillas = new JCheckBox[textos.length];
        for (int i = 0; i < textos.length; i++) {
            casillas[i] = new JCheckBox(textos[i]);
            casillas[i].setOpaque(false);
        }
        return casillas;
    }

    // Agrupa los botones de opción para que solo se pueda elegir uno y los agrega a la fila
    private void agruparOpciones(JPanel pnlFila, JRadioButton... opciones) {
        ButtonGroup grpOpciones = new ButtonGroup();
        for (JRadioButton rbOpcion : opciones) {
            rbOpcion.setOpaque(false);
            grpOpciones.add(rbOpcion);
            pnlFila.add(rbOpcion);
        }
    }

    // Crea un área de texto de dos líneas con una indicación arriba
    private JPanel crearAreaDescripcion(String indicacion, JTextArea txaDescripcion) {
        JLabel lblIndicacion = new JLabel(indicacion);
        lblIndicacion.setFont(FUENTE_CHICA);
        lblIndicacion.setForeground(GRIS_INDICACION);

        txaDescripcion.setFont(FUENTE_TEXTO);
        txaDescripcion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE_CLARO),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        txaDescripcion.setLineWrap(true);

        JPanel pnlDescripcion = new JPanel(new BorderLayout());
        pnlDescripcion.setOpaque(false);
        pnlDescripcion.add(lblIndicacion, BorderLayout.NORTH);
        pnlDescripcion.add(txaDescripcion, BorderLayout.CENTER);
        pnlDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pnlDescripcion;
    }

    // Crea un panel transparente que acomoda sus componentes en columna
    private JPanel crearPanelVertical() {
        JPanel pnlVertical = new JPanel();
        pnlVertical.setLayout(new BoxLayout(pnlVertical, BoxLayout.Y_AXIS));
        return pnlVertical;
    }

    // Crea una fila transparente que acomoda sus componentes de izquierda a derecha
    private JPanel crearFilaFlujo() {
        JPanel pnlFila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pnlFila.setOpaque(false);
        pnlFila.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pnlFila;
    }

    // Crea una etiqueta de texto normal en gris oscuro
    private JLabel crearEtiqueta(String texto) {
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(FUENTE_TEXTO);
        lblTexto.setForeground(GRIS_ETIQUETA);
        return lblTexto;
    }

    // Crea un campo de texto con borde gris claro
    private JTextField crearCampo() {
        JTextField txtCampo = new JTextField();
        txtCampo.setFont(FUENTE_TEXTO);
        txtCampo.setPreferredSize(new Dimension(txtCampo.getPreferredSize().width, ALTO_CAMPO));
        txtCampo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE_CLARO, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return txtCampo;
    }

    // Crea un espacio vertical vacío de la altura indicada
    private Component crearEspacio(int alto) {
        return Box.createRigidArea(new Dimension(0, alto));
    }

    // Eventos

    // Se ejecuta al presionar el botón Registrar Miembro
    @Override
    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();
        if (comando.equals(COMANDO_REGISTRAR)) {
            registrarMiembro();
        }
    }

    // Abre el selector de archivos y muestra la imagen elegida en la vista previa
    private void seleccionarFoto() {
        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setDialogTitle("Seleccionar foto del miembro");
        selectorArchivo.setFileFilter(new FileNameExtensionFilter(
                "Imágenes (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        selectorArchivo.setAcceptAllFileFilterUsed(false);

        if (selectorArchivo.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fotoSeleccionada = selectorArchivo.getSelectedFile();
            ImageIcon imagenOriginal = new ImageIcon(fotoSeleccionada.getAbsolutePath());
            Image imagenEscalada = imagenOriginal.getImage()
                    .getScaledInstance(ANCHO_FOTO, ALTO_FOTO, Image.SCALE_SMOOTH);
            lblVistaPreviaFoto.setIcon(new ImageIcon(imagenEscalada));
            lblVistaPreviaFoto.setText("");
        }
    }

    // Descarta la foto elegida y regresa la vista previa a "Sin foto"
    private void quitarFoto() {
        fotoSeleccionada = null;
        lblVistaPreviaFoto.setIcon(null);
        lblVistaPreviaFoto.setText(TEXTO_SIN_FOTO);
    }

    // Valida el formulario y guarda el miembro con todos sus datos relacionados.
    // Al terminar genera el recibo y limpia el formulario.
    private void registrarMiembro() {
        if (!validarFormulario()) {
            return;
        }

        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        try {
            int idMiembro = insertarMiembro(conexion);

            if (fotoSeleccionada != null && idMiembro != ID_NO_GENERADO) {
                copiarFoto(idMiembro);
                actualizarRutaFoto(conexion, idMiembro);
            }

            insertarContactoEmergencia(conexion, idMiembro);

            int idPlan = idsPlanes.get(cmbMembresia.getSelectedIndex());
            DatosPlan plan = consultarPlan(conexion, idPlan);
            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaVencimiento = fechaInicio.plusDays(plan.duracionDias);

            insertarMembresia(conexion, idMiembro, idPlan, fechaInicio, fechaVencimiento);
            insertarSalud(conexion, idMiembro);
            insertarClasesYDias(conexion, idMiembro);
            generarRecibo(idMiembro, plan, fechaInicio, fechaVencimiento);

            JOptionPane.showMessageDialog(this, "Miembro registrado correctamente. ID: " + idMiembro,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el miembro.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Revisa los campos en orden y muestra el primer error encontrado.
    // Devuelve true si todo es correcto.
    private boolean validarFormulario() {
        String nombres = obtenerTexto(txtNombres);
        String apellidoPaterno = obtenerTexto(txtApellidoPaterno);
        String contacto = obtenerTexto(txtContactoEmergencia);
        String telefonoEmergencia = obtenerTexto(txtTelefonoEmergencia);

        if (nombres.isEmpty() || apellidoPaterno.isEmpty() || contacto.isEmpty()
                || telefonoEmergencia.isEmpty()) {
            mostrarAdvertencia("Llena todos los campos obligatorios (*).", "Campos incompletos");
            return false;
        }

        boolean tieneLongitudCorrecta = validarLongitud(nombres, MAXIMO_NOMBRES, "Nombres")
                && validarLongitud(apellidoPaterno, MAXIMO_APELLIDO, "Apellido paterno")
                && validarLongitud(obtenerTexto(txtApellidoMaterno), MAXIMO_APELLIDO, "Apellido materno")
                && validarLongitud(obtenerTexto(txtCorreo), MAXIMO_CORREO, "Correo")
                && validarLongitud(obtenerTexto(txtTelefono), MAXIMO_TELEFONO, "Teléfono")
                && validarLongitud(contacto, MAXIMO_CONTACTO, "Contacto emergencia")
                && validarLongitud(telefonoEmergencia, MAXIMO_TELEFONO, "Tel. emergencia")
                && validarLongitud(obtenerTexto(txtRelacion), MAXIMO_RELACION, "Relación")
                && validarLongitud(obtenerTexto(txtObjetivo), MAXIMO_OBJETIVO, "Objetivo");
        if (!tieneLongitudCorrecta) {
            return false;
        }

        if (!validarFecha(obtenerTexto(txtFechaNacimiento))) {
            return false;
        }

        boolean tieneMedidasCorrectas = validarMedida(obtenerTexto(txtEstatura),
                "Estatura inválida (1-999.99).", "Estatura debe ser un número.")
                && validarMedida(obtenerTexto(txtPeso),
                        "Peso inválido (1-999.99).", "Peso debe ser un número.");
        if (!tieneMedidasCorrectas) {
            return false;
        }

        if (!chkTerminos.isSelected()) {
            mostrarAdvertencia("Debes aceptar los términos y condiciones.", "Términos no aceptados");
            return false;
        }
        return true;
    }

    // Acceso a datos

    // Llena el combo de planes, del de menor al de mayor duración
    private void cargarPlanes() {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        // Obtiene el nombre y precio de cada plan
        String consulta = "SELECT id_plan, nombre_plan, precio_plan FROM planes ORDER BY duracion_dias";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            cmbMembresia.removeAllItems();
            idsPlanes.clear();
            while (resultado.next()) {
                idsPlanes.add(resultado.getInt("id_plan"));
                cmbMembresia.addItem(resultado.getString("nombre_plan") + " — $"
                        + resultado.getString("precio_plan"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los planes.");
        }
    }

    // Llena el combo de entrenadores; la primera opción es "Sin entrenador"
    private void cargarEntrenadores() {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        // Obtiene los entrenadores ordenados por nombre
        String consulta = "SELECT id_entrenador, nombre FROM entrenador ORDER BY nombre";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            cmbEntrenador.removeAllItems();
            idsEntrenadores.clear();
            cmbEntrenador.addItem(TEXTO_SIN_ENTRENADOR);
            idsEntrenadores.add(ID_SIN_ENTRENADOR);
            while (resultado.next()) {
                idsEntrenadores.add(resultado.getInt("id_entrenador"));
                cmbEntrenador.addItem(resultado.getString("nombre"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los entrenadores.");
        }
    }

    // Carga los IDs de las clases y de los días, en el orden de las casillas
    private void cargarClasesYDias() {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        try {
            // Obtiene los IDs de las clases y de los días, ordenados por ID
            consultarIds(conexion, "SELECT id_clase FROM clase ORDER BY id_clase", "id_clase", idsClases);
            consultarIds(conexion, "SELECT id_dia FROM dia ORDER BY id_dia", "id_dia", idsDias);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las clases y los días.");
        }
    }

    // Ejecuta la consulta y guarda en la lista los valores de la columna indicada
    private void consultarIds(Connection conexion, String consulta, String columna, List<Integer> ids)
            throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            ids.clear();
            while (resultado.next()) {
                ids.add(resultado.getInt(columna));
            }
        }
    }

    // Guarda los datos personales del miembro y devuelve el ID que generó la base de datos
    private int insertarMiembro(Connection conexion) throws SQLException {
        int idEntrenador = idsEntrenadores.get(cmbEntrenador.getSelectedIndex());
        String fechaNacimiento = obtenerTexto(txtFechaNacimiento);

        // Registra el miembro; si eligió foto, la ruta queda pendiente hasta conocer su ID
        String consulta = "INSERT INTO miembro (nombres, apellido_paterno, apellido_materno, "
                + "fecha_nacimiento, correo_electronico, telefono, quiere_entrenador, "
                + "objetivo_principal, horario_deseado, estatura, peso, id_entrenador, foto_ruta) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta,
                Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, obtenerTexto(txtNombres));
            sentencia.setString(2, obtenerTexto(txtApellidoPaterno));
            sentencia.setString(3, textoONulo(txtApellidoMaterno));
            sentencia.setString(4, fechaNacimiento.isEmpty() ? null : fechaNacimiento);
            sentencia.setString(5, textoONulo(txtCorreo));
            sentencia.setString(6, textoONulo(txtTelefono));
            sentencia.setBoolean(7, rbSiEntrenador.isSelected());
            sentencia.setString(8, textoONulo(txtObjetivo));
            sentencia.setString(9, obtenerHorario());
            sentencia.setObject(10, convertirMedida(obtenerTexto(txtEstatura)));
            sentencia.setObject(11, convertirMedida(obtenerTexto(txtPeso)));
            if (idEntrenador == ID_SIN_ENTRENADOR) {
                sentencia.setNull(12, Types.INTEGER);
            } else {
                sentencia.setInt(12, idEntrenador);
            }
            sentencia.setString(13, fotoSeleccionada != null ? "PENDING" : null);
            sentencia.executeUpdate();

            try (ResultSet clavesGeneradas = sentencia.getGeneratedKeys()) {
                return clavesGeneradas.next() ? clavesGeneradas.getInt(1) : ID_NO_GENERADO;
            }
        }
    }

    // Guarda la ruta definitiva de la foto del miembro
    private void actualizarRutaFoto(Connection conexion, int idMiembro) throws SQLException {
        // Reemplaza la ruta pendiente por la ruta donde se copió la foto
        String consulta = "UPDATE miembro SET foto_ruta = ? WHERE id_miembro = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, Rutas.RUTA_FOTOS + idMiembro + EXTENSION_FOTO);
            sentencia.setInt(2, idMiembro);
            sentencia.executeUpdate();
        }
    }

    // Guarda el contacto de emergencia del miembro
    private void insertarContactoEmergencia(Connection conexion, int idMiembro) throws SQLException {
        // Registra el nombre, teléfono y relación del contacto de emergencia
        String consulta = "INSERT INTO contacto_emergencia (id_miembro, nombre, telefono, relacion) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            sentencia.setString(2, obtenerTexto(txtContactoEmergencia));
            sentencia.setString(3, obtenerTexto(txtTelefonoEmergencia));
            sentencia.setString(4, textoONulo(txtRelacion));
            sentencia.executeUpdate();
        }
    }

    // Obtiene la duración, el precio y el nombre del plan elegido.
    // Si no lo encuentra, devuelve los valores predeterminados.
    private DatosPlan consultarPlan(Connection conexion, int idPlan) throws SQLException {
        // Obtiene los datos del plan para calcular el vencimiento y llenar el recibo
        String consulta = "SELECT duracion_dias, precio_plan, nombre_plan FROM planes WHERE id_plan = ?";

        DatosPlan plan = new DatosPlan();
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idPlan);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    plan.duracionDias = resultado.getInt("duracion_dias");
                    plan.precio = resultado.getDouble("precio_plan");
                    plan.nombre = resultado.getString("nombre_plan");
                }
            }
        }
        return plan;
    }

    // Guarda la membresía del miembro como activa desde hoy
    private void insertarMembresia(Connection conexion, int idMiembro, int idPlan,
            LocalDate fechaInicio, LocalDate fechaVencimiento) throws SQLException {
        // Registra la membresía con su plan y fechas, en estado activa
        String consulta = "INSERT INTO membresia (id_miembro, id_plan, fecha_inicio, fecha_vencimiento, estado) "
                + "VALUES (?, ?, ?, ?, 'Activa')";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            sentencia.setInt(2, idPlan);
            sentencia.setDate(3, Date.valueOf(fechaInicio));
            sentencia.setDate(4, Date.valueOf(fechaVencimiento));
            sentencia.executeUpdate();
        }
    }

    // Guarda la información de salud del miembro
    private void insertarSalud(Connection conexion, int idMiembro) throws SQLException {
        boolean tieneCondicionMedica = rbSiCondicion.isSelected();

        // Registra si tiene condición médica (con su descripción) y si tiene lesiones
        String consulta = "INSERT INTO salud (id_miembro, tiene_condicion_medica, descripcion_condicion, "
                + "tiene_lesiones) VALUES (?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            sentencia.setBoolean(2, tieneCondicionMedica);
            sentencia.setString(3, tieneCondicionMedica ? txaCondicion.getText().trim() : null);
            sentencia.setBoolean(4, rbSiLesion.isSelected());
            sentencia.executeUpdate();
        }
    }

    // Guarda las clases de interés y los días disponibles que se marcaron
    private void insertarClasesYDias(Connection conexion, int idMiembro) throws SQLException {
        // Relaciona al miembro con cada clase marcada
        insertarSeleccionados(conexion, idMiembro, chkClases, idsClases,
                "INSERT INTO miembro_clase (id_miembro, id_clase) VALUES (?, ?)");
        // Relaciona al miembro con cada día marcado
        insertarSeleccionados(conexion, idMiembro, chkDias, idsDias,
                "INSERT INTO miembro_dia (id_miembro, id_dia) VALUES (?, ?)");
    }

    // Ejecuta la consulta una vez por cada casilla marcada, con el ID que le corresponde
    // por posición. Las casillas sin ID en la base de datos se ignoran.
    private void insertarSeleccionados(Connection conexion, int idMiembro, JCheckBox[] casillas,
            List<Integer> ids, String consulta) throws SQLException {
        for (int i = 0; i < casillas.length; i++) {
            if (casillas[i].isSelected() && i < ids.size()) {
                try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                    sentencia.setInt(1, idMiembro);
                    sentencia.setInt(2, ids.get(i));
                    sentencia.executeUpdate();
                }
            }
        }
    }

    // Métodos auxiliares

    // Copia la foto elegida a la carpeta de fotos, nombrada con el ID del miembro
    private void copiarFoto(int idMiembro) {
        File carpetaFotos = new File(Rutas.RUTA_FOTOS);
        if (!carpetaFotos.exists()) {
            carpetaFotos.mkdirs();
        }
        File archivoDestino = new File(carpetaFotos, idMiembro + EXTENSION_FOTO);

        try {
            Files.copy(fotoSeleccionada.toPath(), archivoDestino.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Miembro registrado, pero no se pudo copiar la foto.",
                    "Advertencia foto", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Escribe el recibo de pago en un archivo de texto y avisa dónde se guardó
    private void generarRecibo(int idMiembro, DatosPlan plan, LocalDate fechaInicio,
            LocalDate fechaVencimiento) {
        File carpetaRecibos = new File(Rutas.RUTA_RECIBOS);
        if (!carpetaRecibos.exists()) {
            carpetaRecibos.mkdirs();
        }
        String rutaRecibo = new File(carpetaRecibos, "recibo_" + idMiembro + ".txt").getAbsolutePath();

        String correo = obtenerTexto(txtCorreo);
        String telefono = obtenerTexto(txtTelefono);
        String nombreCompleto = obtenerTexto(txtNombres) + " " + obtenerTexto(txtApellidoPaterno)
                + " " + obtenerTexto(txtApellidoMaterno);

        String[] lineasRecibo = {
            LINEA_DOBLE,
            "        GYMFAT - COMPROBANTE DE PAGO    ",
            LINEA_DOBLE,
            "Fecha de expedicion: " + LocalDate.now(),
            "No. de miembro:      " + idMiembro,
            LINEA_SIMPLE,
            "DATOS DEL MIEMBRO",
            "Nombre:   " + nombreCompleto,
            "Correo:   " + (correo.isEmpty() ? TEXTO_NO_REGISTRADO : correo),
            "Telefono: " + (telefono.isEmpty() ? TEXTO_NO_REGISTRADO : telefono),
            LINEA_SIMPLE,
            "DETALLE DE MEMBRESIA",
            "Plan:         " + plan.nombre,
            "Inicio:       " + fechaInicio,
            "Vencimiento:  " + fechaVencimiento,
            "Total:        $" + String.format("%.2f", plan.precio),
            LINEA_DOBLE,
            "      Gracias por unirte a GymFat!      ",
            LINEA_DOBLE
        };

        // El archivo se cierra solo al terminar el bloque, aunque ocurra un error
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutaRecibo))) {
            escritor.write(String.join(System.lineSeparator(), lineasRecibo));
            JOptionPane.showMessageDialog(this,
                    "Recibo generado en:\n" + rutaRecibo,
                    "Recibo generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el recibo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Regresa todos los campos del formulario a su estado inicial
    private void limpiarFormulario() {
        JTextField[] campos = {txtNombres, txtApellidoPaterno, txtApellidoMaterno,
            txtFechaNacimiento, txtCorreo, txtTelefono, txtContactoEmergencia,
            txtTelefonoEmergencia, txtRelacion, txtObjetivo, txtEstatura, txtPeso};
        for (JTextField txtCampo : campos) {
            txtCampo.setText("");
        }
        txaCondicion.setText("");
        txaLesion.setText("");
        rbNoEntrenador.setSelected(true);
        rbNoCondicion.setSelected(true);
        rbNoLesion.setSelected(true);
        chkTerminos.setSelected(false);
        desmarcarCasillas(chkClases);
        desmarcarCasillas(chkDias);
        cmbMembresia.setSelectedIndex(0);
        cmbEntrenador.setSelectedIndex(0);
        quitarFoto();
    }

    // Quita la marca de todas las casillas recibidas
    private void desmarcarCasillas(JCheckBox[] casillas) {
        for (JCheckBox chkOpcion : casillas) {
            chkOpcion.setSelected(false);
        }
    }

    // Devuelve el horario elegido. Si no se eligió ninguno se guarda el de la noche,
    // igual que en la versión anterior del formulario.
    private String obtenerHorario() {
        if (rbManana.isSelected()) {
            return HORARIO_MANANA;
        }
        if (rbTarde.isSelected()) {
            return HORARIO_TARDE;
        }
        return HORARIO_NOCHE;
    }

    // Revisa que el texto no pase del máximo de caracteres; si pasa, avisa al usuario
    private boolean validarLongitud(String texto, int maximo, String nombreCampo) {
        if (texto.length() > maximo) {
            mostrarAdvertencia(nombreCampo + ": máximo " + maximo + " caracteres.", "Error");
            return false;
        }
        return true;
    }

    // Revisa que la fecha, si se escribió, tenga el formato YYYY-MM-DD y sea una fecha real
    private boolean validarFecha(String fecha) {
        if (fecha.isEmpty()) {
            return true;
        }
        try {
            LocalDate.parse(fecha);
            return true;
        } catch (DateTimeParseException ex) {
            mostrarAdvertencia("Fecha inválida. Usa YYYY-MM-DD con valores reales.", "Error");
            return false;
        }
    }

    // Revisa que la medida, si se escribió, sea un número mayor que 0 y no pase del máximo
    private boolean validarMedida(String medida, String mensajeFueraDeRango, String mensajeNoNumero) {
        if (medida.isEmpty()) {
            return true;
        }
        try {
            double valorMedida = Double.parseDouble(medida);
            if (valorMedida <= 0 || valorMedida > MAXIMO_MEDIDA) {
                mostrarAdvertencia(mensajeFueraDeRango, "Error");
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            mostrarAdvertencia(mensajeNoNumero, "Error");
            return false;
        }
    }

    // Convierte una medida ya validada a número, o null si se dejó vacía
    private Double convertirMedida(String medida) {
        return medida.isEmpty() ? null : Double.valueOf(medida);
    }

    // Devuelve el texto del campo sin espacios a los lados
    private String obtenerTexto(JTextField txtCampo) {
        return txtCampo.getText().trim();
    }

    // Devuelve el texto del campo sin espacios, o null si quedó vacío,
    // para que los campos opcionales se guarden como NULL
    private String textoONulo(JTextField txtCampo) {
        String texto = obtenerTexto(txtCampo);
        return texto.isEmpty() ? null : texto;
    }

    // Muestra un mensaje de advertencia con el título indicado
    private void mostrarAdvertencia(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    // Datos del plan que se usan para la membresía y el recibo
    private static class DatosPlan {

        private int duracionDias = DURACION_PLAN_PREDETERMINADA;
        private double precio = 0;
        private String nombre = "";
    }
}