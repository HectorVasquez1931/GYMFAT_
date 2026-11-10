// Nombre: Hector Alfredo Vasquez Carpio
// Programa: GymFat - PanelMembresias
// Descripción: Muestra los planes de membresía como tarjetas con su precio,
// duración, beneficios y costo aproximado por día.
// Fecha: 09/11/2026
package gymfat;

// Librerías a usar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

// Pantalla informativa con los planes de membresía del gimnasio
public class PanelMembresias extends JPanel {

    // Constantes

    // Beneficios fijos de cada plan, en el mismo orden en que llegan los planes
    // de la base de datos (de menor a mayor duración): Mensual, Trimestral, Semestral y Anual
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

    // Color de la franja superior de cada tarjeta, en el mismo orden que BENEFICIOS
    private static final Color[] COLORES_PLANES = {
        new Color(100, 180, 255),   // Mensual: azul claro
        Estilos.VERDE,              // Trimestral: verde
        new Color(255, 165, 0),     // Semestral: naranja
        new Color(180, 100, 255)    // Anual: morado
    };

    // Duración en días del plan que se marca como "Más popular" (el trimestral)
    private static final int DIAS_PLAN_POPULAR = 90;

    // Límites en días para describir la duración del plan
    private static final int DIAS_LIMITE_EN_DIAS = 60;
    private static final int DIAS_SEMESTRE = 180;
    private static final int DIAS_POR_MES = 30;

    // Número de tarjetas por fila
    private static final int COLUMNAS_TARJETAS = 4;

    // Alto de la franja de color: más gruesa en el plan más popular
    private static final int ALTO_FRANJA_POPULAR = 8;
    private static final int ALTO_FRANJA_NORMAL = 5;

    // Medidas del dibujo de la tarjeta: radio de las esquinas y desplazamiento de la sombra
    private static final int RADIO_ESQUINAS = 20;
    private static final int DESPLAZAMIENTO_SOMBRA = 4;

    // Colores propios de esta pantalla
    private static final Color NEGRO_TEXTO = new Color(30, 30, 30);
    private static final Color GRIS_SEPARADOR = new Color(230, 230, 230);
    private static final Color NARANJA_BENEFICIO = new Color(180, 120, 0);
    private static final Color SOMBRA_TARJETA = new Color(0, 0, 0, 18);

    // Fuentes de esta pantalla
    private static final Font FUENTE_TITULO = new Font(Estilos.FUENTE, Font.BOLD, 26);
    private static final Font FUENTE_SUBTITULO = new Font(Estilos.FUENTE, Font.PLAIN, 13);
    private static final Font FUENTE_NOTA = new Font(Estilos.FUENTE, Font.ITALIC, 11);
    private static final Font FUENTE_INSIGNIA = new Font(Estilos.FUENTE, Font.BOLD, 10);
    private static final Font FUENTE_NOMBRE_PLAN = new Font(Estilos.FUENTE, Font.BOLD, 16);
    private static final Font FUENTE_PRECIO = new Font(Estilos.FUENTE, Font.BOLD, 28);
    private static final Font FUENTE_DETALLE = new Font(Estilos.FUENTE, Font.PLAIN, 11);
    private static final Font FUENTE_DETALLE_NEGRITA = new Font(Estilos.FUENTE, Font.BOLD, 11);

    // Atributos y componentes de la interfaz

    // Panel donde se colocan las tarjetas una vez que se cargan los planes
    private final JPanel pnlTarjetas;

    // Constructor

