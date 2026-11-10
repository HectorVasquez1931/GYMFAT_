// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - DetalleMiembro
// Descripción: Ventana que muestra toda la información de un miembro: datos
// personales, contacto de emergencia, membresía, salud, clases, días y
// últimas visitas.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

// Ventana de solo lectura con el detalle de un miembro
public class DetalleMiembro extends JDialog {

    // Constantes

    // Estado de membresía que se muestra en verde; cualquier otro se muestra en rojo
    private static final String ESTADO_ACTIVA = "Activa";

    // Número de visitas recientes que se muestran
    private static final int LIMITE_VISITAS = 5;

    // Texto que se muestra cuando un dato viene vacío de la base de datos
    private static final String VALOR_VACIO = "---";

    // Texto que se muestra cuando la membresía no tiene estado
    private static final String ESTADO_VACIO = "—";

    // Separador entre los elementos de una lista (clases y días)
    private static final String SEPARADOR_LISTA = ", ";

    // Tamaños, en píxeles
    private static final int ANCHO_VENTANA = 600;
    private static final int ALTO_VENTANA = 670;
    private static final int ANCHO_ETIQUETA = 160;
    private static final int ALTO_ETIQUETA = 20;
    private static final int ESPACIO_ENTRE_SECCIONES = 10;

    // Colores propios de esta ventana: fondo y texto de la insignia del estado
    private static final Color FONDO_ESTADO_ACTIVA = new Color(220, 255, 220);
    private static final Color TEXTO_ESTADO_ACTIVA = new Color(0, 130, 0);
    private static final Color FONDO_ESTADO_INACTIVA = new Color(255, 220, 220);
    private static final Color TEXTO_ESTADO_INACTIVA = new Color(180, 0, 0);

