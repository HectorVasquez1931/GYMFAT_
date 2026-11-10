/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gymfat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 *
 * @author SoaringLatitute2
 */
public class DetailMember extends JDialog {

    int id_member;

    public DetailMember(int id_member) {
        this.id_member = id_member;
        setLayout(new BorderLayout());
        setSize(600, 670);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(Color.LIGHT_GRAY);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            String sqlMiembro = "SELECT m.*, e.nombre AS entrenador "
                    + "FROM miembro m "
                    + "LEFT JOIN entrenador e ON m.id_entrenador = e.id_entrenador "
                    + "WHERE m.id_miembro = ?";

            PreparedStatement ps = ConnectionDB.connect().prepareStatement(sqlMiembro);
            ps.setInt(1, id_member);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JPanel personal_data_section = CreateSection("Datos personales");
                personal_data_section.add(CreateRow("Nombre", rs.getString("nombres") + " "
                        + rs.getString("apellido_paterno") + " "
                        + (rs.getString("apellido_materno") != null ? rs.getString("apellido_materno") : "")));
                personal_data_section.add(CreateRow("Fecha de nacimiento", rs.getString("fecha_nacimiento")));
                personal_data_section.add(CreateRow("Correo", rs.getString("correo_electronico")));
                personal_data_section.add(CreateRow("Teléfono", rs.getString("telefono")));
                personal_data_section.add(CreateRow("Objetivo", rs.getString("objetivo_principal")));
                personal_data_section.add(CreateRow("Horario", rs.getString("horario_deseado")));
                personal_data_section.add(CreateRow("Entrenador", rs.getString("entrenador") != null
                        ? rs.getString("entrenador") : "Sin entrenador"));

                content.add(personal_data_section);
            }

            String sql_contact = "SELECT * FROM contacto_emergencia WHERE id_miembro = ?";

