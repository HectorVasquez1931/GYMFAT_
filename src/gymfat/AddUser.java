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
import java.security.MessageDigest;
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
import javax.swing.JComboBox;
import javax.swing.JPasswordField;

/**
 *
 * @author SoaringLatitute2
 */
public class AddUser extends JDialog implements ActionListener {

    static final Color AMARILLO = new Color(255, 220, 0);
    static final Color NEGRO_CLARO = new Color(17, 17, 17);
    static final Color BORDE = new Color(42, 42, 42);

    JTextField txtf_username;
    JPasswordField txtf_password, txtf_confirm;
    JComboBox<String> cmb_nivel;
    JLabel lbl_error;
    boolean saved = false;
    int id_empleado;
    String nombre_empleado;

    public AddUser(Window parent, int id_empleado, String nombre_empleado) {
        super(parent, "Nuevo usuario en el sistema", ModalityType.APPLICATION_MODAL);
        this.id_empleado = id_empleado;
        this.nombre_empleado = nombre_empleado;
        setSize(800, 260);
        setLocationRelativeTo(parent);
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
            try {
                save();
            } catch (Exception ex) {
                System.getLogger(AddUser.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }

    private void initComponents() {
        JPanel left_panel = new JPanel();
        left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));
        left_panel.setBackground(Color.BLACK);
        left_panel.setPreferredSize(new Dimension(180, 0));
        left_panel.add(Box.createVerticalGlue());

        JLabel lbl_title = new JLabel("Nuevo", SwingConstants.CENTER);
        lbl_title.setFont(new Font("Arial", Font.BOLD, 22));
        lbl_title.setForeground(AMARILLO);
        lbl_title.setAlignmentX(CENTER_ALIGNMENT);
        left_panel.add(lbl_title);

        JLabel lbl_title2 = new JLabel("usuario", SwingConstants.CENTER);
        lbl_title2.setFont(new Font("Arial", Font.BOLD, 22));
        lbl_title2.setForeground(AMARILLO);
        lbl_title2.setAlignmentX(CENTER_ALIGNMENT);
        left_panel.add(lbl_title2);

        left_panel.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lbl_emp = new JLabel(nombre_empleado, SwingConstants.CENTER);
        lbl_emp.setFont(new Font("Arial", Font.PLAIN, 10));
        lbl_emp.setForeground(new Color(68, 68, 68));
        lbl_emp.setAlignmentX(CENTER_ALIGNMENT);
        left_panel.add(lbl_emp);

        left_panel.add(Box.createVerticalGlue());
        add(left_panel, BorderLayout.WEST);

        // Panel derecho
        JPanel right_panel = new JPanel();
        right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.Y_AXIS));
        right_panel.setBackground(NEGRO_CLARO);
        right_panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        right_panel.add(Box.createVerticalGlue());

        // Fila 1: username y nivel
        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row1.add(crearCampo("USUARIO *", txtf_username = new JTextField()));

        // Nivel como JComboBox
        JPanel panel_nivel = new JPanel();
        panel_nivel.setLayout(new BoxLayout(panel_nivel, BoxLayout.Y_AXIS));
        panel_nivel.setOpaque(false);
        JLabel lbl_nivel = new JLabel("NIVEL *");
        lbl_nivel.setFont(new Font("Arial", Font.BOLD, 10));
        lbl_nivel.setForeground(Color.gray);
        lbl_nivel.setAlignmentX(LEFT_ALIGNMENT);
        panel_nivel.add(lbl_nivel);
        panel_nivel.add(Box.createRigidArea(new Dimension(0, 4)));
        cmb_nivel = new JComboBox<>(new String[]{"Recepcionista", "Administrador"});
        cmb_nivel.setBackground(new Color(26, 26, 26));
        cmb_nivel.setForeground(Color.WHITE);
        cmb_nivel.setFont(new Font("Arial", Font.PLAIN, 13));
        cmb_nivel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmb_nivel.setAlignmentX(LEFT_ALIGNMENT);
        panel_nivel.add(cmb_nivel);
        row1.add(panel_nivel);

        right_panel.add(row1);
        right_panel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Fila 2: password y confirmar
        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row2.add(crearCampoPassword("CONTRASEÑA *", txtf_password = new JPasswordField()));
        row2.add(crearCampoPassword("CONFIRMAR CONTRASEÑA *", txtf_confirm = new JPasswordField()));
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
        btn_save.setForeground(Color.BLACK);
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

    private JPanel crearCampoPassword(String label, JPasswordField field) {
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

    private void save() throws Exception {
        String username = txtf_username.getText().trim();
        String pass = new String(txtf_password.getPassword());
        String confirm = new String(txtf_confirm.getPassword());

        if (username.isEmpty() || pass.isEmpty()) {
            lbl_error.setText("Todos los campos son obligatorios.");
            return;
        }
        if (!pass.equals(confirm)) {
            lbl_error.setText("Las contraseñas no coinciden.");
            return;
        }
        if (pass.length() < 4) {
            lbl_error.setText("La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        try {
            String hashed = hashPassword(pass);
            String nivel = (String) cmb_nivel.getSelectedItem();
            String sql = "INSERT INTO usuario (username, password, nivel, id_empleado) "
                    + "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = ConnectionDB.connect().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, hashed);
            ps.setString(3, nivel);
            ps.setInt(4, id_empleado);
            ps.executeUpdate();
            saved = true;
            dispose();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("UNIQUE") || ex.getMessage().contains("unique")) {
                lbl_error.setText("Ese nombre de usuario ya existe.");
            } else {
                lbl_error.setText("Error: " + ex.getMessage());
            }
        }
    }

    private String hashPassword(String pass) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(pass.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
