/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gymfat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author SoaringLatitute2
 */
public class AddEmployee extends JDialog implements ActionListener {

    static final Color AMARILLO = new Color(255, 220, 0);
    static final Color NEGRO_CLARO = new Color(17, 17, 17);
    static final Color BORDE = new Color(42, 42, 42);

    JTextField txtf_nombres, txtf_apellido_pat, txtf_apellido_mat;
    JTextField txtf_correo, txtf_telefono, txtf_puesto;
    JLabel lbl_error;
    boolean saved = false;

    public AddEmployee(Window parent) {
        super(parent, "Nuevo empleado", ModalityType.APPLICATION_MODAL);
        setSize(800, 260);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        initComponents();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (ac.equals("Cancelar")) {
            dispose();
        }
        if (ac.equals("Guardar")) {
            save();
        }
    }

    private void initComponents() {
        JPanel left_panel = new JPanel();
        left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));
        left_panel.setBackground(Color.black);
        left_panel.setPreferredSize(new Dimension(180, 0));

        left_panel.add(Box.createVerticalGlue());

        JLabel lbl_title = new JLabel("Nuevo", SwingConstants.CENTER);
        lbl_title.setFont(new Font("Arial", Font.BOLD, 22));
        lbl_title.setForeground(AMARILLO);
        lbl_title.setAlignmentX(CENTER_ALIGNMENT);
        left_panel.add(lbl_title);

        JLabel lbl_title2 = new JLabel("empleado", SwingConstants.CENTER);
        lbl_title2.setFont(new Font("Arial", Font.BOLD, 22));
        lbl_title2.setForeground(AMARILLO);
        lbl_title2.setAlignmentX(CENTER_ALIGNMENT);
        left_panel.add(lbl_title2);

        left_panel.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lbl_subtitle = new JLabel("GymFat", SwingConstants.CENTER);
        lbl_subtitle.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl_subtitle.setForeground(new Color(68, 68, 68));
        lbl_subtitle.setAlignmentX(CENTER_ALIGNMENT);
        left_panel.add(lbl_subtitle);

        left_panel.add(Box.createVerticalGlue());
        add(left_panel, BorderLayout.WEST);

        JPanel right_panel = new JPanel();
        right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.Y_AXIS));
        right_panel.setBackground(NEGRO_CLARO);
        right_panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        right_panel.add(Box.createVerticalGlue());

        JPanel row1 = new JPanel(new GridLayout(1, 3, 12, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row1.add(crearCampo("NOMBRES *", txtf_nombres = new JTextField()));
        row1.add(crearCampo("APELLIDO P. *", txtf_apellido_pat = new JTextField()));
        row1.add(crearCampo("APELLIDO M.", txtf_apellido_mat = new JTextField()));
        right_panel.add(row1);
        right_panel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel row2 = new JPanel(new GridLayout(1, 3, 12, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row2.add(crearCampo("CORREO", txtf_correo = new JTextField()));
        row2.add(crearCampo("TELÉFONO", txtf_telefono = new JTextField()));
        row2.add(crearCampo("PUESTO *", txtf_puesto = new JTextField()));
        right_panel.add(row2);
        right_panel.add(Box.createRigidArea(new Dimension(0, 10)));

        lbl_error = new JLabel(" ");
        lbl_error.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl_error.setForeground(new Color(220, 80, 80));
        lbl_error.setAlignmentX(LEFT_ALIGNMENT);
        right_panel.add(lbl_error);
        right_panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel row_buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        row_buttons.setOpaque(false);
        row_buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btn_cancelar = new JButton("Cancelar");
        btn_cancelar.setBackground(new Color(30, 30, 30));
        btn_cancelar.setForeground(new Color(150, 150, 150));
        btn_cancelar.setFont(new Font("Arial", Font.PLAIN, 13));
        btn_cancelar.setBorderPainted(false);
        btn_cancelar.setFocusPainted(false);
        btn_cancelar.addActionListener(this);
        row_buttons.add(btn_cancelar);

        JButton btn_save = new JButton("Guardar");
        btn_save.setBackground(AMARILLO);
        btn_save.setForeground(Color.black);
        btn_save.setFont(new Font("Arial", Font.BOLD, 13));
        btn_save.setBorderPainted(false);
        btn_save.setFocusPainted(false);
        btn_save.addActionListener(this);
        row_buttons.add(btn_save);

        right_panel.add(row_buttons);
        right_panel.add(Box.createVerticalGlue());

        add(right_panel, BorderLayout.CENTER);

    }

    private JPanel crearCampo(String label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(Color.gray);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));

        field.setBackground(new Color(26, 26, 26));
        field.setForeground(Color.WHITE);
        field.setCaretColor(AMARILLO);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(field);
        return panel;
    }

    private void save() {
        String nombres = txtf_nombres.getText().trim();
        String apellidoP = txtf_apellido_pat.getText().trim();
        String puesto = txtf_puesto.getText().trim();

        if (nombres.isEmpty() || apellidoP.isEmpty() || puesto.isEmpty()) {
            lbl_error.setText("Llena los campos obligatorios (*).");
            return;
        }

        try {
            String sql = "INSERT INTO empleado (nombres, apellido_paterno, apellido_materno, "
                    + "correo_electronico, telefono, puesto, fecha_contratacion) "
                    + "VALUES (?, ?, ?, ?, ?, ?, CAST(GETDATE() AS DATE))";
            PreparedStatement ps = ConnectionDB.connect().prepareStatement(sql);
            ps.setString(1, nombres);
            ps.setString(2, apellidoP);
            ps.setString(3, txtf_apellido_mat.getText().trim().isEmpty() ? null : txtf_apellido_mat.getText().trim());
            ps.setString(4, txtf_correo.getText().trim().isEmpty() ? null : txtf_correo.getText().trim());
            ps.setString(5, txtf_telefono.getText().trim().isEmpty() ? null : txtf_telefono.getText().trim());
            ps.setString(6, puesto);
            ps.executeUpdate();
            saved = true;
            dispose();
        } catch (SQLException ex) {
            lbl_error.setText("Error: " + ex.getMessage());
        }
    }
}
