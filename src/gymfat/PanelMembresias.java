/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gymfat;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author SoaringLatitute2
 */

public class PanelMembresias extends JPanel {

    private static final Color AMARILLO  = new Color(255, 220, 0);
    private static final Color VERDE     = new Color(46, 204, 113);
    private static final Color ROJO      = new Color(231, 76, 60);
    private static final Color NARANJA   = new Color(255, 165, 0);
    private static final Color FONDO     = new Color(245, 245, 245);
    private static final Color NEGRO     = new Color(30, 30, 30);

    // Beneficios fijos por plan (índice 0=Mensual, 1=Trimestral, 2=Semestral, 3=Anual)
    private static final String[][] BENEFICIOS = {
        {
            "Acceso a área de pesas y cardio",
            "Uso de vestidores y regaderas",
            "1 evaluación física inicial",
            "2 clases grupales por semana",
            "1 sesión con entrenador personal",
            "Horario: 8–20 h",
            "Sin acceso a sauna",
            "Sin acceso en días festivos"
        },
        {
            // TRIMESTRAL — más clases, más entrenador, sauna
            "Acceso a área de pesas y cardio",
            "Clases grupales ilimitadas",
            "4 sesiones con entrenador personal",
            "Acceso a sauna (3 veces/semana)",
            "1 consulta de nutrición",
            "Horario completo: 6–22 h",
            "Acceso en días festivos",
            "Sin descuento en tienda"
        },
        {
            "Acceso a área de pesas y cardio",
            "Clases grupales ilimitadas",
            "8 sesiones con entrenador personal",
            "Acceso libre a sauna",
            "2 consultas de nutrición",
            "10% descuento en tienda",
            "Horario completo: 6–22 h",
            "Acceso en días festivos"
        },
        {
            "Acceso a área de pesas y cardio",
            "Clases grupales ilimitadas",
            "Sesiones ilimitadas con entrenador",
            "Acceso libre a sauna y área de masajes",
            "4 consultas de nutrición",
            "20% descuento en tienda",
            "Congelamiento hasta 30 días/año",
            "Acceso prioritario — sin filas"
        }
        
    };

    private static final Color[] ACENTOS = {
        new Color(100, 180, 255),   // Mensual     — azul claro
        VERDE,                       // Trimestral  — verde
        NARANJA,                     // Semestral   — naranja
        new Color(180, 100, 255)    // Anual        — morado
    };

    private JPanel panelTarjetas;

