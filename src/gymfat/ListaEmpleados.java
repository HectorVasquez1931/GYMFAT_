// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - ListaEmpleados
// Descripción: Muestra la tabla de empleados con sus datos de contacto y puesto.
// Permite buscar por nombre, agregar empleados y darles un usuario del sistema.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

// Pantalla con la lista de empleados del gimnasio (solo para administradores)
public class ListaEmpleados extends JPanel implements ActionListener {

    // Constantes

    // Comandos de los botones y del campo de búsqueda
    private static final String COMANDO_BUSCAR = "Buscar";
    private static final String COMANDO_MOSTRAR_TODOS = "Mostrar todos";
    private static final String COMANDO_AGREGAR_EMPLEADO = "Agregar empleado";
    private static final String COMANDO_AGREGAR_USUARIO = "Agregar usuario al sistema";

    // Posición de las columnas que se leen desde el código
    private static final int COLUMNA_ID = 0;
    private static final int COLUMNA_NOMBRE = 1;
    private static final int COLUMNA_APELLIDO_PATERNO = 2;

    // Valor que devuelve JTable cuando no hay ninguna fila seleccionada
    private static final int SIN_SELECCION = -1;

    // Tamaños de los componentes, en píxeles
    private static final int ALTO_FILA = 32;
    private static final int ALTO_ENCABEZADO_TABLA = 36;
    private static final int ANCHO_MAXIMO_COLUMNA_ID = 50;
    private static final int ANCHO_BOTON = 200;
    private static final int ALTO_BOTON = 30;
    private static final int COLUMNAS_CAMPO_BUSCAR = 30;

