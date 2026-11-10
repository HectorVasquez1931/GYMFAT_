// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - VentanaPrincipal
// Descripción: Ventana principal del sistema. Muestra la barra lateral con el
// menú y el usuario en sesión, y cambia entre las pantallas del sistema.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// Ventana principal que contiene el menú lateral y las pantallas del sistema
public class VentanaPrincipal extends JFrame implements ActionListener {

    // Constantes

    // Opciones del menú; cada texto también es el nombre de la pantalla que abre
    private static final String OPCION_INICIO = "Inicio";
    private static final String OPCION_REGISTRAR_MIEMBRO = "Registrar Miembro";
    private static final String OPCION_LISTA_MIEMBROS = "Lista de miembros";
    private static final String OPCION_MEMBRESIAS = "Membresias";
    private static final String OPCION_ENTRENADORES = "Entrenadores";
    private static final String OPCION_EMPLEADOS = "Empleados";

    // Orden en que aparecen las opciones en el menú lateral
    private static final String[] OPCIONES_MENU = {
        OPCION_INICIO,
        OPCION_REGISTRAR_MIEMBRO,
        OPCION_LISTA_MIEMBROS,
        OPCION_MEMBRESIAS,
        OPCION_ENTRENADORES,
        OPCION_EMPLEADOS
    };

    // Nivel de usuario que tiene acceso a la sección de empleados
    private static final String NIVEL_ADMINISTRADOR = "Administrador";

    // Tamaños de la ventana y de la barra lateral, en píxeles
    private static final int ANCHO_VENTANA = 1000;
    private static final int ALTO_VENTANA = 600;
    private static final int ANCHO_BARRA_LATERAL = 200;
    private static final int ALTO_TITULO = 40;
    private static final int ALTO_BOTON_MENU = 40;
    private static final int ALTO_BOTON_CERRAR_SESION = 32;

    // Ancho de la línea que separa el título del menú
    private static final int ANCHO_SEPARADOR = 160;

    // Colores propios de esta ventana
    private static final Color GRIS_SEPARADOR = new Color(60, 60, 60);
    private static final Color FONDO_PANEL_USUARIO = new Color(15, 15, 15);
    private static final Color GRIS_NIVEL = new Color(100, 100, 100);

    // Fondo que recibe un botón del menú al dejar de estar seleccionado
    private static final Color FONDO_BOTON_INACTIVO = new Color(20, 20, 20);

    // Fondo del botón de cerrar sesión cuando el cursor pasa sobre él
    private static final Color ROJO_CERRAR_SESION = new Color(180, 50, 50);