    public PanelMembresias() {
        setLayout(new BorderLayout(0, 10));
        setBackground(FONDO);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(crearEncabezado(), BorderLayout.NORTH);

        panelTarjetas = new JPanel(new GridLayout(1, 4, 14, 0));
        panelTarjetas.setOpaque(false);

        // Envolver en scroll horizontal por si la ventana es chica
        JScrollPane scroll = new JScrollPane(panelTarjetas,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        add(crearPie(), BorderLayout.SOUTH);

        cargarPlanesEnHilo();
    }

    // ── Encabezado ────────────────────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel titulo = new JLabel("Planes de Membresía");
        titulo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel sub = new JLabel("Elige el plan que mejor se adapte a tus metas");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(sub);

        // Línea decorativa amarilla abajo
        JSeparator sep = new JSeparator();
        sep.setForeground(AMARILLO);
        sep.setBackground(AMARILLO);
        sep.setPreferredSize(new Dimension(0, 3));

        p.add(textos, BorderLayout.CENTER);
        p.add(sep,    BorderLayout.SOUTH);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));
        return p;
    }

    // ── Pie: nota informativa ─────────────────────────────────────────────────
    private JPanel crearPie() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        JLabel nota = new JLabel(
            "  ℹ  Los precios incluyen IVA. Para renovar la membresía de un miembro, ve a la sección Inicio.");
        nota.setFont(new Font("Arial", Font.ITALIC, 11));
        nota.setForeground(Color.GRAY);
        p.add(nota);
        return p;
    }

    // ── Hilo: carga planes desde BD ──────────────────────────────────────────
    private void cargarPlanesEnHilo() {
        new Thread(() -> {
            try {
                Connection con = ConnectionDB.connect();
                if (con == null) return;

                String sql = "SELECT id_plan, nombre_plan, precio_plan, duracion_dias "
                           + "FROM planes ORDER BY duracion_dias ASC";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                List<Object[]> planes = new ArrayList<>();
                while (rs.next()) {
                    planes.add(new Object[]{
                        rs.getInt("id_plan"),
                        rs.getString("nombre_plan"),
                        rs.getDouble("precio_plan"),
                        rs.getInt("duracion_dias")
                    });
                }
                rs.close(); ps.close();

                SwingUtilities.invokeLater(() -> mostrarTarjetas(planes));

            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> {
                    panelTarjetas.add(new JLabel("Error al cargar planes: " + ex.getMessage()));
                    panelTarjetas.revalidate();
                });
            }
        }, "hilo-planes").start();
    }

    // ── Construye las tarjetas en el EDT ──────────────────────────────────────
    private void mostrarTarjetas(List<Object[]> planes) {
        panelTarjetas.removeAll();

        for (int i = 0; i < planes.size(); i++) {
            Object[] plan   = planes.get(i);
            String   nombre = (String) plan[1];
            double   precio = (double) plan[2];
            int      dias   = (int)   plan[3];
            Color    acento = ACENTOS[Math.min(i, ACENTOS.length - 1)];
            String[] bens   = BENEFICIOS[Math.min(i, BENEFICIOS.length - 1)];

            // Marcar el plan más popular (Trimestral)
            boolean popular = dias == 90;

            panelTarjetas.add(crearTarjeta(nombre, precio, dias, acento, bens, popular));
        }

        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    // ── Crea una tarjeta individual ───────────────────────────────────────────
    private JPanel crearTarjeta(String nombre, double precio, int dias,
                                 Color acento, String[] beneficios, boolean popular) {
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra suave
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 20, 20);
                // Fondo blanco
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(0, 0, 6, 0));

        // ── Franja de color en la parte superior ──────────────────────────────
        JPanel franja = new JPanel();
        franja.setBackground(acento);
        franja.setMaximumSize(new Dimension(Integer.MAX_VALUE, popular ? 8 : 5));
        franja.setPreferredSize(new Dimension(0, popular ? 8 : 5));
        tarjeta.add(franja);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 14, 14, 14));

        // Badge "Más popular"
        if (popular) {
            JLabel badge = new JLabel("★  MÁS POPULAR");
            badge.setFont(new Font("Arial", Font.BOLD, 10));
            badge.setForeground(Color.WHITE);
            badge.setOpaque(true);
            badge.setBackground(acento);
            badge.setBorder(new EmptyBorder(3, 8, 3, 8));
            badge.setAlignmentX(Component.LEFT_ALIGNMENT);
            contenido.add(badge);
            contenido.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        // Nombre del plan
        JLabel lblNombre = new JLabel(nombre.toUpperCase());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        lblNombre.setForeground(NEGRO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblNombre);
        contenido.add(Box.createRigidArea(new Dimension(0, 4)));

        // Precio
        JLabel lblPrecio = new JLabel(String.format("$%.0f", precio));
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 28));
        lblPrecio.setForeground(acento.darker());
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblPrecio);

        // Duración
        String durTxt = dias < 60 ? dias + " días"
                      : dias < 180 ? (dias / 30) + " meses"
                      : dias == 180 ? "6 meses"
                      : "1 año";
        JLabel lblDur = new JLabel("Duración: " + durTxt);
        lblDur.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDur.setForeground(Color.GRAY);
        lblDur.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblDur);
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(230, 230, 230));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(sep);
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));

        // Beneficios
        JLabel lblBenTitulo = new JLabel("Incluye:");
        lblBenTitulo.setFont(new Font("Arial", Font.BOLD, 11));
        lblBenTitulo.setForeground(Color.DARK_GRAY);
        lblBenTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblBenTitulo);
        contenido.add(Box.createRigidArea(new Dimension(0, 6)));

        for (String ben : beneficios) {
            JLabel lbl = new JLabel("<html>" + ben + "</html>");
            lbl.setFont(new Font("Arial", Font.PLAIN, 11));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (ben.startsWith("✅")) lbl.setForeground(new Color(40, 40, 40));
            else if (ben.startsWith("❌")) lbl.setForeground(new Color(160, 160, 160));
            else lbl.setForeground(new Color(180, 120, 0));   // ⚠ naranja

            contenido.add(lbl);
            contenido.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        // Precio por día (dato curioso)
        contenido.add(Box.createVerticalGlue());
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(230, 230, 230));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(sep2);
        contenido.add(Box.createRigidArea(new Dimension(0, 6)));

        double porDia = precio / dias;
        JLabel lblPorDia = new JLabel(String.format("≈ $%.1f por día", porDia));
        lblPorDia.setFont(new Font("Arial", Font.ITALIC, 11));
        lblPorDia.setForeground(Color.GRAY);
        lblPorDia.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblPorDia);

        tarjeta.add(contenido);
        return tarjeta;
    }
}