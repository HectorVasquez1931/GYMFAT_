// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - ListaEntrenadores
// Descripción: Muestra la tabla de entrenadores con su especialidad y el
// número de miembros asignados. Permite buscar por nombre y agregar entrenadores.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

// Pantalla con la lista de entrenadores del gimnasio
public class ListaEntrenadores extends JPanel implements ActionListener {

    // Constantes

    // Comandos de los botones y del campo de búsqueda
    private static final String COMANDO_BUSCAR = "Buscar";
    private static final String COMANDO_MOSTRAR_TODOS = "Mostrar todos";
    private static final String COMANDO_AGREGAR = "Agregar";

    // Posición de las columnas a las que se les da formato
    private static final int COLUMNA_ID = 0;
    private static final int COLUMNA_MIEMBROS_ASIGNADOS = 3;

    // Tamaños de los componentes, en píxeles
    private static final int ALTO_FILA = 32;
    private static final int ALTO_ENCABEZADO_TABLA = 36;
    private static final int ANCHO_MAXIMO_COLUMNA_ID = 50;
    private static final int ANCHO_BOTON = 120;
    private static final int ALTO_BOTON = 30;
    private static final int COLUMNAS_CAMPO_BUSCAR = 30;

    // Fuentes de esta pantalla
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 28);
    private static final Font FUENTE_TEXTO = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_ENCABEZADO_TABLA = new Font(Estilos.FUENTE, Font.BOLD, 13);

    // Atributos y componentes de la interfaz

    private JTextField txtBuscar;
    private JTable tblEntrenadores;
    private DefaultTableModel modeloEntrenadores;

    // Constructor

    // Crea la pantalla y carga todos los entrenadores
    public ListaEntrenadores() {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO_LISTA);
        construirInterfaz();
        cargarEntrenadores("");
    }

    // Construcción de la interfaz

    // Coloca el título y la búsqueda arriba, la tabla al centro y el botón Agregar abajo
    private void construirInterfaz() {
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearPiePagina(), BorderLayout.SOUTH);
    }

    // Crea el título de la pantalla y la barra de búsqueda
    private JPanel crearEncabezado() {
        JLabel lblTitulo = new JLabel("Lista de entrenadores");
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel pnlEncabezado = new JPanel(new BorderLayout());
        pnlEncabezado.setBackground(Color.WHITE);
        pnlEncabezado.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Estilos.GRIS_BORDE));
        pnlEncabezado.add(lblTitulo, BorderLayout.NORTH);
        pnlEncabezado.add(crearPanelBusqueda(), BorderLayout.CENTER);
        return pnlEncabezado;
    }

    // Crea el campo de búsqueda con los botones Buscar y Mostrar todos
    private JPanel crearPanelBusqueda() {
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlBusqueda.setBackground(Estilos.FONDO_LISTA);

        JLabel lblBuscar = new JLabel("Buscar");
        lblBuscar.setFont(FUENTE_TEXTO);
        pnlBusqueda.add(lblBuscar);

        // Al presionar Enter en el campo se ejecuta la misma acción que el botón Buscar
        txtBuscar = new JTextField(COLUMNAS_CAMPO_BUSCAR);
        txtBuscar.setFont(FUENTE_TEXTO);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.GRIS_BORDE_CAMPO, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtBuscar.setActionCommand(COMANDO_BUSCAR);
        txtBuscar.addActionListener(this);
        pnlBusqueda.add(txtBuscar);

        pnlBusqueda.add(crearBoton(COMANDO_BUSCAR, Estilos.AMARILLO));
        pnlBusqueda.add(crearBoton(COMANDO_MOSTRAR_TODOS, Estilos.FONDO_BOTON_SECUNDARIO));
        return pnlBusqueda;
    }

    // Crea la tabla de entrenadores de solo lectura dentro de un panel con scroll
    private JScrollPane crearTabla() {
        String[] columnas = {"ID", "Nombre", "Especialidad", "Miembros asignados"};
        modeloEntrenadores = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblEntrenadores = new JTable(modeloEntrenadores);
        tblEntrenadores.setRowHeight(ALTO_FILA);
        tblEntrenadores.setFont(FUENTE_TEXTO);
        tblEntrenadores.setShowVerticalLines(false);
        tblEntrenadores.setGridColor(Estilos.GRIS_CUADRICULA);
        tblEntrenadores.setSelectionBackground(Estilos.AMARILLO_SELECCION);
        tblEntrenadores.setSelectionForeground(Color.BLACK);
        tblEntrenadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEntrenadores.setIntercellSpacing(new Dimension(0, 1));
        tblEntrenadores.setFocusable(false);

        configurarEncabezadoTabla();
        configurarColumnasCentradas();

        return new JScrollPane(tblEntrenadores);
    }

    // Da formato a los títulos de las columnas
    private void configurarEncabezadoTabla() {
        JTableHeader encabezadoTabla = tblEntrenadores.getTableHeader();
        encabezadoTabla.setFont(FUENTE_ENCABEZADO_TABLA);
        encabezadoTabla.setBackground(Color.WHITE);
        encabezadoTabla.setForeground(Estilos.GRIS_TEXTO_ENCABEZADO);
        encabezadoTabla.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Estilos.AMARILLO));
        encabezadoTabla.setPreferredSize(new Dimension(0, ALTO_ENCABEZADO_TABLA));
    }

    // Centra el ID y los miembros asignados, y limita el ancho de la columna del ID
    private void configurarColumnasCentradas() {
        DefaultTableCellRenderer renderizadorCentrado = new DefaultTableCellRenderer();
        renderizadorCentrado.setHorizontalAlignment(SwingConstants.CENTER);
        tblEntrenadores.getColumnModel().getColumn(COLUMNA_ID).setCellRenderer(renderizadorCentrado);
        tblEntrenadores.getColumnModel().getColumn(COLUMNA_ID).setMaxWidth(ANCHO_MAXIMO_COLUMNA_ID);
        tblEntrenadores.getColumnModel().getColumn(COLUMNA_MIEMBROS_ASIGNADOS).setCellRenderer(renderizadorCentrado);
    }

    // Crea la barra inferior con el botón Agregar
    private JPanel crearPiePagina() {
        JPanel pnlPiePagina = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlPiePagina.setBackground(Color.WHITE);
        pnlPiePagina.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.GRIS_BORDE));
        pnlPiePagina.add(crearBoton(COMANDO_AGREGAR, Estilos.AMARILLO));
        return pnlPiePagina;
    }

    // Eventos

    // Ejecuta la acción del botón presionado
    @Override
    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();

        if (comando.equals(COMANDO_BUSCAR)) {
            cargarEntrenadores(txtBuscar.getText().trim());
        }
        if (comando.equals(COMANDO_MOSTRAR_TODOS)) {
            txtBuscar.setText("");
            cargarEntrenadores("");
        }
        if (comando.equals(COMANDO_AGREGAR)) {
            agregarEntrenador();
        }
    }

    // Abre el diálogo para registrar un entrenador y, si se guardó, recarga la tabla
    private void agregarEntrenador() {
        DialogoAgregarEntrenador dlgAgregarEntrenador =
                new DialogoAgregarEntrenador(SwingUtilities.getWindowAncestor(this));
        if (dlgAgregarEntrenador.isFueGuardado()) {
            cargarEntrenadores("");
        }
    }

    // Acceso a datos

    // Llena la tabla con los entrenadores cuyo nombre contiene el filtro
    private void cargarEntrenadores(String filtro) {
        modeloEntrenadores.setRowCount(0);

        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        // Obtiene cada entrenador con su especialidad y cuántos miembros tiene asignados
        String consulta = "SELECT e.id_entrenador, e.nombre, e.especialidad, "
                + "COUNT(m.id_miembro) AS miembros_asignados "
                + "FROM entrenador e "
                + "LEFT JOIN miembro m ON e.id_entrenador = m.id_entrenador "
                + "WHERE e.nombre LIKE ? "
                + "GROUP BY e.id_entrenador, e.nombre, e.especialidad "
                + "ORDER BY e.id_entrenador";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, "%" + filtro + "%");
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    modeloEntrenadores.addRow(new Object[]{
                        resultado.getInt("id_entrenador"),
                        resultado.getString("nombre"),
                        resultado.getString("especialidad"),
                        resultado.getInt("miembros_asignados")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los entrenadores.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Métodos auxiliares

    // Crea un botón con el estilo de esta pantalla; su texto también es su comando
    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btnAccion = new JButton(texto);
        btnAccion.setBackground(colorFondo);
        btnAccion.setForeground(Color.BLACK);
        btnAccion.setFont(FUENTE_TEXTO);
        btnAccion.setBorderPainted(false);
        btnAccion.setFocusPainted(false);
        btnAccion.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON));
        btnAccion.addActionListener(this);
        return btnAccion;
    }
}