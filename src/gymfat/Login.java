/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gymfat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

/**
 *
 * @author SoaringLatitute2
 */
public class Login extends JFrame implements ActionListener {

    static final Color AMARILLO = new Color(255, 220, 0);
    static final Color NEGRO_CLARO = new Color(17, 17, 17);
    static final Color BORDE = new Color(42, 42, 42);

    JButton btn_login;
    JTextField txtf_user;
    JPasswordField txtf_passwd;

    public Login() {
        super("GymFat");
        setSize(700, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        initComponents();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            login();
        } catch (Exception ex) {
            System.getLogger(Login.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void initComponents() {
        JPanel left_panel = new JPanel();
        left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));
        left_panel.setBackground(Color.BLACK);
        left_panel.setPreferredSize(new Dimension(300, 0));

        JLabel title = new JLabel("GymFat", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(AMARILLO);
        title.setAlignmentX(CENTER_ALIGNMENT);
        left_panel.add(Box.createVerticalGlue());
        left_panel.add(title);

        JLabel subtitle = new JLabel("Sistema de gestión", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.BOLD, 10));
        subtitle.setForeground(new Color(68, 68, 68));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        left_panel.add(subtitle);
        left_panel.add(Box.createVerticalGlue());

        add(left_panel, BorderLayout.WEST);

        JPanel right_panel = new JPanel();
        right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.Y_AXIS));
        right_panel.setBackground(NEGRO_CLARO);

        right_panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        right_panel.setPreferredSize(new Dimension(386, 0));
        right_panel.add(Box.createVerticalGlue());

        JLabel lbl_user = new JLabel("USUARIO");
        lbl_user.setFont(new Font("Arial", Font.BOLD, 10));
        lbl_user.setForeground(Color.gray);
        lbl_user.setAlignmentX(LEFT_ALIGNMENT);
        right_panel.add(lbl_user);

        txtf_user = new JTextField();
        txtf_user.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtf_user.setBackground(new Color(26, 26, 26));
        txtf_user.setForeground(Color.white);
        txtf_user.setFont(new Font("Arial", Font.PLAIN, 12));
        txtf_user.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        txtf_user.setAlignmentX(LEFT_ALIGNMENT);
        right_panel.add(txtf_user);
        right_panel.add(Box.createRigidArea(new Dimension(0, 14)));

        JLabel lbl_passwd = new JLabel("CONTRASEÑA");
        lbl_passwd.setFont(new Font("Arial", Font.BOLD, 10));
        lbl_passwd.setForeground(Color.gray);
        lbl_passwd.setAlignmentX(LEFT_ALIGNMENT);
        right_panel.add(lbl_passwd);

        txtf_passwd = new JPasswordField();
        txtf_passwd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtf_passwd.setBackground(new Color(26, 26, 26));
        txtf_passwd.setForeground(Color.white);
        txtf_passwd.setFont(new Font("Arial", Font.PLAIN, 12));
        txtf_passwd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        txtf_passwd.setAlignmentX(LEFT_ALIGNMENT);
        txtf_passwd.setActionCommand("Login");
        right_panel.add(txtf_passwd);
        right_panel.add(Box.createRigidArea(new Dimension(0, 20)));

        btn_login = new JButton("Entrar");
        btn_login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn_login.setBackground(AMARILLO);
        btn_login.setForeground(Color.black);
        btn_login.setFont(new Font("Arial", Font.BOLD, 14));
        btn_login.setBorderPainted(false);
        btn_login.setFocusPainted(false);
        btn_login.addActionListener(this);
        btn_login.setAlignmentX(LEFT_ALIGNMENT);

        right_panel.add(btn_login);

        right_panel.add(Box.createVerticalGlue());
        add(right_panel, BorderLayout.EAST);

    }

    private void login() throws Exception {
        String user = txtf_user.getText().trim();
        String passwd = new String(txtf_passwd.getPassword()).trim();

        if (user.isEmpty() || passwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa usuario y contraseña.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String hashed = hashPassword(passwd);
            String sql = "SELECT u.nivel, e.nombres, e.apellido_paterno "
                    + "FROM usuario u "
                    + "JOIN empleado e ON u.id_empleado = e.id_empleado "
                    + "WHERE u.username = ? AND u.password = ?";
            PreparedStatement ps = ConnectionDB.connect().prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, hashed);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nivel = rs.getString("nivel");
                String nombre = rs.getString("nombres") + " " + rs.getString("apellido_paterno");
                dispose();
                new MainWindow(nivel, nombre);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                txtf_passwd.setText("");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String passwd) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(passwd.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }

}
