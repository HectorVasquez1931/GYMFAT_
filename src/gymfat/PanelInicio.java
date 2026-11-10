/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gymfat;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PanelInicio extends JPanel {

    String user;

    // ── colores ───────────────────────────────────────────────────────────────
    private static final Color AMARILLO = new Color(255, 220, 0);
    private static final Color VERDE = new Color(46, 204, 113);
    private static final Color ROJO = new Color(231, 76, 60);
    private static final Color FONDO = new Color(245, 245, 245);

    // ── componentes ───────────────────────────────────────────────────────────
    private JTextField txtBuscar;
    private JLabel lblNombreMiembro, lblEstado, lblPlan,
            lblVencimiento, lblPago, lblTelefono;
    private JLabel lblReloj;
    private JLabel lblFoto;       // muestra foto del miembro
    private JButton btnRenovar;          // visible solo si estado = vencida
    private int idMiembroActual = -1;
    private String estadoActual = "";

    private final Deque<Object[]> historialDeque = new ArrayDeque<>();
    private DefaultTableModel modeloHistorial;

    private static final DateTimeFormatter FMT_HORA
            = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FMT_FECHA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─────────────────────────────────────────────────────────────────────────
    public PanelInicio(String user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(crearNorte(), BorderLayout.NORTH);
        add(crearCentro(), BorderLayout.CENTER);

        iniciarHiloReloj();
        cargarHistorialReciente();
    }

    // ── NORTE ─────────────────────────────────────────────────────────────────
    private JPanel crearNorte() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel titulo = new JLabel("Bienvenido(a) "+user);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblReloj = new JLabel("", SwingConstants.RIGHT);
        lblReloj.setFont(new Font("Arial", Font.PLAIN, 13));
        lblReloj.setForeground(Color.DARK_GRAY);
        p.add(titulo, BorderLayout.WEST);
        p.add(lblReloj, BorderLayout.EAST);
        return p;
    }

    // ── CENTRO ────────────────────────────────────────────────────────────────
    private JPanel crearCentro() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);
        p.add(crearPanelMovimiento());
        p.add(crearPanelInfoMiembro());
        return p;
    }

    // ── Panel izquierdo: búsqueda + botones + historial ───────────────────────
    private JPanel crearPanelMovimiento() {
        JPanel panel = panelConTitulo("Registrar Entrada y Salida");
        panel.setLayout(new BorderLayout(0, 10));

        JPanel filaBusqueda = new JPanel(new BorderLayout(6, 0));
        filaBusqueda.setOpaque(false);
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar Miembro por ID o Nombre...");
        JButton btnBuscar = botonChico("Buscar", Color.DARK_GRAY);
        filaBusqueda.add(txtBuscar, BorderLayout.CENTER);
        filaBusqueda.add(btnBuscar, BorderLayout.EAST);

        JButton btnEntrada = crearBotonMovimiento("MARCAR ENTRADA", VERDE);
        JButton btnSalida = crearBotonMovimiento("MARCAR SALIDA", ROJO);
        JPanel botones = new JPanel(new GridLayout(2, 1, 0, 8));
        botones.setOpaque(false);
        botones.add(btnEntrada);
        botones.add(btnSalida);

        JLabel lbl = new JLabel("Registrar Movimiento");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(filaBusqueda, BorderLayout.NORTH);
        top.add(lbl, BorderLayout.CENTER);
        top.add(botones, BorderLayout.SOUTH);

        String[] cols = {"Fecha", "Hora Entrada", "Hora Salida"};
        modeloHistorial = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloHistorial);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setBackground(AMARILLO);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AMARILLO, 1),
                "Historial Reciente",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11)));

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        txtBuscar.addActionListener(e -> buscarMiembroEnHilo(txtBuscar.getText().trim()));
        btnBuscar.addActionListener(e -> buscarMiembroEnHilo(txtBuscar.getText().trim()));
        btnEntrada.addActionListener(e -> registrarEntrada());
        btnSalida.addActionListener(e -> registrarSalida());

        return panel;
    }

    // ── Panel derecho: info del miembro ───────────────────────────────────────
    private JPanel crearPanelInfoMiembro() {
        JPanel panel = panelConTitulo("Informacion del Miembro");
        panel.setLayout(new BorderLayout(0, 10));

        // Foto del miembro — placeholder hasta que se busque uno
        lblFoto = new JLabel(fotoPlaceholder(), SwingConstants.CENTER);
        lblFoto.setPreferredSize(new Dimension(110, 120));
        lblFoto.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        lblNombreMiembro = labelNegrita("—", 15);
        lblEstado = labelNegrita("Estado: —", 13);   // negrita para que el color resalte
        lblPlan = labelNormal("Tipo Membresia: —");
        lblVencimiento = labelNormal("Vencimiento: —");
        lblPago = labelNormal("Ultimo Pago: —");
        lblTelefono = labelNormal("Telefono: —");

        // Boton renovar — empieza oculto
        btnRenovar = new JButton("Renovar Membresia");
        btnRenovar.setBackground(AMARILLO);
        btnRenovar.setForeground(Color.BLACK);
        btnRenovar.setFont(new Font("Arial", Font.BOLD, 12));
        btnRenovar.setBorderPainted(false);
        btnRenovar.setFocusPainted(false);
        btnRenovar.setVisible(false);
        btnRenovar.addActionListener(e -> abrirDialogoRenovar());

        JPanel datos = new JPanel(new GridLayout(0, 1, 0, 7));
        datos.setOpaque(false);
        datos.add(lblNombreMiembro);
        datos.add(lblEstado);
        datos.add(lblPlan);
        datos.add(lblVencimiento);
        datos.add(lblPago);
        datos.add(lblTelefono);
        datos.add(btnRenovar);          // siempre en el panel, solo cambia visibilidad

        panel.add(lblFoto, BorderLayout.NORTH);
        panel.add(datos, BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HILO 1 — Buscar miembro
    // ─────────────────────────────────────────────────────────────────────────
    private void buscarMiembroEnHilo(String termino) {
        if (termino.isEmpty()) {
            return;
        }
        new Thread(() -> {
            try {
                Connection con = ConnectionDB.connect();
                if (con == null) {
                    return;
                }

                String sql
                        = "SELECT TOP 1 m.id_miembro, "
                        + "m.nombres + ' ' + m.apellido_paterno + ' ' + ISNULL(m.apellido_materno,'') AS nombre_completo, "
                        + "m.telefono, p.nombre_plan, "
                        + "mem.fecha_vencimiento, mem.fecha_inicio, mem.estado "
                        + "FROM miembro m "
                        + "LEFT JOIN membresia mem ON m.id_miembro = mem.id_miembro "
                        + "LEFT JOIN planes p      ON mem.id_plan  = p.id_plan "
                        + "WHERE CAST(m.id_miembro AS VARCHAR) = ? "
                        + "   OR m.nombres + ' ' + m.apellido_paterno LIKE ? "
                        + "ORDER BY mem.fecha_vencimiento DESC";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, termino);
                ps.setString(2, "%" + termino + "%");
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("id_miembro");
                    String nombre = rs.getString("nombre_completo");
                    String tel = rs.getString("telefono");
                    String plan = rs.getString("nombre_plan");
                    String venc = rs.getString("fecha_vencimiento");
                    String inicio = rs.getString("fecha_inicio");
                    String estado = rs.getString("estado");

                    SwingUtilities.invokeLater(()
                            -> actualizarInfoMiembro(id, nombre, tel, plan, venc, inicio, estado));
                } else {
                    SwingUtilities.invokeLater(() -> {
                        limpiarInfo();
                        JOptionPane.showMessageDialog(PanelInicio.this,
                                "No se encontro ningun miembro con: " + termino,
                                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                    });
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(PanelInicio.this,
                                "Error al buscar: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
            }
        }, "hilo-busqueda").start();
    }

    // Actualiza los labels y aplica color según estado
    private void actualizarInfoMiembro(int id, String nombre, String tel,
            String plan, String venc, String inicio, String estado) {
        idMiembroActual = id;
        estadoActual = nvl(estado);

        lblNombreMiembro.setText(nombre + "  (ID " + id + ")");
        lblPlan.setText("Tipo Membresia: " + nvl(plan));
        lblVencimiento.setText("Vencimiento: " + nvl(venc));
        lblPago.setText("Ultimo Pago: " + nvl(inicio));
        lblTelefono.setText("Telefono: " + nvl(tel));

        boolean activa = "activa".equalsIgnoreCase(estado);

        // ── Color del estado ──────────────────────────────────────────────────
        lblEstado.setText("Estado: " + estadoActual.toUpperCase());
        lblEstado.setForeground(activa ? VERDE : ROJO);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 13));

        // ── Boton renovar: solo visible si esta vencida ───────────────────────
        btnRenovar.setVisible(!activa);

        // ── Foto del miembro ─────────────────────────────────────────────────
        cargarFotoEnHilo(id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIALOGO DE RENOVACION
    // ─────────────────────────────────────────────────────────────────────────
    private void abrirDialogoRenovar() {
        // Cargamos los planes desde la BD en un hilo, luego mostramos el dialog
        new Thread(() -> {
            try {
                Connection con = ConnectionDB.connect();
                if (con == null) {
                    return;
                }

                // Traer los 4 planes base (los primeros 4 distintos por nombre)
                String sql = "SELECT id_plan, nombre_plan, precio_plan, duracion_dias "
                        + "FROM planes "
                        + "ORDER BY duracion_dias ASC";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                // Guardamos en arrays para pasar al dialog en el EDT
                java.util.List<int[]> ids = new java.util.ArrayList<>();
                java.util.List<String> etiquetas = new java.util.ArrayList<>();

                while (rs.next()) {
                    ids.add(new int[]{rs.getInt("id_plan"), rs.getInt("duracion_dias")});
                    etiquetas.add(String.format("%s — $%.0f / %d dias",
                            rs.getString("nombre_plan"),
                            rs.getDouble("precio_plan"),
                            rs.getInt("duracion_dias")));
                }
                rs.close();
                ps.close();

                SwingUtilities.invokeLater(()
                        -> mostrarDialogoRenovar(ids, etiquetas));

            } catch (SQLException ex) {
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(PanelInicio.this,
                                "Error al cargar planes: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
            }
        }, "hilo-cargar-planes").start();
    }

    private void mostrarDialogoRenovar(java.util.List<int[]> ids,
            java.util.List<String> etiquetas) {
        // Obtener ventana padre
        Window padre = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(
                (padre instanceof Frame) ? (Frame) padre : null,
                "Renovar Membresia", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Titulo
        JLabel titulo = new JLabel("Selecciona el nuevo plan:");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(titulo, BorderLayout.NORTH);

        // Grupo de radio buttons — uno por plan
        JPanel panelPlanes = new JPanel(new GridLayout(0, 1, 0, 8));
        panelPlanes.setBackground(Color.WHITE);
        panelPlanes.setBorder(BorderFactory.createLineBorder(AMARILLO, 2));
        ButtonGroup grupo = new ButtonGroup();
        JRadioButton[] radios = new JRadioButton[etiquetas.size()];

        for (int i = 0; i < etiquetas.size(); i++) {
            radios[i] = new JRadioButton(etiquetas.get(i));
            radios[i].setFont(new Font("Arial", Font.PLAIN, 13));
            radios[i].setBackground(Color.WHITE);
            if (i == 0) {
                radios[i].setSelected(true);
            }
            grupo.add(radios[i]);
            panelPlanes.add(radios[i]);
        }
        dialog.add(new JScrollPane(panelPlanes), BorderLayout.CENTER);

        // Botones
        JButton btnConfirmar = new JButton("Confirmar Pago");
        btnConfirmar.setBackground(VERDE);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 13));
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setFocusPainted(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ROJO);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnConfirmar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        // ── Accion confirmar ──────────────────────────────────────────────────
        btnConfirmar.addActionListener(e -> {
            int indexSeleccionado = -1;
            for (int i = 0; i < radios.length; i++) {
                if (radios[i].isSelected()) {
                    indexSeleccionado = i;
                    break;
                }
            }
            if (indexSeleccionado == -1) {
                return;
            }

            int idPlan = ids.get(indexSeleccionado)[0];
            int duracionDias = ids.get(indexSeleccionado)[1];

            dialog.dispose();
            renovarMembresia(idPlan, duracionDias);
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HILO — Renovar membresía en la BD
    // ─────────────────────────────────────────────────────────────────────────
    private void renovarMembresia(int idPlan, int duracionDias) {
        new Thread(() -> {
            try {
                Connection con = ConnectionDB.connect();
                if (con == null) {
                    return;
                }

                LocalDate hoy = LocalDate.now();
                LocalDate vencimiento = hoy.plusDays(duracionDias);

                String sql = "UPDATE membresia "
                        + "SET id_plan = ?, "
                        + "    fecha_inicio = ?, "
                        + "    fecha_vencimiento = ?, "
                        + "    estado = 'activa' "
                        + "WHERE id_miembro = ?";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, idPlan);
                ps.setDate(2, Date.valueOf(hoy));
                ps.setDate(3, Date.valueOf(vencimiento));
                ps.setInt(4, idMiembroActual);
                ps.executeUpdate();
                ps.close();

                // Recargar info del miembro para reflejar el nuevo estado
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(PanelInicio.this,
                            "Membresia renovada correctamente hasta: " + vencimiento,
                            "Renovacion exitosa", JOptionPane.INFORMATION_MESSAGE);
                    // Volver a buscar para actualizar los labels
                    buscarMiembroEnHilo(String.valueOf(idMiembroActual));
                });

            } catch (SQLException ex) {
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(PanelInicio.this,
                                "Error al renovar: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
            }
        }, "hilo-renovar").start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HILO 2 — Registrar ENTRADA (con validación: no doble entrada sin salida)
    // ─────────────────────────────────────────────────────────────────────────
    private void registrarEntrada() {
        if (idMiembroActual == -1) {
            JOptionPane.showMessageDialog(this, "Busca un miembro primero.",
                    "Sin miembro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new Thread(() -> {
            try {
                Connection con = ConnectionDB.connect();
                if (con == null) {
                    return;
                }

                // ── Validación: ¿ya tiene entrada activa hoy (sin salida)? ────
                String sqlCheck = "SELECT COUNT(*) FROM visita "
                        + "WHERE id_miembro = ? "
                        + "  AND fecha = CAST(GETDATE() AS DATE) "
                        + "  AND hora_salida IS NULL";
                PreparedStatement psCheck = con.prepareStatement(sqlCheck);
                psCheck.setInt(1, idMiembroActual);
                ResultSet rs = psCheck.executeQuery();
                rs.next();
                int pendientes = rs.getInt(1);
                rs.close();
                psCheck.close();

                if (pendientes > 0) {
                    SwingUtilities.invokeLater(()
                            -> JOptionPane.showMessageDialog(PanelInicio.this,
                                    "Este miembro ya tiene una entrada activa hoy."
                                    + "Debe registrar la salida antes de volver a entrar.",
                                    "Entrada duplicada", JOptionPane.WARNING_MESSAGE));
                    return;
                }

                // ── Insertar entrada ──────────────────────────────────────────
                String sql = "INSERT INTO visita (id_miembro, fecha, hora_entrada) "
                        + "VALUES (?, CAST(GETDATE() AS DATE), CAST(GETDATE() AS TIME))";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, idMiembroActual);
                ps.executeUpdate();
                ps.close();

                String fecha = LocalDateTime.now().format(FMT_FECHA);
                String hora = LocalDateTime.now().format(FMT_HORA);
                agregarAlHistorial(fecha, hora, "—");

                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(PanelInicio.this,
                                "Entrada registrada correctamente.",
                                "Entrada OK", JOptionPane.INFORMATION_MESSAGE));

            } catch (SQLException ex) {
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(PanelInicio.this,
                                "Error al registrar entrada: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
            }
        }, "hilo-entrada").start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HILO 3 — Registrar SALIDA (con validación: debe existir entrada activa)
    // ─────────────────────────────────────────────────────────────────────────
    private void registrarSalida() {
        if (idMiembroActual == -1) {
            JOptionPane.showMessageDialog(this, "Busca un miembro primero.",
                    "Sin miembro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new Thread(() -> {
            try {
                Connection con = ConnectionDB.connect();
                if (con == null) {
                    return;
                }

                // ── Validación: ¿existe entrada activa hoy? ───────────────────
                String sqlCheck = "SELECT COUNT(*) FROM visita "
                        + "WHERE id_miembro = ? "
                        + "  AND fecha = CAST(GETDATE() AS DATE) "
                        + "  AND hora_salida IS NULL";
                PreparedStatement psCheck = con.prepareStatement(sqlCheck);
                psCheck.setInt(1, idMiembroActual);
                ResultSet rs = psCheck.executeQuery();
                rs.next();
                int activas = rs.getInt(1);
                rs.close();
                psCheck.close();

                if (activas == 0) {
                    SwingUtilities.invokeLater(()
                            -> JOptionPane.showMessageDialog(PanelInicio.this,
                                    "Este miembro no tiene ninguna entrada activa hoy."
                                    + "Registra la entrada primero.",
                                    "Sin entrada activa", JOptionPane.WARNING_MESSAGE));
                    return;
                }

                // ── Actualizar hora de salida ─────────────────────────────────
                String sql = "UPDATE TOP (1) visita "
                        + "SET hora_salida = CAST(GETDATE() AS TIME) "
                        + "WHERE id_miembro = ? "
                        + "  AND fecha = CAST(GETDATE() AS DATE) "
                        + "  AND hora_salida IS NULL";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, idMiembroActual);
                ps.executeUpdate();
                ps.close();

                String fecha = LocalDateTime.now().format(FMT_FECHA);
                String hora = LocalDateTime.now().format(FMT_HORA);
                agregarAlHistorial(fecha, "—", hora);

                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(PanelInicio.this,
                                "Salida registrada correctamente.",
                                "Salida OK", JOptionPane.INFORMATION_MESSAGE));

            } catch (SQLException ex) {
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(PanelInicio.this,
                                "Error al registrar salida: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
            }
        }, "hilo-salida").start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HILO 4 — Historial inicial
    // ─────────────────────────────────────────────────────────────────────────
    private void cargarHistorialReciente() {
        new Thread(() -> {
            try {
                Connection con = ConnectionDB.connect();
                if (con == null) {
                    return;
                }

                String sql
                        = "SELECT TOP 50 "
                        + "CONVERT(VARCHAR, fecha, 103)                    AS fecha, "
                        + "CONVERT(VARCHAR, hora_entrada, 108)             AS hora_entrada, "
                        + "ISNULL(CONVERT(VARCHAR, hora_salida, 108), '—') AS hora_salida "
                        + "FROM visita "
                        + "ORDER BY fecha DESC, hora_entrada DESC";

                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String f = rs.getString("fecha");
                    String he = rs.getString("hora_entrada");
                    String hs = rs.getString("hora_salida");
                    synchronized (historialDeque) {
                        historialDeque.addLast(new Object[]{f, he, hs});
                    }
                    SwingUtilities.invokeLater(()
                            -> modeloHistorial.addRow(new Object[]{f, he, hs}));
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                System.out.println("Historial no cargado: " + ex.getMessage());
            }
        }, "hilo-historial").start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HILO 5 — Reloj daemon
    // ─────────────────────────────────────────────────────────────────────────
    private void iniciarHiloReloj() {
        Thread t = new Thread(() -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(
                    "EEEE, dd 'de' MMMM yyyy   HH:mm:ss",
                    new java.util.Locale("es", "MX"));
            while (!Thread.currentThread().isInterrupted()) {
                String hora = LocalDateTime.now().format(fmt);
                SwingUtilities.invokeLater(() -> lblReloj.setText(hora));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "hilo-reloj");
        t.setDaemon(true);
        t.start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void agregarAlHistorial(String fecha, String entrada, String salida) {
        synchronized (historialDeque) {
            if (historialDeque.size() >= 50) {
                historialDeque.pollLast();
            }
            historialDeque.addFirst(new Object[]{fecha, entrada, salida});
        }
        SwingUtilities.invokeLater(() -> {
            modeloHistorial.insertRow(0, new Object[]{fecha, entrada, salida});
            if (modeloHistorial.getRowCount() > 50) {
                modeloHistorial.removeRow(50);
            }
        });
    }

    private void limpiarInfo() {
        idMiembroActual = -1;
        estadoActual = "";
        lblNombreMiembro.setText("—");
        lblEstado.setText("Estado: —");
        lblEstado.setForeground(Color.DARK_GRAY);
        lblPlan.setText("Tipo Membresia: —");
        lblVencimiento.setText("Vencimiento: —");
        lblPago.setText("Ultimo Pago: —");
        lblTelefono.setText("Telefono: —");
        btnRenovar.setVisible(false);
        lblFoto.setIcon(fotoPlaceholder());
    }

    private String nvl(String s) {
        return s != null ? s : "—";
    }

    private JPanel panelConTitulo(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        TitledBorder b = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AMARILLO, 2), titulo);
        b.setTitleFont(new Font("Arial", Font.BOLD, 13));
        p.setBorder(BorderFactory.createCompoundBorder(b,
                BorderFactory.createEmptyBorder(8, 10, 10, 10)));
        return p;
    }

    private JButton crearBotonMovimiento(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 42));
        return btn;
    }

    private JButton botonChico(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(70, 28));
        return btn;
    }

    private JLabel labelNegrita(String t, int size) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.BOLD, size));
        return l;
    }

    private JLabel labelNormal(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setForeground(Color.DARK_GRAY);
        return l;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FOTO: carga imagen desde C:\GymFat\fotos\{id}.jpg en un hilo
    // ─────────────────────────────────────────────────────────────────────────
    private void cargarFotoEnHilo(int idMiembro) {
        new Thread(() -> {
            // Buscar primero .jpg, luego .png
            File archivo = new File("C:\\GymFat\\fotos\\" + idMiembro + ".jpg");
            if (!archivo.exists()) {
                archivo = new File("C:\\GymFat\\fotos\\" + idMiembro + ".png");
            }

            final ImageIcon icono;
            if (archivo.exists()) {
                // Escalar la imagen a 110x120 manteniendo calidad
                ImageIcon raw = new ImageIcon(archivo.getAbsolutePath());
                Image scaled = raw.getImage().getScaledInstance(110, 120, Image.SCALE_SMOOTH);
                icono = new ImageIcon(scaled);
            } else {
                icono = fotoPlaceholder();
            }
            SwingUtilities.invokeLater(() -> lblFoto.setIcon(icono));
        }, "hilo-foto").start();
    }

    // Placeholder cuando no hay foto: silueta gris con texto
    private ImageIcon fotoPlaceholder() {
        int w = 110, h = 120;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo gris claro
        g2.setColor(new Color(230, 230, 230));
        g2.fillRect(0, 0, w, h);

        // Círculo cabeza
        g2.setColor(new Color(180, 180, 180));
        g2.fillOval(35, 15, 40, 40);

        // Cuerpo
        g2.fillRoundRect(20, 60, 70, 45, 20, 20);

        // Texto "Sin foto"
        g2.setColor(new Color(130, 130, 130));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        String txt = "Sin foto";
        g2.drawString(txt, (w - fm.stringWidth(txt)) / 2, h - 5);

        g2.dispose();
        return new ImageIcon(img);
    }

}