            ps = ConnectionDB.connect().prepareStatement(sql_contact);
            ps.setInt(1, id_member);
            rs = ps.executeQuery();
            if (rs.next()) {
                JPanel contact_section = CreateSection("Contacto de Emergencia");
                contact_section.add(CreateRow("Nombre", rs.getString("nombre")));
                contact_section.add(CreateRow("Teléfono", rs.getString("telefono")));
                contact_section.add(CreateRow("Relación", rs.getString("relacion")));
                content.add(contact_section);
                content.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            String sql_membership = "SELECT mem.*, p.nombre_plan, p.precio_plan "
                    + "FROM membresia mem "
                    + "JOIN planes p ON mem.id_plan = p.id_plan "
                    + "WHERE mem.id_miembro = ?";
            ps = ConnectionDB.connect().prepareStatement(sql_membership);
            ps.setInt(1, id_member);
            rs = ps.executeQuery();

            if (rs.next()) {
                JPanel section_membership = CreateSection("Membresía");
                section_membership.add(CreateRow("Plan", rs.getString("nombre_plan")));
                section_membership.add(CreateRow("Precio", "$" + rs.getString("precio_plan")));
                section_membership.add(CreateRow("Inicio", rs.getString("fecha_inicio")));
                section_membership.add(CreateRow("Vencimiento", rs.getString("fecha_vencimiento")));
                section_membership.add(CreateRowEstado(rs.getString("estado")));
                content.add(section_membership);
                content.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            String sql_health = "SELECT * FROM salud WHERE id_miembro = ?";
            ps = ConnectionDB.connect().prepareStatement(sql_health);
            ps.setInt(1, id_member);
            rs = ps.executeQuery();

            if (rs.next()) {
                JPanel section_health = CreateSection("Salud");
                section_health.add(CreateRow("Condición médica",
                        rs.getBoolean("tiene_condicion_medica") ? "Sí" : "No"));
                if (rs.getBoolean("tiene_condicion_medica")) {
                    section_health.add(CreateRow("Descripción", rs.getString("descripcion_condicion")));
                }
                section_health.add(CreateRow("Lesiones recientes",
                        rs.getBoolean("tiene_lesiones") ? "Sí" : "No"));
                content.add(section_health);
                content.add(Box.createRigidArea(new Dimension(0, 10)));

                String sql_clases = "SELECT c.nombre_clase FROM miembro_clase mc "
                        + "JOIN clase c ON mc.id_clase = c.id_clase "
                        + "WHERE mc.id_miembro = ?";
                ps = ConnectionDB.connect().prepareStatement(sql_clases);
                ps.setInt(1, id_member);
                rs = ps.executeQuery();

                StringBuilder clases = new StringBuilder();

                while (rs.next()) {
                    if (clases.length() > 0) {
                        clases.append(", ");
                    }
                    clases.append(rs.getString("nombre_clase"));
                }
                JPanel section_clases = CreateSection("Clases");
                section_clases.add(CreateRow("Clases inscritas",
                        clases.length() > 0 ? clases.toString() : "Ninguna"));
                content.add(section_clases);
                content.add(Box.createRigidArea(new Dimension(0, 10)));

                String sql_days = "SELECT d.nombre_dia FROM miembro_dia md "
                        + "JOIN dia d ON md.id_dia = d.id_dia "
                        + "WHERE md.id_miembro = ?";
                ps = ConnectionDB.connect().prepareStatement(sql_days);
                ps.setInt(1, id_member);
                rs = ps.executeQuery();
                StringBuilder days = new StringBuilder();
                while (rs.next()) {
                    if (days.length() > 0) {
                        days.append(", ");
                    }
                    days.append(rs.getString("nombre_dia"));
                }
                JPanel section_days = CreateSection("Días disponibles");
                section_days.add(CreateRow("Días", days.length() > 0 ? days.toString() : "Ninguno"));
                content.add(section_days);
                content.add(Box.createRigidArea(new Dimension(0, 10)));

             
                String sql_visits = "SELECT TOP 5 fecha, hora_entrada, hora_salida "
                        + "FROM visita WHERE id_miembro = ? ORDER BY fecha DESC";
                ps = ConnectionDB.connect().prepareStatement(sql_visits);
                ps.setInt(1, id_member);
                rs = ps.executeQuery();
                JPanel section_visits = CreateSection("Últimas visitas");
                boolean tieneVisitas = false;
                while (rs.next()) {
                    tieneVisitas = true;
                    section_visits.add(CreateRow(rs.getString("fecha"),
                            "Entrada: " + rs.getString("hora_entrada")
                            + "   Salida: " + rs.getString("hora_salida")));
                }
                if (!tieneVisitas) {
                    section_visits.add(CreateRow("", "Sin visitas registradas"));
                }
                content.add(section_visits);
            }
        } catch (SQLException ex) {
            System.getLogger(DetailMember.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        JLabel titulo = new JLabel("Información detallada");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        add(titulo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(content);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel CreateSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(255, 220, 0), 2), title,
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12)),
                BorderFactory.createEmptyBorder(2, 8, 8, 8)
        ));
        return section;
    }

    private JPanel CreateRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        JLabel name_row = new JLabel(label + ":");
        name_row.setFont(new Font("Arial", Font.BOLD, 13));
        name_row.setForeground(new Color(80, 80, 80));
        name_row.setPreferredSize(new Dimension(160, 20));

        JLabel data = new JLabel(value != null ? value : "---");
        data.setFont(new Font("Arial", Font.PLAIN, 13));

        row.add(name_row, BorderLayout.WEST);
        row.add(data, BorderLayout.CENTER);
        return row;
    }

    private JPanel CreateRowEstado(String state) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        JLabel lbl = new JLabel("Estado:");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));
        lbl.setPreferredSize(new Dimension(160, 20));

        JLabel badge = new JLabel(state != null ? "  " + state + "  " : "—");
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        if ("Activa".equalsIgnoreCase(state)) {
            badge.setBackground(new Color(220, 255, 220));
            badge.setForeground(new Color(0, 130, 0));
        } else {
            badge.setBackground(new Color(255, 220, 220));
            badge.setForeground(new Color(180, 0, 0));
        }

        row.add(lbl, BorderLayout.WEST);
        row.add(badge, BorderLayout.CENTER);
        return row;
    }
}
