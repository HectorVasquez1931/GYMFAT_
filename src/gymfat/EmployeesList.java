/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gymfat;

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
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author SoaringLatitute2
 */
public class EmployeesList extends JPanel implements ActionListener, MouseListener {

    DefaultTableModel model;
    JTextField txt_field;
    JTable table;
    String level;

    static final Color AMARILLO = new Color(255, 220, 0);
    static final Color FONDO = new Color(248, 248, 248);
    static final Color GRIS_FILA = new Color(252, 252, 252);
    static final Color ROJO_CLARO = new Color(255, 235, 235);

    public EmployeesList(String level) {
        this.level = level;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        JLabel title = new JLabel("Lista de empleados");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        //search
        JPanel search_panel = new JPanel();
        search_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        search_panel.setBackground(FONDO);

        //label search
        JLabel txt = new JLabel("Buscar");
        txt.setFont(new Font("Arial", Font.PLAIN, 13));
        search_panel.add(txt);

        //txrfield search
        txt_field = new JTextField(30);
        txt_field.setFont(new Font("Arial", Font.PLAIN, 13));
        txt_field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        txt_field.setActionCommand("Buscar");
        txt_field.addActionListener(this);
        search_panel.add(txt_field);

        //button search and show all
        JButton btn_search = new JButton("Buscar");
        btn_search = configButton(btn_search);

        btn_search.setBackground(AMARILLO);
        btn_search.setForeground(Color.BLACK);
        JButton btn_show_all = new JButton("Mostrar todos");
        btn_show_all = configButton(btn_show_all);

        btn_show_all.setBackground(new Color(240, 240, 240));
        btn_show_all.setForeground(Color.BLACK);
        search_panel.add(btn_search);
        search_panel.add(btn_show_all);
        btn_search.addActionListener(this);
        btn_show_all.addActionListener(this);

        //north panel (title)
        JPanel north_panel = new JPanel();
        north_panel.setLayout(new BorderLayout());
        north_panel.setBackground(Color.WHITE);
        north_panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        north_panel.add(title, BorderLayout.NORTH);
        north_panel.add(search_panel, BorderLayout.CENTER);
        add(north_panel, BorderLayout.NORTH);

        //model
        String columns[] = {"ID", "Nombre", "Apellido Paterno", "Apellido Materno", "Correo", "Telefono", "Puesto"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        //table
        table = new JTable(model);

        table.addMouseListener(
                this);
        table.setRowHeight(
                32);
        table.setFont(
                new Font("Arial", Font.PLAIN, 13));
        table.setShowVerticalLines(
                false);
        table.setGridColor(
                new Color(235, 235, 235));
        table.setSelectionBackground(
                new Color(255, 240, 150));
        table.setSelectionForeground(Color.BLACK);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setIntercellSpacing(
                new Dimension(0, 1));
        table.setFocusable(
                false);

        JTableHeader header = new JTableHeader();
        header = table.getTableHeader();

        header.setFont(
                new Font("Arial", Font.BOLD, 13));
        header.setBackground(Color.WHITE);

        header.setForeground(
                new Color(80, 80, 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AMARILLO));
        header.setPreferredSize(
                new Dimension(0, 36));

        DefaultTableCellRenderer center_renderer = new DefaultTableCellRenderer();

        center_renderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel()
                .getColumn(0).setCellRenderer(center_renderer);
        table.getColumnModel().getColumn(0).setCellRenderer(center_renderer);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        //paint rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : GRIS_FILA);
                    c.setForeground(new Color(40, 40, 40));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        //footer
        JPanel footer = new JPanel();
        footer.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btn_add_employee = new JButton("Agregar empleado");
        btn_add_employee.setBackground(AMARILLO);
        btn_add_employee.setForeground(Color.BLACK);
        btn_add_employee.addActionListener(this);
        btn_add_employee = configButton(btn_add_employee);
        footer.add(btn_add_employee);

        JButton btn_add_user = new JButton("Agregar usuario al sistema");
        btn_add_user.setBackground(AMARILLO);
        btn_add_user.setForeground(Color.BLACK);
        btn_add_user.addActionListener(this);
        btn_add_user = configButton(btn_add_user);
        footer.add(btn_add_user);
        add(footer, BorderLayout.SOUTH);

        loadEmployees("");
    }

    private void loadEmployees(String filter) {
        try {
            String sql = "SELECT id_empleado, nombres, apellido_paterno, "
                    + "ISNULL(apellido_materno, '') AS apellido_materno, "
                    + "ISNULL(correo_electronico, '') AS correo, "
                    + "ISNULL(telefono, '') AS telefono, "
                    + "puesto "
                    + "FROM empleado "
                    + "WHERE nombres + ' ' + apellido_paterno LIKE ? "
                    + "ORDER BY id_empleado";

            PreparedStatement ps = ConnectionDB.connect().prepareStatement(sql);
            ps.setString(1, "%" + filter + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_empleado"),
                    rs.getString("nombres"),
                    rs.getString("apellido_paterno"),
                    rs.getString("apellido_materno"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("puesto")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (ac.equals("Buscar")) {
            model.setRowCount(0);
            loadEmployees(txt_field.getText().trim());
        }
        if (ac.equals("Mostrar todos")) {
            txt_field.setText("");
            model.setRowCount(0);
            loadEmployees("");
        }
        if (ac.equals("Agregar empleado")) {
            AddEmployee dialog = new AddEmployee(SwingUtilities.getWindowAncestor(this));
            if (dialog.saved) {
                model.setRowCount(0);
                loadEmployees("");
            }
        }
        if (ac.equals("Agregar usuario al sistema")) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un empleado primero.");
                return;
            }
            int idEmpleado = (int) model.getValueAt(row, 0);
            String nombre = model.getValueAt(row, 1) + " " + model.getValueAt(row, 2);
            new AddUser(SwingUtilities.getWindowAncestor(this), idEmpleado, nombre);

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void seeDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un miembro primero.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        DetailMember detailMember = new DetailMember(id);
    }

    private JButton configButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 30));
        return btn;

    }

    private void deleteMember() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un miembro primero.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String nombre = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar a " + nombre + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            String[] sqls = {
                "DELETE FROM miembro_clase WHERE id_miembro = ?",
                "DELETE FROM miembro_dia WHERE id_miembro = ?",
                "DELETE FROM visita WHERE id_miembro = ?",
                "DELETE FROM salud WHERE id_miembro = ?",
                "DELETE FROM contacto_emergencia WHERE id_miembro = ?",
                "DELETE FROM membresia WHERE id_miembro = ?",
                "DELETE FROM miembro WHERE id_miembro = ?"
            };
            for (String sql : sqls) {
                PreparedStatement ps = ConnectionDB.connect().prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            model.setRowCount(0);
            loadEmployees("");
            JOptionPane.showMessageDialog(this, "Miembro eliminado correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        model.setRowCount(0);
        loadEmployees("");
    }
}
