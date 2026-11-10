// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - PanelInicio
// Descripción: Pantalla de inicio. Busca miembros, registra entradas y
// salidas, renueva membresías y muestra el historial.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

// Pantalla de inicio: control de entradas y salidas e información del miembro
public class PanelInicio extends JPanel {

    // Constantes

    // Texto que se muestra cuando un dato no está disponible
    private static final String TEXTO_VACIO = "—";

    // Valor de idMiembroActual cuando no se ha buscado ningún miembro
    private static final int SIN_MIEMBRO = -1;

    // Valor que indica que no hay ningún plan seleccionado en el diálogo de renovación
    private static final int SIN_SELECCION = -1;

    // Estado que tiene en la base de datos una membresía vigente
    private static final String ESTADO_ACTIVA = "activa";

    // Número máximo de visitas que se muestran en el historial
    private static final int LIMITE_HISTORIAL = 50;

    // Extensiones con las que se buscan las fotos de los miembros en Rutas.RUTA_FOTOS
    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_PNG = ".png";

    // Tamaño del recuadro de la foto del miembro, en píxeles
    private static final int ANCHO_FOTO = 110;
    private static final int ALTO_FOTO = 120;

    // Tamaños de otros componentes, en píxeles
    private static final int ALTO_FILA_HISTORIAL = 22;
    private static final int ALTO_BOTON_MOVIMIENTO = 42;
    private static final int ANCHO_BOTON_CHICO = 70;
    private static final int ALTO_BOTON_CHICO = 28;
    private static final int ANCHO_DIALOGO_RENOVAR = 420;
    private static final int ALTO_DIALOGO_RENOVAR = 300;

    // Tiempo entre cada actualización del reloj, en milisegundos
    private static final int INTERVALO_RELOJ = 1000;

    // Formatos de fecha y hora para el historial y el reloj
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_RELOJ = DateTimeFormatter.ofPattern(
            "EEEE, dd 'de' MMMM yyyy   HH:mm:ss", new Locale("es", "MX"));

    // Colores propios de esta pantalla
    private static final Color GRIS_BORDE_FOTO = new Color(220, 220, 220);
    private static final Color FONDO_FOTO_GENERICA = new Color(230, 230, 230);
    private static final Color GRIS_SILUETA = new Color(180, 180, 180);
    private static final Color GRIS_TEXTO_FOTO = new Color(130, 130, 130);

    // Fuentes de esta pantalla
    private static final Font FUENTE_BIENVENIDA = new Font(Estilos.FUENTE, Font.BOLD, 28);
    private static final Font FUENTE_TEXTO = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_BOTON = new Font(Estilos.FUENTE, Font.BOLD, 13);
    private static final Font FUENTE_SUBTITULO = new Font(Estilos.FUENTE, Font.BOLD, 12);
    private static final Font FUENTE_TITULO_HISTORIAL = new Font(Estilos.FUENTE, Font.BOLD, 11);
    private static final Font FUENTE_TITULO_DIALOGO = new Font(Estilos.FUENTE, Font.BOLD, 14);
    private static final Font FUENTE_NOMBRE_MIEMBRO = new Font(Estilos.FUENTE, Font.BOLD, 15);
    private static final Font FUENTE_ESTADO = new Font(Estilos.FUENTE, Font.BOLD, 13);
    private static final Font FUENTE_SIN_FOTO = new Font(Estilos.FUENTE, Font.PLAIN, 10);

    // Atributos y componentes de la interfaz

    // Nombre del usuario en sesión, para el mensaje de bienvenida
    private final String nombreUsuario;

    // Campo para buscar al miembro por ID o por nombre
    private JTextField txtBuscar;

    // Datos del miembro encontrado
    private JLabel lblNombreMiembro;
    private JLabel lblEstado;
    private JLabel lblPlan;
    private JLabel lblVencimiento;
    private JLabel lblPago;
    private JLabel lblTelefono;

    // Fecha y hora actuales; se actualiza cada segundo
    private JLabel lblReloj;

    // Foto del miembro, o una silueta si no tiene foto
    private JLabel lblFoto;

    // Solo se muestra cuando la membresía del miembro no está activa
    private JButton btnRenovar;

    // ID del miembro que se está mostrando
    private int idMiembroActual = SIN_MIEMBRO;

