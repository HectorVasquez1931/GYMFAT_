/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gymfat;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.SQLException;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author SoaringLatitute2
 */
public class MainWindow extends JFrame implements ActionListener, MouseListener, WindowListener {

    String text_buttons[] = {"Inicio", "Registrar Miembro", "Lista de miembros", "Membresias", "Entrenadores", "Empleados"};
    CardLayout card_layout = new CardLayout();
    JPanel content_panel = new JPanel();
    JButton active_btn = new JButton();
    String level;
    String username;

    public MainWindow(String level, String username) {
        super("GymFat");

        this.level = level;
        this.username = username;
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        setLocationRelativeTo(null);
        addWindowListener(this);

        setVisible(true);

    }

    private void initComponents() {
        add(createSideBar(), BorderLayout.WEST);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createSideBar() {
        //Creation of side bar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(Color.BLACK);

        //northpanel("GYMFAT")
        JPanel north_panel = new JPanel();
        north_panel.setLayout(new BoxLayout(north_panel, BoxLayout.Y_AXIS));
        north_panel.setBackground(Color.black);
        JPanel title_panel = new JPanel();
        title_panel.setBackground(Color.black);
        title_panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        JLabel title = new JLabel("GymFat", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setMaximumSize(new Dimension(200, 40));
        title.setForeground(new Color(255, 220, 0));
        title_panel.add(title);
        north_panel.add(title_panel);

        JPanel separator = new JPanel();
        separator.setBackground(new Color(60, 60, 60));
        separator.setMaximumSize(new Dimension(160, 1));
        separator.setPreferredSize(new Dimension(160, 1));
        separator.setMinimumSize(new Dimension(160, 1));

        north_panel.add(separator);

        sidebar.add(north_panel, BorderLayout.NORTH);

        //Center panel(buttons)
        JPanel buttons_panel = new JPanel();
        buttons_panel.setLayout(new BoxLayout(buttons_panel, BoxLayout.Y_AXIS));
        buttons_panel.setBackground(Color.black);

        //creation of buttons
        for (String text_button : text_buttons) {
            JButton btn = new JButton(text_button);
            btn.setMaximumSize(new Dimension(200, 40));
            btn.setMinimumSize(new Dimension(200, 40));
            btn.setPreferredSize(new Dimension(200, 40));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setForeground(new Color(180, 180, 180));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setBackground(Color.BLACK);
            btn.setFont(new Font("Arial", Font.PLAIN, 13));
            btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            btn.addActionListener(this);
            btn.addMouseListener(this);
            buttons_panel.add(btn);
        }
        sidebar.add(buttons_panel, BorderLayout.CENTER);
        JPanel user_panel = new JPanel();
        user_panel.setLayout(new BoxLayout(user_panel, BoxLayout.Y_AXIS));
        user_panel.setBackground(new Color(15, 15, 15));
        user_panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lbl_user = new JLabel(username);
        lbl_user.setForeground(new Color(180, 180, 180));
        lbl_user.setFont(new Font("Arial", Font.BOLD, 13));
        lbl_user.setAlignmentX(LEFT_ALIGNMENT);
        user_panel.add(lbl_user);

        JLabel lbl_level = new JLabel(level);
        lbl_level.setForeground(new Color(100, 100, 100));
        lbl_level.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl_level.setAlignmentX(LEFT_ALIGNMENT);
        user_panel.add(lbl_level);

        user_panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btn_logout = new JButton("Cerrar sesión");
        btn_logout.setBackground(new Color(40, 40, 40));
        btn_logout.setForeground(new Color(180, 180, 180));
        btn_logout.setFont(new Font("Arial", Font.PLAIN, 12));
        btn_logout.setBorderPainted(false);
        btn_logout.setFocusPainted(false);
        btn_logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btn_logout.setAlignmentX(LEFT_ALIGNMENT);
        btn_logout.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btn_logout.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de cerrar sesión?",
                    "Cerrar sesión", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Login();
            }
        });
        btn_logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn_logout.setBackground(new Color(180, 50, 50));
                btn_logout.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn_logout.setBackground(new Color(40, 40, 40));
                btn_logout.setForeground(new Color(180, 180, 180));
            }
        });
        user_panel.add(btn_logout);

        sidebar.add(user_panel, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel createContentPanel() {
        content_panel.setLayout(card_layout);
        content_panel.add(new PanelInicio(username), "Inicio");
        content_panel.add(new AddMember(), "Registrar Miembro");
        content_panel.add(new MembersList(level), "Lista de miembros");
        content_panel.add(new TrainersList(), "Entrenadores");
        content_panel.add(new PanelMembresias(), "Membresias");
        content_panel.add(new EmployeesList(level), "Empleados");

        card_layout.show(content_panel, "Inicio");
        return content_panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();

        if (ac.equals("Empleados") && !level.equals("Administrador")) {
            JOptionPane.showMessageDialog(this,
                    "Solo los administradores pueden acceder a esta sección.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        card_layout.show(content_panel, ac);

        if (ac.equals("Lista de miembros")) {
            for (Component component : content_panel.getComponents()) {
                if (component instanceof MembersList) {
                    ((MembersList) component).refresh();
                }
            }
        }

        if (active_btn != null) {
            active_btn.setBackground(new Color(20, 20, 20));
            active_btn.setForeground(new Color(180, 180, 180));
            active_btn.setFont(new Font("Arial", Font.PLAIN, 13));
        }

        active_btn = (JButton) e.getSource();
        active_btn.setBackground(new Color(255, 220, 0));
        active_btn.setForeground(Color.black);
        active_btn.setFont(new Font("Arial", Font.BOLD, 13));

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
        JButton btn = (JButton) e.getSource();
        if (btn != active_btn) {
            btn.setBackground(new Color(40, 40, 40));
            btn.setForeground(Color.white);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JButton btn = (JButton) e.getSource();
        if (btn != active_btn) {
            btn.setBackground(Color.black);
            btn.setForeground(new Color(180, 180, 180));
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        cerrarConexion();
        dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    private void cerrarConexion() {
        try {
            Connection con = ConnectionDB.connect();
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Conexion cerrada correctamente.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al cerrar conexion: " + ex.getMessage());
        }
    }
}
