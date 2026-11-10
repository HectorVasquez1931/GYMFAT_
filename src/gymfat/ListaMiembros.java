// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - ListaMiembros
// Descripción: Muestra la tabla de miembros con su plan y estado. Permite
// buscar por nombre, ver el detalle de un miembro y eliminarlo (solo administradores).
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

// Pantalla con la lista de miembros del gimnasio
public class ListaMiembros extends JPanel implements ActionListener {

    // Constantes

    // Comandos de los botones y del campo de búsqueda
    private static final String COMANDO_BUSCAR = "Buscar";
    private static final String COMANDO_MOSTRAR_TODOS = "Mostrar todos";
    private static final String COMANDO_VER_DETALLE = "Ver detalle";
    private static final String COMANDO_ELIMINAR = "Eliminar";

    // Nivel de usuario que puede eliminar miembros
    private static final String NIVEL_ADMINISTRADOR = "Administrador";

    // Estado de membresía que se resalta en rojo en la tabla
    private static final String ESTADO_VENCIDA = "Vencida";

    // Posición de las columnas que se leen desde el código
    private static final int COLUMNA_ID = 0;
    private static final int COLUMNA_NOMBRE = 1;
    private static final int COLUMNA_ESTADO = 5;

    // Valor que devuelve JTable cuando no hay ninguna fila seleccionada
    private static final int SIN_SELECCION = -1;

    // Número de clics que abre el detalle del miembro
    private static final int CLICS_VER_DETALLE = 2;

    // Tamaños de los componentes, en píxeles
    private static final int ALTO_FILA = 32;
    private static final int ALTO_ENCABEZADO_TABLA = 36;
    private static final int ANCHO_MAXIMO_COLUMNA_ID = 50;
    private static final int ANCHO_BOTON = 120;
    private static final int ALTO_BOTON = 30;
    private static final int COLUMNAS_CAMPO_BUSCAR = 30;

    // Consultas que borran los datos de un miembro. El orden importa:
    // primero los registros que dependen del miembro y al final el miembro.
    private static final String[] CONSULTAS_ELIMINAR_MIEMBRO = {
        "DELETE FROM miembro_clase WHERE id_miembro = ?",
        "DELETE FROM miembro_dia WHERE id_miembro = ?",
        "DELETE FROM visita WHERE id_miembro = ?",
        "DELETE FROM salud WHERE id_miembro = ?",
        "DELETE FROM contacto_emergencia WHERE id_miembro = ?",
        "DELETE FROM membresia WHERE id_miembro = ?",
        "DELETE FROM miembro WHERE id_miembro = ?"
    };

    // Colores propios de esta pantalla; los compartidos con otras listas están en Estilos
    private static final Color ROJO_CLARO = new Color(255, 235, 235);
    private static final Color GRIS_TEXTO_FILA = new Color(40, 40, 40);