    // Fuentes de esta ventana
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 22);
    private static final Font FUENTE_MENU = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_MENU_ACTIVO = new Font(Estilos.FUENTE, Font.BOLD, 13);
    private static final Font FUENTE_USUARIO = new Font(Estilos.FUENTE, Font.BOLD, 13);
    private static final Font FUENTE_NIVEL = new Font(Estilos.FUENTE, Font.PLAIN, 11);
    private static final Font FUENTE_CERRAR_SESION = new Font(Estilos.FUENTE, Font.PLAIN, 12);

    // Atributos y componentes de la interfaz

    // Nivel del usuario en sesión (por ejemplo, Administrador)
    private final String nivel;

    // Nombre del usuario en sesión
    private final String nombreUsuario;

    // Distribución que muestra una sola pantalla a la vez dentro del panel de contenido
    private final CardLayout distribucionPantallas = new CardLayout();

    private final JPanel pnlContenido = new JPanel();

    // Pantalla de la lista de miembros; se vuelve a cargar cada vez que se abre
    private ListaMiembros pnlListaMiembros;

    // Botón del menú que corresponde a la pantalla visible
    private JButton btnActivo = null;

    // Constructor

    // Crea la ventana para el usuario que inició sesión y la muestra
    public VentanaPrincipal(String nivel, String nombreUsuario) {
        super("GymFat");

        this.nivel = nivel;
        this.nombreUsuario = nombreUsuario;
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        construirInterfaz();
        setLocationRelativeTo(null);

        // Al cerrar la ventana con la X se cierra también la conexión a la base de datos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                cerrarConexion();
                dispose();
            }
        });

        setVisible(true);
    }

    // Construcción de la interfaz

    // Coloca la barra lateral a la izquierda y las pantallas al centro
    private void construirInterfaz() {
        add(crearBarraLateral(), BorderLayout.WEST);
        add(crearPanelContenido(), BorderLayout.CENTER);
    }

    // Arma la barra lateral: título arriba, menú al centro y usuario abajo
    private JPanel crearBarraLateral() {
        JPanel pnlBarraLateral = new JPanel(new BorderLayout());
        pnlBarraLateral.setPreferredSize(new Dimension(ANCHO_BARRA_LATERAL, 0));
        pnlBarraLateral.setBackground(Color.BLACK);

        pnlBarraLateral.add(crearEncabezado(), BorderLayout.NORTH);
        pnlBarraLateral.add(crearMenu(), BorderLayout.CENTER);
        pnlBarraLateral.add(crearPanelUsuario(), BorderLayout.SOUTH);
        return pnlBarraLateral;
    }

    // Crea el título "GymFat" y la línea que lo separa del menú
    private JPanel crearEncabezado() {
        JPanel pnlEncabezado = new JPanel();
        pnlEncabezado.setLayout(new BoxLayout(pnlEncabezado, BoxLayout.Y_AXIS));
        pnlEncabezado.setBackground(Color.BLACK);

        JPanel pnlTitulo = new JPanel();
        pnlTitulo.setBackground(Color.BLACK);
        pnlTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JLabel lblTitulo = new JLabel("GymFat", SwingConstants.CENTER);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setMaximumSize(new Dimension(ANCHO_BARRA_LATERAL, ALTO_TITULO));
        lblTitulo.setForeground(Estilos.AMARILLO);
        pnlTitulo.add(lblTitulo);
        pnlEncabezado.add(pnlTitulo);

        JPanel pnlSeparador = new JPanel();
        pnlSeparador.setBackground(GRIS_SEPARADOR);
        pnlSeparador.setMaximumSize(new Dimension(ANCHO_SEPARADOR, 1));
        pnlSeparador.setPreferredSize(new Dimension(ANCHO_SEPARADOR, 1));
        pnlSeparador.setMinimumSize(new Dimension(ANCHO_SEPARADOR, 1));
        pnlEncabezado.add(pnlSeparador);

        return pnlEncabezado;
    }

    // Crea un botón por cada opción del menú
    private JPanel crearMenu() {
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setBackground(Color.BLACK);

        EfectoHoverMenu efectoHover = new EfectoHoverMenu();
        for (String opcion : OPCIONES_MENU) {
            pnlMenu.add(crearBotonMenu(opcion, efectoHover));
        }
        return pnlMenu;
    }

    // Crea un botón del menú con el estilo de la barra lateral
    private JButton crearBotonMenu(String texto, MouseAdapter efectoHover) {
        JButton btnOpcion = new JButton(texto);
        btnOpcion.setMaximumSize(new Dimension(ANCHO_BARRA_LATERAL, ALTO_BOTON_MENU));
        btnOpcion.setMinimumSize(new Dimension(ANCHO_BARRA_LATERAL, ALTO_BOTON_MENU));
        btnOpcion.setPreferredSize(new Dimension(ANCHO_BARRA_LATERAL, ALTO_BOTON_MENU));
        btnOpcion.setHorizontalAlignment(SwingConstants.LEFT);
        btnOpcion.setForeground(Estilos.GRIS_TEXTO);
        btnOpcion.setBorderPainted(false);
        btnOpcion.setFocusPainted(false);
        btnOpcion.setBackground(Color.BLACK);
        btnOpcion.setFont(FUENTE_MENU);
        btnOpcion.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btnOpcion.addActionListener(this);
        btnOpcion.addMouseListener(efectoHover);
        return btnOpcion;
    }

    // Muestra el nombre y nivel del usuario en sesión y el botón para cerrar sesión
    private JPanel crearPanelUsuario() {
        JPanel pnlUsuario = new JPanel();
        pnlUsuario.setLayout(new BoxLayout(pnlUsuario, BoxLayout.Y_AXIS));
        pnlUsuario.setBackground(FONDO_PANEL_USUARIO);
        pnlUsuario.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblUsuario = new JLabel(nombreUsuario);
        lblUsuario.setForeground(Estilos.GRIS_TEXTO);
        lblUsuario.setFont(FUENTE_USUARIO);
        lblUsuario.setAlignmentX(LEFT_ALIGNMENT);
        pnlUsuario.add(lblUsuario);

        JLabel lblNivel = new JLabel(nivel);
        lblNivel.setForeground(GRIS_NIVEL);
        lblNivel.setFont(FUENTE_NIVEL);
        lblNivel.setAlignmentX(LEFT_ALIGNMENT);
        pnlUsuario.add(lblNivel);

        pnlUsuario.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlUsuario.add(crearBotonCerrarSesion());
        return pnlUsuario;
    }

    // Crea el botón de cerrar sesión, que se pone rojo cuando el cursor pasa sobre él
    private JButton crearBotonCerrarSesion() {
        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.setBackground(Estilos.GRIS_OSCURO);
        btnCerrarSesion.setForeground(Estilos.GRIS_TEXTO);
        btnCerrarSesion.setFont(FUENTE_CERRAR_SESION);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_BOTON_CERRAR_SESION));
        btnCerrarSesion.setAlignmentX(LEFT_ALIGNMENT);
        btnCerrarSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.addActionListener(evento -> cerrarSesion());
        btnCerrarSesion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                btnCerrarSesion.setBackground(ROJO_CERRAR_SESION);
                btnCerrarSesion.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                btnCerrarSesion.setBackground(Estilos.GRIS_OSCURO);
                btnCerrarSesion.setForeground(Estilos.GRIS_TEXTO);
            }
        });
        return btnCerrarSesion;
    }

    // Agrega todas las pantallas del sistema y muestra la de inicio
    private JPanel crearPanelContenido() {
        pnlContenido.setLayout(distribucionPantallas);
        pnlContenido.add(new PanelInicio(nombreUsuario), OPCION_INICIO);
        pnlContenido.add(new PanelRegistroMiembro(), OPCION_REGISTRAR_MIEMBRO);
        pnlListaMiembros = new ListaMiembros(nivel);
        pnlContenido.add(pnlListaMiembros, OPCION_LISTA_MIEMBROS);
        pnlContenido.add(new ListaEntrenadores(), OPCION_ENTRENADORES);
        pnlContenido.add(new PanelMembresias(), OPCION_MEMBRESIAS);
        pnlContenido.add(new ListaEmpleados(), OPCION_EMPLEADOS);

        distribucionPantallas.show(pnlContenido, OPCION_INICIO);
        return pnlContenido;
    }

    // Eventos

    // Cambia a la pantalla de la opción elegida en el menú y marca su botón
    @Override
    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();

        // Solo los administradores pueden entrar a la sección de empleados
        if (comando.equals(OPCION_EMPLEADOS) && !nivel.equals(NIVEL_ADMINISTRADOR)) {
            JOptionPane.showMessageDialog(this,
                    "Solo los administradores pueden acceder a esta sección.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        distribucionPantallas.show(pnlContenido, comando);

        // La lista de miembros se vuelve a cargar para mostrar los registros recientes
        if (comando.equals(OPCION_LISTA_MIEMBROS)) {
            pnlListaMiembros.recargar();
        }

        marcarBotonActivo((JButton) evento.getSource());
    }

    // Pide confirmación y, si el usuario acepta, regresa a la pantalla de inicio de sesión
    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de cerrar sesión?",
                "Cerrar sesión", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            dispose();
            new InicioSesion();
        }
    }

    // Resalta los botones del menú cuando el cursor pasa sobre ellos,
    // excepto el botón de la pantalla activa
    private class EfectoHoverMenu extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent evento) {
            JButton btnOpcion = (JButton) evento.getSource();
            if (btnOpcion != btnActivo) {
                btnOpcion.setBackground(Estilos.GRIS_OSCURO);
                btnOpcion.setForeground(Color.WHITE);
            }
        }

        @Override
        public void mouseExited(MouseEvent evento) {
            JButton btnOpcion = (JButton) evento.getSource();
            if (btnOpcion != btnActivo) {
                btnOpcion.setBackground(Color.BLACK);
                btnOpcion.setForeground(Estilos.GRIS_TEXTO);
            }
        }
    }

    // Acceso a datos

    // Cierra la conexión con la base de datos antes de cerrar la ventana
    private void cerrarConexion() {
        try {
            Connection conexion = ConexionBD.conectar();
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cerrar la conexión con la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Métodos auxiliares

    // Devuelve el botón anterior a su estilo normal y resalta el botón seleccionado
    private void marcarBotonActivo(JButton btnSeleccionado) {
        if (btnActivo != null) {
            btnActivo.setBackground(FONDO_BOTON_INACTIVO);
            btnActivo.setForeground(Estilos.GRIS_TEXTO);
            btnActivo.setFont(FUENTE_MENU);
        }

        btnActivo = btnSeleccionado;
        btnActivo.setBackground(Estilos.AMARILLO);
        btnActivo.setForeground(Color.BLACK);
        btnActivo.setFont(FUENTE_MENU_ACTIVO);
    }
}