    // Copia de las visitas del historial, de la más reciente a la más antigua
    private final Deque<Object[]> visitas = new ArrayDeque<>();

    // Filas de la tabla del historial reciente
    private DefaultTableModel modeloHistorial;

    // Constructor

    // Crea la pantalla, arranca el reloj y carga el historial de visitas
    public PanelInicio(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        setLayout(new BorderLayout(10, 10));
        setBackground(Estilos.FONDO_PANTALLA);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);

        iniciarReloj();
        cargarHistorialReciente();
    }

    // Construcción de la interfaz

    // Crea el mensaje de bienvenida a la izquierda y el reloj a la derecha
    private JPanel crearEncabezado() {
        JPanel pnlEncabezado = new JPanel(new BorderLayout());
        pnlEncabezado.setOpaque(false);

        JLabel lblBienvenida = new JLabel("Bienvenido(a) " + nombreUsuario);
        lblBienvenida.setFont(FUENTE_BIENVENIDA);

        lblReloj = new JLabel("", SwingConstants.RIGHT);
        lblReloj.setFont(FUENTE_TEXTO);
        lblReloj.setForeground(Color.DARK_GRAY);

        pnlEncabezado.add(lblBienvenida, BorderLayout.WEST);
        pnlEncabezado.add(lblReloj, BorderLayout.EAST);
        return pnlEncabezado;
    }

    // Divide el centro en dos columnas: movimientos a la izquierda e información a la derecha
    private JPanel crearPanelCentral() {
        JPanel pnlCentral = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlCentral.setOpaque(false);
        pnlCentral.add(crearPanelMovimiento());
        pnlCentral.add(crearPanelInfoMiembro());
        return pnlCentral;
    }

    // Crea el panel izquierdo: búsqueda, botones de movimiento e historial
    private JPanel crearPanelMovimiento() {
        JPanel pnlMovimiento = crearPanelConTitulo("Registrar Entrada y Salida");
        pnlMovimiento.setLayout(new BorderLayout(0, 10));
        pnlMovimiento.add(crearPanelControles(), BorderLayout.NORTH);
        pnlMovimiento.add(crearTablaHistorial(), BorderLayout.CENTER);
        return pnlMovimiento;
    }

    // Crea el campo de búsqueda y los botones para marcar entrada y salida
    private JPanel crearPanelControles() {
        JPanel pnlBusqueda = new JPanel(new BorderLayout(6, 0));
        pnlBusqueda.setOpaque(false);
        txtBuscar = new JTextField();
        txtBuscar.setFont(FUENTE_TEXTO);
        txtBuscar.setToolTipText("Buscar Miembro por ID o Nombre...");
        JButton btnBuscar = crearBotonChico("Buscar", Color.DARK_GRAY);
        pnlBusqueda.add(txtBuscar, BorderLayout.CENTER);
        pnlBusqueda.add(btnBuscar, BorderLayout.EAST);

        JButton btnEntrada = crearBotonMovimiento("MARCAR ENTRADA", Estilos.VERDE);
        JButton btnSalida = crearBotonMovimiento("MARCAR SALIDA", Estilos.ROJO);
        JPanel pnlBotones = new JPanel(new GridLayout(2, 1, 0, 8));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnEntrada);
        pnlBotones.add(btnSalida);

        JLabel lblMovimiento = new JLabel("Registrar Movimiento");
        lblMovimiento.setFont(FUENTE_SUBTITULO);

        JPanel pnlControles = new JPanel(new BorderLayout(0, 6));
        pnlControles.setOpaque(false);
        pnlControles.add(pnlBusqueda, BorderLayout.NORTH);
        pnlControles.add(lblMovimiento, BorderLayout.CENTER);
        pnlControles.add(pnlBotones, BorderLayout.SOUTH);

        // Se busca al presionar Enter en el campo o al dar clic en Buscar
        txtBuscar.addActionListener(evento -> buscarMiembro(txtBuscar.getText().trim()));
        btnBuscar.addActionListener(evento -> buscarMiembro(txtBuscar.getText().trim()));
        btnEntrada.addActionListener(evento -> registrarEntrada());
        btnSalida.addActionListener(evento -> registrarSalida());

        return pnlControles;
    }

    // Crea la tabla de solo lectura con las visitas recientes
    private JScrollPane crearTablaHistorial() {
        String[] columnas = {"Fecha", "Hora Entrada", "Hora Salida"};
        modeloHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tblHistorial = new JTable(modeloHistorial);
        tblHistorial.setRowHeight(ALTO_FILA_HISTORIAL);
        tblHistorial.getTableHeader().setBackground(Estilos.AMARILLO);
        tblHistorial.getTableHeader().setFont(FUENTE_SUBTITULO);

        JScrollPane scrHistorial = new JScrollPane(tblHistorial);
        scrHistorial.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Estilos.AMARILLO, 1),
                "Historial Reciente",
                TitledBorder.LEFT, TitledBorder.TOP,
                FUENTE_TITULO_HISTORIAL));
        return scrHistorial;
    }

    // Crea el panel derecho con la foto y los datos del miembro
    private JPanel crearPanelInfoMiembro() {
        JPanel pnlInfoMiembro = crearPanelConTitulo("Informacion del Miembro");
        pnlInfoMiembro.setLayout(new BorderLayout(0, 10));

        // Muestra la silueta genérica hasta que se busque un miembro
        lblFoto = new JLabel(crearFotoGenerica(), SwingConstants.CENTER);
        lblFoto.setPreferredSize(new Dimension(ANCHO_FOTO, ALTO_FOTO));
        lblFoto.setBorder(BorderFactory.createLineBorder(GRIS_BORDE_FOTO, 1));

        lblNombreMiembro = crearEtiquetaNegrita(TEXTO_VACIO, FUENTE_NOMBRE_MIEMBRO);
        // El estado va en negrita para que resalte su color
        lblEstado = crearEtiquetaNegrita("Estado: " + TEXTO_VACIO, FUENTE_ESTADO);
        lblPlan = crearEtiquetaNormal("Tipo Membresia: " + TEXTO_VACIO);
        lblVencimiento = crearEtiquetaNormal("Vencimiento: " + TEXTO_VACIO);
        lblPago = crearEtiquetaNormal("Ultimo Pago: " + TEXTO_VACIO);
        lblTelefono = crearEtiquetaNormal("Telefono: " + TEXTO_VACIO);

        // Empieza oculto; se muestra al encontrar un miembro con membresía no activa
        btnRenovar = new JButton("Renovar Membresia");
        btnRenovar.setBackground(Estilos.AMARILLO);
        btnRenovar.setForeground(Color.BLACK);
        btnRenovar.setFont(FUENTE_SUBTITULO);
        btnRenovar.setBorderPainted(false);
        btnRenovar.setFocusPainted(false);
        btnRenovar.setVisible(false);
        btnRenovar.addActionListener(evento -> abrirDialogoRenovar());

        JPanel pnlDatos = new JPanel(new GridLayout(0, 1, 0, 7));
        pnlDatos.setOpaque(false);
        pnlDatos.add(lblNombreMiembro);
        pnlDatos.add(lblEstado);
        pnlDatos.add(lblPlan);
        pnlDatos.add(lblVencimiento);
        pnlDatos.add(lblPago);
        pnlDatos.add(lblTelefono);
        pnlDatos.add(btnRenovar);

        pnlInfoMiembro.add(lblFoto, BorderLayout.NORTH);
        pnlInfoMiembro.add(pnlDatos, BorderLayout.CENTER);
        return pnlInfoMiembro;
    }

    // Muestra el diálogo con un botón de opción por plan para renovar la membresía
    private void mostrarDialogoRenovar(List<Integer> idsPlanes, List<Integer> duracionesPlanes,
            List<String> etiquetasPlanes) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        JDialog dlgRenovar = new JDialog(
                (ventanaPadre instanceof Frame) ? (Frame) ventanaPadre : null,
                "Renovar Membresia", true);
        dlgRenovar.setSize(ANCHO_DIALOGO_RENOVAR, ALTO_DIALOGO_RENOVAR);
        dlgRenovar.setLocationRelativeTo(this);
        dlgRenovar.setLayout(new BorderLayout(10, 10));
        dlgRenovar.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblInstruccion = new JLabel("Selecciona el nuevo plan:");
        lblInstruccion.setFont(FUENTE_TITULO_DIALOGO);
        dlgRenovar.add(lblInstruccion, BorderLayout.NORTH);

        JRadioButton[] rbPlanes = new JRadioButton[etiquetasPlanes.size()];
        dlgRenovar.add(new JScrollPane(crearPanelPlanes(etiquetasPlanes, rbPlanes)), BorderLayout.CENTER);

        JButton btnConfirmar = crearBotonColor("Confirmar Pago", Estilos.VERDE);
        JButton btnCancelar = crearBotonColor("Cancelar", Estilos.ROJO);
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnConfirmar);
        dlgRenovar.add(pnlBotones, BorderLayout.SOUTH);

        // Al confirmar se cierra el diálogo y se renueva con el plan elegido
        btnConfirmar.addActionListener(evento -> {
            int indiceSeleccionado = obtenerIndiceSeleccionado(rbPlanes);
            if (indiceSeleccionado == SIN_SELECCION) {
                return;
            }
            dlgRenovar.dispose();
            renovarMembresia(idsPlanes.get(indiceSeleccionado), duracionesPlanes.get(indiceSeleccionado));
        });
        btnCancelar.addActionListener(evento -> dlgRenovar.dispose());

        dlgRenovar.setVisible(true);
    }

    // Crea un botón de opción por cada plan y los agrupa; el primero queda seleccionado.
    // Los botones creados se guardan en rbPlanes para saber después cuál se eligió.
    private JPanel crearPanelPlanes(List<String> etiquetasPlanes, JRadioButton[] rbPlanes) {
        JPanel pnlPlanes = new JPanel(new GridLayout(0, 1, 0, 8));
        pnlPlanes.setBackground(Color.WHITE);
        pnlPlanes.setBorder(BorderFactory.createLineBorder(Estilos.AMARILLO, 2));
        ButtonGroup grpPlanes = new ButtonGroup();

        for (int i = 0; i < etiquetasPlanes.size(); i++) {
            rbPlanes[i] = new JRadioButton(etiquetasPlanes.get(i));
            rbPlanes[i].setFont(FUENTE_TEXTO);
            rbPlanes[i].setBackground(Color.WHITE);
            if (i == 0) {
                rbPlanes[i].setSelected(true);
            }
            grpPlanes.add(rbPlanes[i]);
            pnlPlanes.add(rbPlanes[i]);
        }
        return pnlPlanes;
    }

    // Eventos

    // Busca en segundo plano al miembro escrito en el campo de búsqueda
    private void buscarMiembro(String termino) {
        if (termino.isEmpty()) {
            return;
        }
        new Thread(() -> consultarMiembro(termino), "hilo-busqueda").start();
    }

    // Carga los planes en segundo plano y después abre el diálogo de renovación
    private void abrirDialogoRenovar() {
        new Thread(this::consultarPlanes, "hilo-cargar-planes").start();
    }

    // Guarda en segundo plano la renovación con el plan elegido
    private void renovarMembresia(int idPlan, int duracionDias) {
        new Thread(() -> actualizarMembresia(idPlan, duracionDias), "hilo-renovar").start();
    }

    // Registra la entrada del miembro mostrado, si ya se buscó alguno
    private void registrarEntrada() {
        if (idMiembroActual == SIN_MIEMBRO) {
            JOptionPane.showMessageDialog(this, "Busca un miembro primero.",
                    "Sin miembro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new Thread(this::guardarEntrada, "hilo-entrada").start();
    }

    // Registra la salida del miembro mostrado, si ya se buscó alguno
    private void registrarSalida() {
        if (idMiembroActual == SIN_MIEMBRO) {
            JOptionPane.showMessageDialog(this, "Busca un miembro primero.",
                    "Sin miembro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new Thread(this::guardarSalida, "hilo-salida").start();
    }

    // Acceso a datos

    // Busca al miembro por ID exacto o por nombre y muestra sus datos.
    // Se ejecuta en un hilo aparte.
    private void consultarMiembro(String termino) {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        // Obtiene el primer miembro que coincide, con su plan y su membresía más reciente
        String consulta
                = "SELECT TOP 1 m.id_miembro, "
                + "m.nombres + ' ' + m.apellido_paterno + ' ' + ISNULL(m.apellido_materno,'') AS nombre_completo, "
                + "m.telefono, p.nombre_plan, "
                + "mem.fecha_vencimiento, mem.fecha_inicio, mem.estado "
                + "FROM miembro m "
                + "LEFT JOIN membresia mem ON m.id_miembro = mem.id_miembro "
                + "LEFT JOIN planes p      ON mem.id_plan  = p.id_plan "
                + "WHERE CAST(m.id_miembro AS VARCHAR) = ? "
                + "   OR m.nombres + ' ' + m.apellido_paterno LIKE ? "
                + "ORDER BY mem.fecha_vencimiento DESC";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, termino);
            sentencia.setString(2, "%" + termino + "%");

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    int idMiembro = resultado.getInt("id_miembro");
                    String nombreCompleto = resultado.getString("nombre_completo");
                    String telefono = resultado.getString("telefono");
                    String plan = resultado.getString("nombre_plan");
                    String vencimiento = resultado.getString("fecha_vencimiento");
                    String ultimoPago = resultado.getString("fecha_inicio");
                    String estado = resultado.getString("estado");

                    SwingUtilities.invokeLater(() -> mostrarInfoMiembro(idMiembro, nombreCompleto,
                            telefono, plan, vencimiento, ultimoPago, estado));
                } else {
                    SwingUtilities.invokeLater(() -> {
                        limpiarInfoMiembro();
                        JOptionPane.showMessageDialog(this,
                                "No se encontro ningun miembro con: " + termino,
                                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            }
        } catch (SQLException ex) {
            mostrarError("No se pudo buscar el miembro.");
        }
    }

    // Carga los planes disponibles y abre el diálogo de renovación con ellos.
    // Se ejecuta en un hilo aparte.
    private void consultarPlanes() {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        // Obtiene todos los planes, del de menor al de mayor duración
        String consulta = "SELECT id_plan, nombre_plan, precio_plan, duracion_dias "
                + "FROM planes "
                + "ORDER BY duracion_dias ASC";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            List<Integer> idsPlanes = new ArrayList<>();
            List<Integer> duracionesPlanes = new ArrayList<>();
            List<String> etiquetasPlanes = new ArrayList<>();

            while (resultado.next()) {
                idsPlanes.add(resultado.getInt("id_plan"));
                duracionesPlanes.add(resultado.getInt("duracion_dias"));
                etiquetasPlanes.add(String.format("%s — $%.0f / %d dias",
                        resultado.getString("nombre_plan"),
                        resultado.getDouble("precio_plan"),
                        resultado.getInt("duracion_dias")));
            }

            SwingUtilities.invokeLater(()
                    -> mostrarDialogoRenovar(idsPlanes, duracionesPlanes, etiquetasPlanes));
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar los planes.");
        }
    }

    // Renueva la membresía del miembro mostrado desde hoy y vuelve a cargar sus datos.
    // Se ejecuta en un hilo aparte.
    private void actualizarMembresia(int idPlan, int duracionDias) {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = hoy.plusDays(duracionDias);

        // Cambia el plan del miembro, reinicia las fechas desde hoy y marca la membresía como activa
        String consulta = "UPDATE membresia "
                + "SET id_plan = ?, "
                + "    fecha_inicio = ?, "
                + "    fecha_vencimiento = ?, "
                + "    estado = 'activa' "
                + "WHERE id_miembro = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idPlan);
            sentencia.setDate(2, Date.valueOf(hoy));
            sentencia.setDate(3, Date.valueOf(vencimiento));
            sentencia.setInt(4, idMiembroActual);
            sentencia.executeUpdate();

            // Se vuelve a buscar al miembro para mostrar su nuevo estado
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Membresia renovada correctamente hasta: " + vencimiento,
                        "Renovacion exitosa", JOptionPane.INFORMATION_MESSAGE);
                buscarMiembro(String.valueOf(idMiembroActual));
            });
        } catch (SQLException ex) {
            mostrarError("No se pudo renovar la membresía.");
        }
    }

    // Guarda la entrada del miembro, siempre que no tenga ya una entrada sin salida hoy.
    // Se ejecuta en un hilo aparte.
    private void guardarEntrada() {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        try {
            if (tieneVisitaActiva(conexion, idMiembroActual)) {
                mostrarMensaje("Este miembro ya tiene una entrada activa hoy. "
                        + "Debe registrar la salida antes de volver a entrar.",
                        "Entrada duplicada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Registra una visita nueva con la fecha y hora actuales del servidor
            String consulta = "INSERT INTO visita (id_miembro, fecha, hora_entrada) "
                    + "VALUES (?, CAST(GETDATE() AS DATE), CAST(GETDATE() AS TIME))";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                sentencia.setInt(1, idMiembroActual);
                sentencia.executeUpdate();
            }

            String fecha = LocalDateTime.now().format(FORMATO_FECHA);
            String hora = LocalDateTime.now().format(FORMATO_HORA);
            agregarAlHistorial(fecha, hora, TEXTO_VACIO);

            mostrarMensaje("Entrada registrada correctamente.",
                    "Entrada OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            mostrarError("No se pudo registrar la entrada.");
        }
    }

    // Guarda la salida del miembro, siempre que tenga una entrada sin salida hoy.
    // Se ejecuta en un hilo aparte.
    private void guardarSalida() {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        try {
            if (!tieneVisitaActiva(conexion, idMiembroActual)) {
                mostrarMensaje("Este miembro no tiene ninguna entrada activa hoy. "
                        + "Registra la entrada primero.",
                        "Sin entrada activa", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Pone la hora actual como salida en la visita de hoy que sigue abierta
            String consulta = "UPDATE TOP (1) visita "
                    + "SET hora_salida = CAST(GETDATE() AS TIME) "
                    + "WHERE id_miembro = ? "
                    + "  AND fecha = CAST(GETDATE() AS DATE) "
                    + "  AND hora_salida IS NULL";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                sentencia.setInt(1, idMiembroActual);
                sentencia.executeUpdate();
            }

            String fecha = LocalDateTime.now().format(FORMATO_FECHA);
            String hora = LocalDateTime.now().format(FORMATO_HORA);
            agregarAlHistorial(fecha, TEXTO_VACIO, hora);

            mostrarMensaje("Salida registrada correctamente.",
                    "Salida OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            mostrarError("No se pudo registrar la salida.");
        }
    }

    // Verifica que el miembro tenga una entrada sin salida registrada en el día actual
    private boolean tieneVisitaActiva(Connection conexion, int idMiembro) throws SQLException {
        // Cuenta las visitas de hoy del miembro que todavía no tienen hora de salida
        String consulta = "SELECT COUNT(*) FROM visita "
                + "WHERE id_miembro = ? "
                + "  AND fecha = CAST(GETDATE() AS DATE) "
                + "  AND hora_salida IS NULL";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        }
    }

    // Carga en segundo plano las visitas más recientes en la tabla del historial
    private void cargarHistorialReciente() {
        new Thread(() -> {
            Connection conexion = ConexionBD.conectar();
            if (conexion == null) {
                return;
            }

            // Obtiene las últimas visitas con la fecha y las horas ya formateadas
            String consulta
                    = "SELECT TOP (?) "
                    + "CONVERT(VARCHAR, fecha, 103)                    AS fecha, "
                    + "CONVERT(VARCHAR, hora_entrada, 108)             AS hora_entrada, "
                    + "ISNULL(CONVERT(VARCHAR, hora_salida, 108), '—') AS hora_salida "
                    + "FROM visita "
                    + "ORDER BY fecha DESC, hora_entrada DESC";

            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                sentencia.setInt(1, LIMITE_HISTORIAL);
                try (ResultSet resultado = sentencia.executeQuery()) {
                    while (resultado.next()) {
                        String fecha = resultado.getString("fecha");
                        String horaEntrada = resultado.getString("hora_entrada");
                        String horaSalida = resultado.getString("hora_salida");
                        synchronized (visitas) {
                            visitas.addLast(new Object[]{fecha, horaEntrada, horaSalida});
                        }
                        SwingUtilities.invokeLater(()
                                -> modeloHistorial.addRow(new Object[]{fecha, horaEntrada, horaSalida}));
                    }
                }
            } catch (SQLException ex) {
                mostrarError("No se pudo cargar el historial.");
            }
        }, "hilo-historial").start();
    }

    // Métodos auxiliares

    // Inicia un hilo en segundo plano que actualiza el reloj cada segundo.
    // Es daemon para que no impida cerrar el programa.
    private void iniciarReloj() {
        Thread hiloReloj = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                String fechaHora = LocalDateTime.now().format(FORMATO_RELOJ);
                SwingUtilities.invokeLater(() -> lblReloj.setText(fechaHora));
                try {
                    Thread.sleep(INTERVALO_RELOJ);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }, "hilo-reloj");
        hiloReloj.setDaemon(true);
        hiloReloj.start();
    }

    // Muestra los datos del miembro encontrado. El estado se pinta verde si la
    // membresía está activa y rojo si no; en ese caso aparece el botón de renovar.
    private void mostrarInfoMiembro(int idMiembro, String nombreCompleto, String telefono,
            String plan, String vencimiento, String ultimoPago, String estado) {
        idMiembroActual = idMiembro;

        lblNombreMiembro.setText(nombreCompleto + "  (ID " + idMiembro + ")");
        lblPlan.setText("Tipo Membresia: " + valorOVacio(plan));
        lblVencimiento.setText("Vencimiento: " + valorOVacio(vencimiento));
        lblPago.setText("Ultimo Pago: " + valorOVacio(ultimoPago));
        lblTelefono.setText("Telefono: " + valorOVacio(telefono));

        boolean esActiva = ESTADO_ACTIVA.equalsIgnoreCase(estado);
        lblEstado.setText("Estado: " + valorOVacio(estado).toUpperCase());
        lblEstado.setForeground(esActiva ? Estilos.VERDE : Estilos.ROJO);
        btnRenovar.setVisible(!esActiva);

        cargarFoto(idMiembro);
    }

    // Regresa la información del miembro a su estado inicial
    private void limpiarInfoMiembro() {
        idMiembroActual = SIN_MIEMBRO;
        lblNombreMiembro.setText(TEXTO_VACIO);
        lblEstado.setText("Estado: " + TEXTO_VACIO);
        lblEstado.setForeground(Color.DARK_GRAY);
        lblPlan.setText("Tipo Membresia: " + TEXTO_VACIO);
        lblVencimiento.setText("Vencimiento: " + TEXTO_VACIO);
        lblPago.setText("Ultimo Pago: " + TEXTO_VACIO);
        lblTelefono.setText("Telefono: " + TEXTO_VACIO);
        btnRenovar.setVisible(false);
        lblFoto.setIcon(crearFotoGenerica());
    }

    // Agrega una visita al inicio del historial y quita la más antigua si se pasa del límite
    private void agregarAlHistorial(String fecha, String horaEntrada, String horaSalida) {
        synchronized (visitas) {
            if (visitas.size() >= LIMITE_HISTORIAL) {
                visitas.pollLast();
            }
            visitas.addFirst(new Object[]{fecha, horaEntrada, horaSalida});
        }
        SwingUtilities.invokeLater(() -> {
            modeloHistorial.insertRow(0, new Object[]{fecha, horaEntrada, horaSalida});
            if (modeloHistorial.getRowCount() > LIMITE_HISTORIAL) {
                modeloHistorial.removeRow(LIMITE_HISTORIAL);
            }
        });
    }

    // Devuelve la posición del plan seleccionado, o SIN_SELECCION si no hay ninguno
    private int obtenerIndiceSeleccionado(JRadioButton[] rbPlanes) {
        for (int i = 0; i < rbPlanes.length; i++) {
            if (rbPlanes[i].isSelected()) {
                return i;
            }
        }
        return SIN_SELECCION;
    }

    // Devuelve el texto recibido, o TEXTO_VACIO si viene nulo de la base de datos
    private String valorOVacio(String valor) {
        return valor != null ? valor : TEXTO_VACIO;
    }

    // Muestra un mensaje desde un hilo en segundo plano
    private void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        SwingUtilities.invokeLater(()
                -> JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje));
    }

    // Muestra un mensaje de error desde un hilo en segundo plano
    private void mostrarError(String mensaje) {
        mostrarMensaje(mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Crea un panel blanco con borde amarillo y título
    private JPanel crearPanelConTitulo(String titulo) {
        JPanel pnlConTitulo = new JPanel(new BorderLayout());
        pnlConTitulo.setBackground(Color.WHITE);
        TitledBorder bordeTitulo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Estilos.AMARILLO, 2), titulo);
        bordeTitulo.setTitleFont(FUENTE_BOTON);
        pnlConTitulo.setBorder(BorderFactory.createCompoundBorder(bordeTitulo,
                BorderFactory.createEmptyBorder(8, 10, 10, 10)));
        return pnlConTitulo;
    }

    // Crea un botón con fondo de color y texto blanco en negrita
    private JButton crearBotonColor(String texto, Color colorFondo) {
        JButton btnColor = new JButton(texto);
        btnColor.setBackground(colorFondo);
        btnColor.setForeground(Color.WHITE);
        btnColor.setFont(FUENTE_BOTON);
        btnColor.setBorderPainted(false);
        btnColor.setFocusPainted(false);
        return btnColor;
    }

    // Crea uno de los botones grandes para marcar entrada o salida
    private JButton crearBotonMovimiento(String texto, Color colorFondo) {
        JButton btnMovimiento = crearBotonColor(texto, colorFondo);
        btnMovimiento.setPreferredSize(new Dimension(0, ALTO_BOTON_MOVIMIENTO));
        return btnMovimiento;
    }

    // Crea un botón pequeño con fondo de color y texto blanco
    private JButton crearBotonChico(String texto, Color colorFondo) {
        JButton btnChico = new JButton(texto);
        btnChico.setBackground(colorFondo);
        btnChico.setForeground(Color.WHITE);
        btnChico.setBorderPainted(false);
        btnChico.setFocusPainted(false);
        btnChico.setPreferredSize(new Dimension(ANCHO_BOTON_CHICO, ALTO_BOTON_CHICO));
        return btnChico;
    }

    // Crea una etiqueta en negrita con la fuente indicada
    private JLabel crearEtiquetaNegrita(String texto, Font fuente) {
        JLabel lblNegrita = new JLabel(texto);
        lblNegrita.setFont(fuente);
        return lblNegrita;
    }

    // Crea una etiqueta de texto normal en gris oscuro
    private JLabel crearEtiquetaNormal(String texto) {
        JLabel lblNormal = new JLabel(texto);
        lblNormal.setFont(FUENTE_TEXTO);
        lblNormal.setForeground(Color.DARK_GRAY);
        return lblNormal;
    }

    // Carga en segundo plano la foto del miembro, escalada al tamaño del recuadro.
    // Si no tiene foto, muestra la silueta genérica.
    private void cargarFoto(int idMiembro) {
        new Thread(() -> {
            File archivoFoto = buscarArchivoFoto(idMiembro);

            final ImageIcon icono;
            if (archivoFoto.exists()) {
                ImageIcon imagenOriginal = new ImageIcon(archivoFoto.getAbsolutePath());
                Image imagenEscalada = imagenOriginal.getImage()
                        .getScaledInstance(ANCHO_FOTO, ALTO_FOTO, Image.SCALE_SMOOTH);
                icono = new ImageIcon(imagenEscalada);
            } else {
                icono = crearFotoGenerica();
            }
            SwingUtilities.invokeLater(() -> lblFoto.setIcon(icono));
        }, "hilo-foto").start();
    }

    // Busca la foto del miembro primero en .jpg y, si no existe, en .png
    private File buscarArchivoFoto(int idMiembro) {
        File archivoFoto = new File(Rutas.RUTA_FOTOS + idMiembro + EXTENSION_JPG);
        if (!archivoFoto.exists()) {
            archivoFoto = new File(Rutas.RUTA_FOTOS + idMiembro + EXTENSION_PNG);
        }
        return archivoFoto;
    }

    // Dibuja una silueta gris con el texto "Sin foto" para los miembros sin foto.
    // Las coordenadas están calculadas para el recuadro de ANCHO_FOTO x ALTO_FOTO.
    private ImageIcon crearFotoGenerica() {
        BufferedImage imagen = new BufferedImage(ANCHO_FOTO, ALTO_FOTO, BufferedImage.TYPE_INT_ARGB);
        Graphics2D grafico = imagen.createGraphics();
        grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo
        grafico.setColor(FONDO_FOTO_GENERICA);
        grafico.fillRect(0, 0, ANCHO_FOTO, ALTO_FOTO);

        // Cabeza (círculo) y cuerpo (rectángulo redondeado)
        grafico.setColor(GRIS_SILUETA);
        grafico.fillOval(35, 15, 40, 40);
        grafico.fillRoundRect(20, 60, 70, 45, 20, 20);

        // Texto centrado en la parte inferior
        grafico.setColor(GRIS_TEXTO_FOTO);
        grafico.setFont(FUENTE_SIN_FOTO);
        FontMetrics medidasFuente = grafico.getFontMetrics();
        String textoSinFoto = "Sin foto";
        grafico.drawString(textoSinFoto, (ANCHO_FOTO - medidasFuente.stringWidth(textoSinFoto)) / 2, ALTO_FOTO - 5);

        grafico.dispose();
        return new ImageIcon(imagen);
    }
}