    // Fuentes de esta pantalla
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 28);
    private static final Font FUENTE_TEXTO = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_ENCABEZADO_TABLA = new Font(Estilos.FUENTE, Font.BOLD, 13);

    // Atributos y componentes de la interfaz

    // Nivel del usuario en sesión; define si puede eliminar miembros
    private final String nivel;

    private JTextField txtBuscar;
    private JTable tblMiembros;
    private DefaultTableModel modeloMiembros;

    // Constructor

    // Crea la pantalla y carga todos los miembros
    public ListaMiembros(String nivel) {
        this.nivel = nivel;
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO_LISTA);
        construirInterfaz();
        cargarMiembros("");
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
        JLabel lblTitulo = new JLabel("Lista de miembros");
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

    // Crea la tabla de miembros de solo lectura dentro de un panel con scroll
    private JScrollPane crearTabla() {
        String[] columnas = {"ID", "Nombre", "Telefono", "Plan", "Vencimiento", "Estado"};
        modeloMiembros = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblMiembros = new JTable(modeloMiembros);
        tblMiembros.setRowHeight(ALTO_FILA);
        tblMiembros.setFont(FUENTE_TEXTO);
        tblMiembros.setShowVerticalLines(false);
        tblMiembros.setGridColor(Estilos.GRIS_CUADRICULA);
        tblMiembros.setSelectionBackground(Estilos.AMARILLO_SELECCION);
        tblMiembros.setSelectionForeground(Color.BLACK);
        tblMiembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMiembros.setIntercellSpacing(new Dimension(0, 1));
        tblMiembros.setFocusable(false);

        // Doble clic sobre una fila abre el detalle del miembro
        tblMiembros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getClickCount() == CLICS_VER_DETALLE) {
                    verDetalle();
                }
            }
        });

        configurarEncabezadoTabla();
        configurarColumnaId();
        tblMiembros.setDefaultRenderer(Object.class, new RenderizadorFilas());

        return new JScrollPane(tblMiembros);
    }

    // Da formato a los títulos de las columnas
    private void configurarEncabezadoTabla() {
        JTableHeader encabezadoTabla = tblMiembros.getTableHeader();
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
        tblMiembros.getColumnModel().getColumn(COLUMNA_ID).setCellRenderer(renderizadorCentrado);
        tblMiembros.getColumnModel().getColumn(COLUMNA_ID).setMaxWidth(ANCHO_MAXIMO_COLUMNA_ID);
    }

    // Crea la barra inferior con los botones Eliminar y Ver detalle
    private JPanel crearPiePagina() {
        JPanel pnlPiePagina = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlPiePagina.setBackground(Color.WHITE);
        pnlPiePagina.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.GRIS_BORDE));

        pnlPiePagina.add(crearBoton(COMANDO_ELIMINAR, Color.RED));
        pnlPiePagina.add(crearBoton(COMANDO_VER_DETALLE, Estilos.AMARILLO));
        return pnlPiePagina;
    }

    // Eventos

    // Ejecuta la acción del botón presionado
    @Override
    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();

        if (comando.equals(COMANDO_BUSCAR)) {
            cargarMiembros(txtBuscar.getText().trim());
        }
        if (comando.equals(COMANDO_MOSTRAR_TODOS)) {
            txtBuscar.setText("");
            cargarMiembros("");
        }
        if (comando.equals(COMANDO_VER_DETALLE)) {
            verDetalle();
        }
        if (comando.equals(COMANDO_ELIMINAR)) {
            eliminarMiembro();
        }
    }

    // Abre la ventana de detalle del miembro seleccionado
    private void verDetalle() {
        int fila = obtenerFilaSeleccionada();
        if (fila == SIN_SELECCION) {
            return;
        }
        int idMiembro = (int) modeloMiembros.getValueAt(fila, COLUMNA_ID);
        new DetailMember(idMiembro);
    }

    // Elimina al miembro seleccionado después de confirmar. Solo para administradores.
    private void eliminarMiembro() {
        if (!nivel.equals(NIVEL_ADMINISTRADOR)) {
            JOptionPane.showMessageDialog(this,
                    "No tienes permisos para eliminar miembros.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fila = obtenerFilaSeleccionada();
        if (fila == SIN_SELECCION) {
            return;
        }
        int idMiembro = (int) modeloMiembros.getValueAt(fila, COLUMNA_ID);
        String nombreMiembro = (String) modeloMiembros.getValueAt(fila, COLUMNA_NOMBRE);

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar a " + nombreMiembro + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        boolean fueEliminado = borrarMiembro(idMiembro);
        if (fueEliminado) {
            cargarMiembros("");
            JOptionPane.showMessageDialog(this, "Miembro eliminado correctamente.");
        }
    }

    // Acceso a datos

    // Vuelve a cargar todos los miembros. Lo usa VentanaPrincipal al abrir esta pantalla.
    public void recargar() {
        cargarMiembros("");
    }

    // Llena la tabla con los miembros cuyo nombre contiene el filtro
    private void cargarMiembros(String filtro) {
        modeloMiembros.setRowCount(0);

        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return;
        }

        // Obtiene cada miembro con su teléfono, plan, vencimiento y estado de membresía
        String consulta = "SELECT m.id_miembro, "
                + "m.nombres + ' ' + m.apellido_paterno + ' ' + ISNULL(m.apellido_materno,'') AS nombre_completo, "
                + "m.telefono, "
                + "p.nombre_plan, "
                + "mem.fecha_vencimiento, "
                + "mem.estado "
                + "FROM miembro m "
                + "LEFT JOIN membresia mem ON m.id_miembro = mem.id_miembro "
                + "LEFT JOIN planes p ON mem.id_plan = p.id_plan "
                + "WHERE m.nombres + ' ' + m.apellido_paterno LIKE ? "
                + "ORDER BY m.id_miembro";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, "%" + filtro + "%");
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    modeloMiembros.addRow(new Object[]{
                        resultado.getInt("id_miembro"),
                        resultado.getString("nombre_completo"),
                        resultado.getString("telefono"),
                        resultado.getString("nombre_plan"),
                        resultado.getString("fecha_vencimiento"),
                        resultado.getString("estado")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los miembros.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Borra al miembro y todos sus registros relacionados. Devuelve true si se completó.
    private boolean borrarMiembro(int idMiembro) {
        Connection conexion = ConexionBD.conectar();
        if (conexion == null) {
            return false;
        }

        try {
            // Ejecuta cada borrado en el orden definido en CONSULTAS_ELIMINAR_MIEMBRO
            for (String consulta : CONSULTAS_ELIMINAR_MIEMBRO) {
                try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                    sentencia.setInt(1, idMiembro);
                    sentencia.executeUpdate();
                }
            }
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el miembro.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Métodos auxiliares

    // Devuelve la fila seleccionada; si no hay ninguna, avisa al usuario y devuelve SIN_SELECCION
    private int obtenerFilaSeleccionada() {
        int fila = tblMiembros.getSelectedRow();
        if (fila == SIN_SELECCION) {
            JOptionPane.showMessageDialog(this, "Selecciona un miembro primero.");
        }
        return fila;
    }

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

    // Pinta de rojo claro las filas con membresía vencida y alterna blanco y gris
    // en las demás. Las filas seleccionadas conservan el color de selección.
    private class RenderizadorFilas extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor,
                boolean esSeleccionada, boolean tieneFoco, int fila, int columna) {
            Component celda = super.getTableCellRendererComponent(tabla, valor,
                    esSeleccionada, tieneFoco, fila, columna);
            if (!esSeleccionada) {
                String estado = (String) modeloMiembros.getValueAt(fila, COLUMNA_ESTADO);
                if (ESTADO_VENCIDA.equalsIgnoreCase(estado)) {
                    celda.setBackground(ROJO_CLARO);
                } else {
                    celda.setBackground(fila % 2 == 0 ? Color.WHITE : Estilos.GRIS_FILA);
                }
                celda.setForeground(GRIS_TEXTO_FILA);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return celda;
        }
    }
}