    // Fuentes de esta pantalla
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 28);
    private static final Font FUENTE_TEXTO = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_ENCABEZADO_TABLA = new Font(Estilos.FUENTE, Font.BOLD, 13);

    // Atributos y componentes de la interfaz

    private JTextField txtBuscar;
    private JTable tblEmpleados;
    private DefaultTableModel modeloEmpleados;

    // Constructor

    // Crea la pantalla y carga todos los empleados
    public ListaEmpleados() {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO_LISTA);
        construirInterfaz();
        cargarEmpleados("");
    }

    // Construcción de la interfaz

    // Coloca el título y la búsqueda arriba, la tabla al centro y los botones abajo
    private void construirInterfaz() {
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearPiePagina(), BorderLayout.SOUTH);
    }

    // Crea el título de la pantalla y la barra de búsqueda
    private JPanel crearEncabezado() {
        JLabel lblTitulo = new JLabel("Lista de empleados");
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

    // Crea la tabla de empleados de solo lectura dentro de un panel con scroll
    private JScrollPane crearTabla() {
        String[] columnas = {"ID", "Nombre", "Apellido Paterno", "Apellido Materno",
            "Correo", "Telefono", "Puesto"};
        modeloEmpleados = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblEmpleados = new JTable(modeloEmpleados);
        tblEmpleados.setRowHeight(ALTO_FILA);
        tblEmpleados.setFont(FUENTE_TEXTO);
        tblEmpleados.setShowVerticalLines(false);
        tblEmpleados.setGridColor(Estilos.GRIS_CUADRICULA);
        tblEmpleados.setSelectionBackground(Estilos.AMARILLO_SELECCION);
        tblEmpleados.setSelectionForeground(Color.BLACK);
        tblEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEmpleados.setIntercellSpacing(new Dimension(0, 1));
        tblEmpleados.setFocusable(false);

        configurarEncabezadoTabla();
        configurarColumnaId();
        tblEmpleados.setDefaultRenderer(Object.class, new RenderizadorFilas());

        return new JScrollPane(tblEmpleados);
    }

    // Da formato a los títulos de las columnas
    private void configurarEncabezadoTabla() {
        JTableHeader encabezadoTabla = tblEmpleados.getTableHeader();
        encabezadoTabla.setFont(FUENTE_ENCABEZADO_TABLA);
        encabezadoTabla.setBackground(Color.WHITE);
        encabezadoTabla.setForeground(Estilos.GRIS_TEXTO_ENCABEZADO);
        encabezadoTabla.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Estilos.AMARILLO));
        encabezadoTabla.setPreferredSize(new Dimension(0, ALTO_ENCABEZADO_TABLA));
    }

    // Centra el ID y limita el ancho de su columna
    private void configurarColumnaId() {
        DefaultTableCellRenderer renderizadorCentrado = new DefaultTableCellRenderer();
        renderizadorCentrado.setHorizontalAlignment(SwingConstants.CENTER);
        tblEmpleados.getColumnModel().getColumn(COLUMNA_ID).setCellRenderer(renderizadorCentrado);
        tblEmpleados.getColumnModel().getColumn(COLUMNA_ID).setMaxWidth(ANCHO_MAXIMO_COLUMNA_ID);
    }

    // Crea la barra inferior con los botones para agregar empleados y usuarios
    private JPanel crearPiePagina() {
        JPanel pnlPiePagina = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlPiePagina.setBackground(Color.WHITE);
        pnlPiePagina.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.GRIS_BORDE));

        pnlPiePagina.add(crearBoton(COMANDO_AGREGAR_EMPLEADO, Estilos.AMARILLO));
        pnlPiePagina.add(crearBoton(COMANDO_AGREGAR_USUARIO, Estilos.AMARILLO));
        return pnlPiePagina;
    }

    // Eventos

    // Ejecuta la acción del botón presionado
    @Override
    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();

        if (comando.equals(COMANDO_BUSCAR)) {
            cargarEmpleados(txtBuscar.getText().trim());
        }
        if (comando.equals(COMANDO_MOSTRAR_TODOS)) {
            txtBuscar.setText("");
            cargarEmpleados("");
        }
        if (comando.equals(COMANDO_AGREGAR_EMPLEADO)) {
            agregarEmpleado();
        }
        if (comando.equals(COMANDO_AGREGAR_USUARIO)) {
            agregarUsuario();
        }
    }

    // Abre el diálogo para registrar un empleado y, si se guardó, recarga la tabla
    private void agregarEmpleado() {
        AddEmployee dlgAgregarEmpleado = new AddEmployee(SwingUtilities.getWindowAncestor(this));
        if (dlgAgregarEmpleado.saved) {
            cargarEmpleados("");
        }
    }

    // Abre el diálogo para crear el usuario del sistema del empleado seleccionado
    private void agregarUsuario() {
        int fila = tblEmpleados.getSelectedRow();
        if (fila == SIN_SELECCION) {
            JOptionPane.showMessageDialog(this, "Selecciona un empleado primero.");
            return;
        }
        int idEmpleado = (int) modeloEmpleados.getValueAt(fila, COLUMNA_ID);
        String nombreEmpleado = modeloEmpleados.getValueAt(fila, COLUMNA_NOMBRE) + " "
                + modeloEmpleados.getValueAt(fila, COLUMNA_APELLIDO_PATERNO);
        new AddUser(SwingUtilities.getWindowAncestor(this), idEmpleado, nombreEmpleado);
    }

    // Acceso a datos

    // Llena la tabla con los empleados cuyo nombre contiene el filtro
    private void cargarEmpleados(String filtro) {
        modeloEmpleados.setRowCount(0);

        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        // Obtiene los datos de cada empleado; los datos opcionales vacíos se muestran en blanco
        String consulta = "SELECT id_empleado, nombres, apellido_paterno, "
                + "ISNULL(apellido_materno, '') AS apellido_materno, "
                + "ISNULL(correo_electronico, '') AS correo, "
                + "ISNULL(telefono, '') AS telefono, "
                + "puesto "
                + "FROM empleado "
                + "WHERE nombres + ' ' + apellido_paterno LIKE ? "
                + "ORDER BY id_empleado";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, "%" + filtro + "%");
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    modeloEmpleados.addRow(new Object[]{
                        resultado.getInt("id_empleado"),
                        resultado.getString("nombres"),
                        resultado.getString("apellido_paterno"),
                        resultado.getString("apellido_materno"),
                        resultado.getString("correo"),
                        resultado.getString("telefono"),
                        resultado.getString("puesto")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los empleados.",
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

    // Alterna el fondo de las filas entre blanco y gris claro.
    // Las filas seleccionadas conservan el color de selección.
    private static class RenderizadorFilas extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor,
                boolean esSeleccionada, boolean tieneFoco, int fila, int columna) {
            Component celda = super.getTableCellRendererComponent(tabla, valor,
                    esSeleccionada, tieneFoco, fila, columna);
            if (!esSeleccionada) {
                celda.setBackground(fila % 2 == 0 ? Color.WHITE : Estilos.GRIS_FILA);
                celda.setForeground(Estilos.GRIS_TEXTO_FILA);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return celda;
        }
    }
}