    // Fuentes de esta ventana
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 22);
    private static final Font FUENTE_TITULO_SECCION = new Font(Estilos.FUENTE, Font.BOLD, 12);
    private static final Font FUENTE_ETIQUETA = new Font(Estilos.FUENTE, Font.BOLD, 13);
    private static final Font FUENTE_VALOR = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_ESTADO = new Font(Estilos.FUENTE, Font.BOLD, 12);

    // Atributos y componentes de la interfaz

    // ID del miembro cuya información se muestra
    private final int idMiembro;

    // Constructor

    // Crea la ventana con la información del miembro indicado y la muestra
    public DetalleMiembro(int idMiembro) {
        this.idMiembro = idMiembro;
        setLayout(new BorderLayout());
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(Color.LIGHT_GRAY);
        construirInterfaz();
        setVisible(true);
    }

    // Construcción de la interfaz

    // Coloca el título arriba y las secciones con la información en un panel con scroll
    private void construirInterfaz() {
        JPanel pnlContenido = new JPanel();
        pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS));
        pnlContenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        cargarSecciones(pnlContenido);

        JLabel lblTitulo = new JLabel("Información detallada");
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scrContenido = new JScrollPane(pnlContenido);
        scrContenido.getViewport().setBackground(Color.WHITE);
        scrContenido.setBorder(null);
        add(scrContenido, BorderLayout.CENTER);
    }

    // Crea una sección blanca con borde amarillo y su título
    private JPanel crearSeccion(String titulo) {
        JPanel pnlSeccion = new JPanel();
        pnlSeccion.setLayout(new BoxLayout(pnlSeccion, BoxLayout.Y_AXIS));
        pnlSeccion.setBackground(Color.WHITE);
        pnlSeccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        pnlSeccion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Estilos.AMARILLO, 2), titulo,
                        TitledBorder.LEFT, TitledBorder.TOP, FUENTE_TITULO_SECCION),
                BorderFactory.createEmptyBorder(2, 8, 8, 8)));
        return pnlSeccion;
    }

    // Crea una fila con la etiqueta en negrita a la izquierda y el valor a la derecha
    private JPanel crearFila(String etiqueta, String valor) {
        JLabel lblValor = new JLabel(valor != null ? valor : VALOR_VACIO);
        lblValor.setFont(FUENTE_VALOR);
        return crearFilaConComponente(etiqueta, lblValor);
    }

    // Crea la fila del estado de la membresía con una insignia verde (activa) o roja
    private JPanel crearFilaEstado(String estado) {
        JLabel lblEstado = new JLabel(estado != null ? "  " + estado + "  " : ESTADO_VACIO);
        lblEstado.setFont(FUENTE_ESTADO);
        lblEstado.setOpaque(true);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        if (ESTADO_ACTIVA.equalsIgnoreCase(estado)) {
            lblEstado.setBackground(FONDO_ESTADO_ACTIVA);
            lblEstado.setForeground(TEXTO_ESTADO_ACTIVA);
        } else {
            lblEstado.setBackground(FONDO_ESTADO_INACTIVA);
            lblEstado.setForeground(TEXTO_ESTADO_INACTIVA);
        }
        return crearFilaConComponente("Estado", lblEstado);
    }

    // Arma una fila con la etiqueta a la izquierda y el componente recibido al centro
    private JPanel crearFilaConComponente(String etiqueta, JLabel lblValor) {
        JPanel pnlFila = new JPanel(new BorderLayout());
        pnlFila.setBackground(Color.WHITE);
        pnlFila.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        JLabel lblEtiqueta = new JLabel(etiqueta + ":");
        lblEtiqueta.setFont(FUENTE_ETIQUETA);
        lblEtiqueta.setForeground(Estilos.GRIS_TEXTO_ENCABEZADO);
        lblEtiqueta.setPreferredSize(new Dimension(ANCHO_ETIQUETA, ALTO_ETIQUETA));

        pnlFila.add(lblEtiqueta, BorderLayout.WEST);
        pnlFila.add(lblValor, BorderLayout.CENTER);
        return pnlFila;
    }

    // Agrega una sección al contenido seguida del espacio que la separa de la siguiente
    private void agregarSeccionConEspacio(JPanel pnlContenido, JPanel pnlSeccion) {
        pnlContenido.add(pnlSeccion);
        pnlContenido.add(Box.createRigidArea(new Dimension(0, ESPACIO_ENTRE_SECCIONES)));
    }

    // Acceso a datos

    // Consulta la información del miembro y agrega una sección por cada tipo de dato.
    // Las clases, los días y las visitas solo se muestran si el miembro tiene registro de salud.
    private void cargarSecciones(JPanel pnlContenido) {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        try {
            agregarDatosPersonales(conexion, pnlContenido);
            agregarContactoEmergencia(conexion, pnlContenido);
            agregarMembresia(conexion, pnlContenido);
            if (agregarSalud(conexion, pnlContenido)) {
                agregarClases(conexion, pnlContenido);
                agregarDias(conexion, pnlContenido);
                agregarVisitas(conexion, pnlContenido);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la información del miembro.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Agrega la sección de datos personales y el entrenador asignado
    private void agregarDatosPersonales(Connection conexion, JPanel pnlContenido) throws SQLException {
        // Obtiene todos los datos del miembro y el nombre de su entrenador, si tiene
        String consulta = "SELECT m.*, e.nombre AS entrenador "
                + "FROM miembro m "
                + "LEFT JOIN entrenador e ON m.id_entrenador = e.id_entrenador "
                + "WHERE m.id_miembro = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return;
                }
                String apellidoMaterno = resultado.getString("apellido_materno");
                String nombreCompleto = resultado.getString("nombres") + " "
                        + resultado.getString("apellido_paterno") + " "
                        + (apellidoMaterno != null ? apellidoMaterno : "");
                String entrenador = resultado.getString("entrenador");

                JPanel pnlDatosPersonales = crearSeccion("Datos personales");
                pnlDatosPersonales.add(crearFila("Nombre", nombreCompleto));
                pnlDatosPersonales.add(crearFila("Fecha de nacimiento", resultado.getString("fecha_nacimiento")));
                pnlDatosPersonales.add(crearFila("Correo", resultado.getString("correo_electronico")));
                pnlDatosPersonales.add(crearFila("Teléfono", resultado.getString("telefono")));
                pnlDatosPersonales.add(crearFila("Objetivo", resultado.getString("objetivo_principal")));
                pnlDatosPersonales.add(crearFila("Horario", resultado.getString("horario_deseado")));
                pnlDatosPersonales.add(crearFila("Entrenador",
                        entrenador != null ? entrenador : "Sin entrenador"));
                pnlContenido.add(pnlDatosPersonales);
            }
        }
    }

    // Agrega la sección del contacto de emergencia, si el miembro tiene uno
    private void agregarContactoEmergencia(Connection conexion, JPanel pnlContenido) throws SQLException {
        // Obtiene el contacto de emergencia del miembro
        String consulta = "SELECT * FROM contacto_emergencia WHERE id_miembro = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return;
                }
                JPanel pnlContacto = crearSeccion("Contacto de Emergencia");
                pnlContacto.add(crearFila("Nombre", resultado.getString("nombre")));
                pnlContacto.add(crearFila("Teléfono", resultado.getString("telefono")));
                pnlContacto.add(crearFila("Relación", resultado.getString("relacion")));
                agregarSeccionConEspacio(pnlContenido, pnlContacto);
            }
        }
    }

    // Agrega la sección de la membresía con su plan, fechas y estado
    private void agregarMembresia(Connection conexion, JPanel pnlContenido) throws SQLException {
        // Obtiene la membresía del miembro con el nombre y precio de su plan
        String consulta = "SELECT mem.*, p.nombre_plan, p.precio_plan "
                + "FROM membresia mem "
                + "JOIN planes p ON mem.id_plan = p.id_plan "
                + "WHERE mem.id_miembro = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return;
                }
                JPanel pnlMembresia = crearSeccion("Membresía");
                pnlMembresia.add(crearFila("Plan", resultado.getString("nombre_plan")));
                pnlMembresia.add(crearFila("Precio", "$" + resultado.getString("precio_plan")));
                pnlMembresia.add(crearFila("Inicio", resultado.getString("fecha_inicio")));
                pnlMembresia.add(crearFila("Vencimiento", resultado.getString("fecha_vencimiento")));
                pnlMembresia.add(crearFilaEstado(resultado.getString("estado")));
                agregarSeccionConEspacio(pnlContenido, pnlMembresia);
            }
        }
    }

    // Agrega la sección de salud. Devuelve true si el miembro tiene registro de salud.
    private boolean agregarSalud(Connection conexion, JPanel pnlContenido) throws SQLException {
        // Obtiene el registro de salud del miembro
        String consulta = "SELECT * FROM salud WHERE id_miembro = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return false;
                }
                boolean tieneCondicionMedica = resultado.getBoolean("tiene_condicion_medica");
                boolean tieneLesiones = resultado.getBoolean("tiene_lesiones");

                JPanel pnlSalud = crearSeccion("Salud");
                pnlSalud.add(crearFila("Condición médica", tieneCondicionMedica ? "Sí" : "No"));
                if (tieneCondicionMedica) {
                    pnlSalud.add(crearFila("Descripción", resultado.getString("descripcion_condicion")));
                }
                pnlSalud.add(crearFila("Lesiones recientes", tieneLesiones ? "Sí" : "No"));
                agregarSeccionConEspacio(pnlContenido, pnlSalud);
                return true;
            }
        }
    }

    // Agrega la sección con las clases en las que está inscrito el miembro
    private void agregarClases(Connection conexion, JPanel pnlContenido) throws SQLException {
        // Obtiene el nombre de cada clase en la que está inscrito el miembro
        String consulta = "SELECT c.nombre_clase FROM miembro_clase mc "
                + "JOIN clase c ON mc.id_clase = c.id_clase "
                + "WHERE mc.id_miembro = ?";
        String clases = consultarLista(conexion, consulta, "nombre_clase");

        JPanel pnlClases = crearSeccion("Clases");
        pnlClases.add(crearFila("Clases inscritas", clases.isEmpty() ? "Ninguna" : clases));
        agregarSeccionConEspacio(pnlContenido, pnlClases);
    }

    // Agrega la sección con los días en los que el miembro puede asistir
    private void agregarDias(Connection conexion, JPanel pnlContenido) throws SQLException {
        // Obtiene el nombre de cada día disponible del miembro
        String consulta = "SELECT d.nombre_dia FROM miembro_dia md "
                + "JOIN dia d ON md.id_dia = d.id_dia "
                + "WHERE md.id_miembro = ?";
        String dias = consultarLista(conexion, consulta, "nombre_dia");

        JPanel pnlDias = crearSeccion("Días disponibles");
        pnlDias.add(crearFila("Días", dias.isEmpty() ? "Ninguno" : dias));
        agregarSeccionConEspacio(pnlContenido, pnlDias);
    }

    // Agrega la sección con las visitas más recientes del miembro
    private void agregarVisitas(Connection conexion, JPanel pnlContenido) throws SQLException {
        // Obtiene las últimas visitas del miembro, de la más reciente a la más antigua
        String consulta = "SELECT TOP (?) fecha, hora_entrada, hora_salida "
                + "FROM visita WHERE id_miembro = ? ORDER BY fecha DESC";

        JPanel pnlVisitas = crearSeccion("Últimas visitas");
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, LIMITE_VISITAS);
            sentencia.setInt(2, idMiembro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                boolean tieneVisitas = false;
                while (resultado.next()) {
                    tieneVisitas = true;
                    pnlVisitas.add(crearFila(resultado.getString("fecha"),
                            "Entrada: " + resultado.getString("hora_entrada")
                            + "   Salida: " + resultado.getString("hora_salida")));
                }
                if (!tieneVisitas) {
                    pnlVisitas.add(crearFila("", "Sin visitas registradas"));
                }
            }
        }
        pnlContenido.add(pnlVisitas);
    }

    // Ejecuta una consulta del miembro y une los valores de la columna indicada,
    // separados por coma. Devuelve una cadena vacía si no hay resultados.
    private String consultarLista(Connection conexion, String consulta, String columna) throws SQLException {
        StringBuilder lista = new StringBuilder();
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMiembro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    if (lista.length() > 0) {
                        lista.append(SEPARADOR_LISTA);
                    }
                    lista.append(resultado.getString(columna));
                }
            }
        }
        return lista.toString();
    }
}