    // Crea la pantalla y carga los planes en segundo plano
    public PanelMembresias() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilos.FONDO_PANTALLA);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        pnlTarjetas = new JPanel(new GridLayout(1, COLUMNAS_TARJETAS, 14, 0));
        pnlTarjetas.setOpaque(false);

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearScrollTarjetas(), BorderLayout.CENTER);
        add(crearPiePagina(), BorderLayout.SOUTH);

        cargarPlanes();
    }

    // Construcción de la interfaz

    // Crea el título, el subtítulo y la línea amarilla que los separa de las tarjetas
    private JPanel crearEncabezado() {
        JLabel lblTitulo = new JLabel("Planes de Membresía");
        lblTitulo.setFont(FUENTE_TITULO);

        JLabel lblSubtitulo = new JLabel("Elige el plan que mejor se adapte a tus metas");
        lblSubtitulo.setFont(FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(Color.GRAY);

        JPanel pnlTextos = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTextos.setOpaque(false);
        pnlTextos.add(lblTitulo);
        pnlTextos.add(lblSubtitulo);

        JSeparator separador = new JSeparator();
        separador.setForeground(Estilos.AMARILLO);
        separador.setBackground(Estilos.AMARILLO);
        separador.setPreferredSize(new Dimension(0, 3));

        JPanel pnlEncabezado = new JPanel(new BorderLayout());
        pnlEncabezado.setOpaque(false);
        pnlEncabezado.add(pnlTextos, BorderLayout.CENTER);
        pnlEncabezado.add(separador, BorderLayout.SOUTH);
        pnlEncabezado.setBorder(new EmptyBorder(0, 0, 10, 0));
        return pnlEncabezado;
    }

    // Envuelve las tarjetas en un scroll horizontal por si la ventana es chica
    private JScrollPane crearScrollTarjetas() {
        JScrollPane scrTarjetas = new JScrollPane(pnlTarjetas,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrTarjetas.setBorder(null);
        scrTarjetas.getViewport().setOpaque(false);
        scrTarjetas.setOpaque(false);
        return scrTarjetas;
    }

    // Crea la nota informativa de la parte inferior
    private JPanel crearPiePagina() {
        JLabel lblNota = new JLabel(
                "  ℹ  Los precios incluyen IVA. Para renovar la membresía de un miembro, ve a la sección Inicio.");
        lblNota.setFont(FUENTE_NOTA);
        lblNota.setForeground(Color.GRAY);

        JPanel pnlPiePagina = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlPiePagina.setOpaque(false);
        pnlPiePagina.add(lblNota);
        return pnlPiePagina;
    }

    // Reemplaza el contenido del panel con una tarjeta por cada plan
    private void mostrarTarjetas(List<Plan> planes) {
        pnlTarjetas.removeAll();

        for (int i = 0; i < planes.size(); i++) {
            Plan plan = planes.get(i);
            // Si hubiera más planes que colores o beneficios, se repite el último
            Color colorPlan = COLORES_PLANES[Math.min(i, COLORES_PLANES.length - 1)];
            String[] beneficios = BENEFICIOS[Math.min(i, BENEFICIOS.length - 1)];
            boolean esPopular = plan.duracionDias == DIAS_PLAN_POPULAR;

            pnlTarjetas.add(crearTarjeta(plan, colorPlan, beneficios, esPopular));
        }

        pnlTarjetas.revalidate();
        pnlTarjetas.repaint();
    }

    // Crea la tarjeta de un plan: franja de color arriba y el contenido debajo
    private JPanel crearTarjeta(Plan plan, Color colorPlan, String[] beneficios, boolean esPopular) {
        JPanel pnlTarjeta = new PanelTarjeta();
        pnlTarjeta.setOpaque(false);
        pnlTarjeta.setLayout(new BoxLayout(pnlTarjeta, BoxLayout.Y_AXIS));
        pnlTarjeta.setBorder(new EmptyBorder(0, 0, 6, 0));

        int altoFranja = esPopular ? ALTO_FRANJA_POPULAR : ALTO_FRANJA_NORMAL;
        JPanel pnlFranja = new JPanel();
        pnlFranja.setBackground(colorPlan);
        pnlFranja.setMaximumSize(new Dimension(Integer.MAX_VALUE, altoFranja));
        pnlFranja.setPreferredSize(new Dimension(0, altoFranja));
        pnlTarjeta.add(pnlFranja);

        pnlTarjeta.add(crearContenidoTarjeta(plan, colorPlan, beneficios, esPopular));
        return pnlTarjeta;
    }

    // Agrega a la tarjeta la insignia, el nombre, el precio, la duración,
    // los beneficios y el precio por día
    private JPanel crearContenidoTarjeta(Plan plan, Color colorPlan, String[] beneficios,
            boolean esPopular) {
        JPanel pnlContenido = new JPanel();
        pnlContenido.setOpaque(false);
        pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS));
        pnlContenido.setBorder(new EmptyBorder(12, 14, 14, 14));

        if (esPopular) {
            pnlContenido.add(crearInsigniaPopular(colorPlan));
            pnlContenido.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        pnlContenido.add(crearEtiqueta(plan.nombre.toUpperCase(), FUENTE_NOMBRE_PLAN, NEGRO_TEXTO));
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 4)));

        pnlContenido.add(crearEtiqueta(String.format("$%.0f", plan.precio), FUENTE_PRECIO,
                colorPlan.darker()));
        pnlContenido.add(crearEtiqueta("Duración: " + describirDuracion(plan.duracionDias),
                FUENTE_DETALLE, Color.GRAY));
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 10)));

        pnlContenido.add(crearSeparador());
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 10)));

        pnlContenido.add(crearEtiqueta("Incluye:", FUENTE_DETALLE_NEGRITA, Color.DARK_GRAY));
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 6)));

        // Se usa HTML para que los beneficios largos ocupen varias líneas
        for (String beneficio : beneficios) {
            pnlContenido.add(crearEtiqueta("<html>" + beneficio + "</html>", FUENTE_DETALLE,
                    NARANJA_BENEFICIO));
            pnlContenido.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        // El precio por día queda al fondo de la tarjeta
        pnlContenido.add(Box.createVerticalGlue());
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlContenido.add(crearSeparador());
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 6)));

        double precioPorDia = plan.precio / plan.duracionDias;
        pnlContenido.add(crearEtiqueta(String.format("≈ $%.1f por día", precioPorDia),
                FUENTE_NOTA, Color.GRAY));

        return pnlContenido;
    }

    // Crea la insignia "Más popular" con el color del plan
    private JLabel crearInsigniaPopular(Color colorPlan) {
        JLabel lblInsignia = crearEtiqueta("★  MÁS POPULAR", FUENTE_INSIGNIA, Color.WHITE);
        lblInsignia.setOpaque(true);
        lblInsignia.setBackground(colorPlan);
        lblInsignia.setBorder(new EmptyBorder(3, 8, 3, 8));
        return lblInsignia;
    }

    // Acceso a datos

    // Carga en segundo plano los planes de la base de datos y después muestra las tarjetas
    private void cargarPlanes() {
        new Thread(() -> {
            Connection conexion = ConexionBD.conectar();
            if (conexion == null) {
                return;
            }

            // Obtiene todos los planes, del de menor al de mayor duración
            String consulta = "SELECT id_plan, nombre_plan, precio_plan, duracion_dias "
                    + "FROM planes ORDER BY duracion_dias ASC";

            try (PreparedStatement sentencia = conexion.prepareStatement(consulta);
                    ResultSet resultado = sentencia.executeQuery()) {
                List<Plan> planes = new ArrayList<>();
                while (resultado.next()) {
                    planes.add(new Plan(
                            resultado.getString("nombre_plan"),
                            resultado.getDouble("precio_plan"),
                            resultado.getInt("duracion_dias")));
                }
                SwingUtilities.invokeLater(() -> mostrarTarjetas(planes));
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(this::mostrarErrorCarga);
            }
        }, "hilo-planes").start();
    }

    // Métodos auxiliares

    // Muestra un aviso en lugar de las tarjetas cuando no se pudieron cargar los planes
    private void mostrarErrorCarga() {
        pnlTarjetas.add(new JLabel("No se pudieron cargar los planes."));
        pnlTarjetas.revalidate();
    }

    // Convierte la duración en días a un texto: "30 días", "3 meses", "6 meses" o "1 año"
    private String describirDuracion(int duracionDias) {
        if (duracionDias < DIAS_LIMITE_EN_DIAS) {
            return duracionDias + " días";
        }
        if (duracionDias < DIAS_SEMESTRE) {
            return (duracionDias / DIAS_POR_MES) + " meses";
        }
        if (duracionDias == DIAS_SEMESTRE) {
            return "6 meses";
        }
        return "1 año";
    }

    // Crea una etiqueta alineada a la izquierda con la fuente y el color indicados
    private JLabel crearEtiqueta(String texto, Font fuente, Color colorTexto) {
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(fuente);
        lblTexto.setForeground(colorTexto);
        lblTexto.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lblTexto;
    }

    // Crea una línea gris delgada que separa secciones de la tarjeta
    private JSeparator crearSeparador() {
        JSeparator separador = new JSeparator();
        separador.setForeground(GRIS_SEPARADOR);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);
        return separador;
    }

    // Datos de un plan que se muestran en su tarjeta
    private static class Plan {

        private final String nombre;
        private final double precio;
        private final int duracionDias;

        private Plan(String nombre, double precio, int duracionDias) {
            this.nombre = nombre;
            this.precio = precio;
            this.duracionDias = duracionDias;
        }
    }

    // Panel blanco con esquinas redondeadas y una sombra suave
    private static class PanelTarjeta extends JPanel {

        @Override
        protected void paintComponent(Graphics grafico) {
            Graphics2D grafico2D = (Graphics2D) grafico.create();
            grafico2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Sombra desplazada hacia abajo y a la derecha
            grafico2D.setColor(SOMBRA_TARJETA);
            grafico2D.fillRoundRect(DESPLAZAMIENTO_SOMBRA, DESPLAZAMIENTO_SOMBRA,
                    getWidth() - DESPLAZAMIENTO_SOMBRA, getHeight() - DESPLAZAMIENTO_SOMBRA,
                    RADIO_ESQUINAS, RADIO_ESQUINAS);

            // Fondo blanco de la tarjeta
            grafico2D.setColor(Color.WHITE);
            grafico2D.fillRoundRect(0, 0, getWidth() - DESPLAZAMIENTO_SOMBRA,
                    getHeight() - DESPLAZAMIENTO_SOMBRA, RADIO_ESQUINAS, RADIO_ESQUINAS);

            grafico2D.dispose();
            super.paintComponent(grafico);
        }
    }
}