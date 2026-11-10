/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gymfat;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author SoaringLatitute2
 */
public class AddMember extends JPanel implements ActionListener {

    JTextField txtf_nombres, txtf_apellido_pat, txtf_apellido_mat;
    JTextField txtf_fecha_nac, txtf_correo, txtf_telefono;
    JTextField txtf_contacto_emerg, txtf_tel_emerg, txtf_relacion;
    JRadioButton rb_si_entrenador, rb_no_entrenador;
    ButtonGroup bg_entrenador;
    JTextField txtf_objetivo;
    JCheckBox cb_yoga, cb_crossfit, cb_spinning, cb_hiit, cb_zumba, cb_pilates;
    JCheckBox cb_lunes, cb_martes, cb_miercoles, cb_jueves, cb_viernes, cb_sabado, cb_domingo;
    JRadioButton rb_manana, rb_tarde, rb_noche;
    ButtonGroup bg_horario;
    JComboBox<String> cmb_membresia;
    JComboBox<String> cmb_entrenador;
    JRadioButton rb_si_condicion, rb_no_condicion;
    ButtonGroup bg_condicion;
    JTextArea txtarea_condicion;
    JRadioButton rb_si_lesion, rb_no_lesion;
    ButtonGroup bg_lesion;
    JTextArea txtarea_lesion;
    JTextField txtf_estatura, txtf_peso;
    JCheckBox cb_terminos;
    JButton btn_registrar;

    private File foto_seleccionada = null;
    private JLabel lbl_preview_foto;

    List<Integer> id_planes = new ArrayList<>();
    List<Integer> id_entrenadores = new ArrayList<>();
    List<Integer> id_clases = new ArrayList<>();
    List<Integer> id_dias = new ArrayList<>();

    static final Color AMARILLO = new Color(255, 220, 0);
    static final Color FONDO = new Color(248, 248, 248);
    static final Color BORDE_SEC = new Color(235, 235, 235);

    public AddMember() {
        setLayout(new BorderLayout());
        setBackground(FONDO);
        initComponents();
        loadPlans();
        loadTrainers();
        loadClassesAndDays();
    }

    private void initComponents() {
        JLabel lbl_titulo = new JLabel("Registro de nuevo miembro");
        lbl_titulo.setFont(new Font("Arial", Font.BOLD, 24));
        lbl_titulo.setForeground(new Color(20, 20, 20));
        lbl_titulo.setBorder(BorderFactory.createEmptyBorder(24, 40, 10, 40));
        add(lbl_titulo, BorderLayout.NORTH);

        JPanel form_panel = new JPanel();
        form_panel.setLayout(new BoxLayout(form_panel, BoxLayout.Y_AXIS));
        form_panel.setBackground(FONDO);
        form_panel.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));

        form_panel.add(createSection("1. Datos Personales", createPersonalDataPanel()));
        form_panel.add(createGap(10));
        form_panel.add(createSection("2. Entrenador y Clases", createTrainerPanel()));
        form_panel.add(createGap(10));
        form_panel.add(createSection("3. Plan de Membresía", createMembershipPanel()));
        form_panel.add(createGap(10));
        form_panel.add(createSection("4. Salud (Confidencial)", createHealthPanel()));
        form_panel.add(createGap(10));
        form_panel.add(createSection("5. Términos y Consentimiento", createTermsPanel()));
        form_panel.add(createGap(10));
        form_panel.add(createButtonPanel());

        JScrollPane scroll = new JScrollPane(form_panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createSection(String titulo, JPanel contenido) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, AMARILLO),
                BorderFactory.createLineBorder(BORDE_SEC, 1)
        ));

        JLabel lbl = new JLabel("  " + titulo);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(50, 50, 50));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 10));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));

        JPanel north_panel = new JPanel(new BorderLayout());
        north_panel.setBackground(Color.WHITE);
        north_panel.add(lbl, BorderLayout.CENTER);
        north_panel.add(sep, BorderLayout.SOUTH);

        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 20, 16, 20));

        wrapper.add(north_panel, BorderLayout.NORTH);
        wrapper.add(contenido, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createPersonalDataPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel fila_fecha = createFlowRow();
        fila_fecha.add(createLabel("Fecha de registro: "));
        JLabel lbl_fecha = new JLabel(LocalDate.now().toString());
        lbl_fecha.setFont(new Font("Arial", Font.BOLD, 13));
        fila_fecha.add(lbl_fecha);
        panel.add(fila_fecha);
        panel.add(createGap(8));

        panel.add(createGridRow(
                new String[]{"Nombres *"},
                new JTextField[]{txtf_nombres = createField(0)}
        ));
        panel.add(createGap(8));

        panel.add(createGridRow(
                new String[]{"Apellido paterno *", "Apellido materno"},
                new JTextField[]{txtf_apellido_pat = createField(0), txtf_apellido_mat = createField(0)}
        ));
        panel.add(createGap(8));

        panel.add(createGridRow(
                new String[]{"Fecha nacimiento (YYYY-MM-DD)", "Correo electrónico"},
                new JTextField[]{txtf_fecha_nac = createField(0), txtf_correo = createField(0)}
        ));
        panel.add(createGap(8));

        panel.add(createGridRow(
                new String[]{"Teléfono"},
                new JTextField[]{txtf_telefono = createField(0)}
        ));
        panel.add(createGap(8));

        panel.add(createGridRow(
                new String[]{"Contacto de emergencia *"},
                new JTextField[]{txtf_contacto_emerg = createField(0)}
        ));
        panel.add(createGap(8));

        panel.add(createGridRow(
                new String[]{"Tel. emergencia *", "Relación"},
                new JTextField[]{txtf_tel_emerg = createField(0), txtf_relacion = createField(0)}
        ));
        panel.add(createGap(12));

        JPanel fila_foto = createFlowRow();
        fila_foto.add(createLabel("Foto del miembro:"));

        lbl_preview_foto = new JLabel("Sin foto", SwingConstants.CENTER);
        lbl_preview_foto.setPreferredSize(new Dimension(70, 80));
        lbl_preview_foto.setFont(new Font("Arial", Font.ITALIC, 10));
        lbl_preview_foto.setForeground(Color.GRAY);
        lbl_preview_foto.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        fila_foto.add(lbl_preview_foto);

        JButton btn_foto = new JButton("Seleccionar...");
        btn_foto.setFont(new Font("Arial", Font.PLAIN, 12));
        btn_foto.setBackground(new Color(240, 240, 240));
        btn_foto.setFocusPainted(false);
        btn_foto.addActionListener(e -> selectPhoto());

        JButton btn_quitar_foto = new JButton("Quitar");
        btn_quitar_foto.setFont(new Font("Arial", Font.PLAIN, 11));
        btn_quitar_foto.setForeground(Color.RED);
        btn_quitar_foto.setBorderPainted(false);
        btn_quitar_foto.setContentAreaFilled(false);
        btn_quitar_foto.setFocusPainted(false);
        btn_quitar_foto.addActionListener(e -> {
            foto_seleccionada = null;
            lbl_preview_foto.setIcon(null);
            lbl_preview_foto.setText("Sin foto");
        });

        fila_foto.add(btn_foto);
        fila_foto.add(btn_quitar_foto);
        panel.add(fila_foto);

        return panel;
    }

    private void selectPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar foto del miembro");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imágenes (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            foto_seleccionada = chooser.getSelectedFile();
            ImageIcon raw = new ImageIcon(foto_seleccionada.getAbsolutePath());
            Image scaled = raw.getImage().getScaledInstance(70, 80, Image.SCALE_SMOOTH);
            lbl_preview_foto.setIcon(new ImageIcon(scaled));
            lbl_preview_foto.setText("");
        }
    }

    private void copyPhoto(int id_miembro) {
        if (foto_seleccionada == null) {
            return;
        }
        File carpeta = new File("C:\\GymFat\\fotos");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        File destino = new File(carpeta, id_miembro + ".jpg");
        try {
            Files.copy(foto_seleccionada.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Miembro registrado, pero no se pudo copiar la foto: " + ex.getMessage(),
                    "Advertencia foto", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createTrainerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel fila1 = createFlowRow();
        rb_si_entrenador = new JRadioButton("Sí, deseo entrenador personal");
        rb_no_entrenador = new JRadioButton("No por el momento");
        rb_no_entrenador.setSelected(true);
        bg_entrenador = new ButtonGroup();
        bg_entrenador.add(rb_si_entrenador);
        bg_entrenador.add(rb_no_entrenador);
        rb_si_entrenador.setOpaque(false);
        rb_no_entrenador.setOpaque(false);
        fila1.add(rb_si_entrenador);
        fila1.add(rb_no_entrenador);
        panel.add(fila1);
        panel.add(createGap(8));

        JPanel fila2 = createFlowRow();
        fila2.add(createLabel("Entrenador:"));
        cmb_entrenador = new JComboBox<>();
        cmb_entrenador.setPreferredSize(new Dimension(220, 30));
        fila2.add(cmb_entrenador);
        panel.add(fila2);
        panel.add(createGap(8));

        panel.add(createGridRow(
                new String[]{"Objetivo principal"},
                new JTextField[]{txtf_objetivo = createField(0)}
        ));
        panel.add(createGap(10));

        JPanel fila_clases = createFlowRow();
        fila_clases.add(createLabel("Clases de interés:  "));
        cb_yoga = new JCheckBox("Yoga");
        cb_yoga.setOpaque(false);
        cb_crossfit = new JCheckBox("CrossFit");
        cb_crossfit.setOpaque(false);
        cb_spinning = new JCheckBox("Spinning");
        cb_spinning.setOpaque(false);
        cb_hiit = new JCheckBox("HIIT");
        cb_hiit.setOpaque(false);
        cb_zumba = new JCheckBox("Zumba");
        cb_zumba.setOpaque(false);
        cb_pilates = new JCheckBox("Pilates");
        cb_pilates.setOpaque(false);
        for (JCheckBox cb : new JCheckBox[]{cb_yoga, cb_crossfit, cb_spinning, cb_hiit, cb_zumba, cb_pilates}) {
            fila_clases.add(cb);
        }
        panel.add(fila_clases);
        panel.add(createGap(6));

        JPanel fila_dias = createFlowRow();
        fila_dias.add(createLabel("Días disponibles:  "));
        cb_lunes = new JCheckBox("Lunes");
        cb_lunes.setOpaque(false);
        cb_martes = new JCheckBox("Martes");
        cb_martes.setOpaque(false);
        cb_miercoles = new JCheckBox("Miércoles");
        cb_miercoles.setOpaque(false);
        cb_jueves = new JCheckBox("Jueves");
        cb_jueves.setOpaque(false);
        cb_viernes = new JCheckBox("Viernes");
        cb_viernes.setOpaque(false);
        cb_sabado = new JCheckBox("Sábado");
        cb_sabado.setOpaque(false);
        cb_domingo = new JCheckBox("Domingo");
        cb_domingo.setOpaque(false);
        for (JCheckBox cb : new JCheckBox[]{cb_lunes, cb_martes, cb_miercoles, cb_jueves, cb_viernes, cb_sabado, cb_domingo}) {
            fila_dias.add(cb);
        }
        panel.add(fila_dias);
        panel.add(createGap(6));

        JPanel fila_horario = createFlowRow();
        fila_horario.add(createLabel("Horario preferido:  "));
        rb_manana = new JRadioButton("Mañana (6-12h)");
        rb_manana.setOpaque(false);
        rb_tarde = new JRadioButton("Tarde (12-17h)");
        rb_tarde.setOpaque(false);
        rb_noche = new JRadioButton("Noche (17-22h)");
        rb_noche.setOpaque(false);
        bg_horario = new ButtonGroup();
        bg_horario.add(rb_manana);
        bg_horario.add(rb_tarde);
        bg_horario.add(rb_noche);
        fila_horario.add(rb_manana);
        fila_horario.add(rb_tarde);
        fila_horario.add(rb_noche);
        panel.add(fila_horario);

        return panel;
    }

    private JPanel createMembershipPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel fila = createFlowRow();
        fila.add(createLabel("Plan de membresía *:  "));
        cmb_membresia = new JComboBox<>();
        cmb_membresia.setPreferredSize(new Dimension(320, 30));
        fila.add(cmb_membresia);
        panel.add(fila);
        return panel;
    }

    private JPanel createHealthPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel fila1 = createFlowRow();
        fila1.add(createLabel("¿Padece alguna condición médica?  "));
        rb_si_condicion = new JRadioButton("Sí");
        rb_si_condicion.setOpaque(false);
        rb_no_condicion = new JRadioButton("No");
        rb_no_condicion.setSelected(true);
        rb_no_condicion.setOpaque(false);
        bg_condicion = new ButtonGroup();
        bg_condicion.add(rb_si_condicion);
        bg_condicion.add(rb_no_condicion);
        fila1.add(rb_si_condicion);
        fila1.add(rb_no_condicion);
        panel.add(fila1);
        panel.add(createGap(6));

        JPanel panel_condicion = new JPanel(new BorderLayout());
        panel_condicion.setOpaque(false);
        JLabel lbl_condicion = new JLabel("En caso afirmativo, especifique:");
        lbl_condicion.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl_condicion.setForeground(new Color(100, 100, 100));
        txtarea_condicion = new JTextArea(2, 0);
        txtarea_condicion.setFont(new Font("Arial", Font.PLAIN, 13));
        txtarea_condicion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        txtarea_condicion.setLineWrap(true);
        panel_condicion.add(lbl_condicion, BorderLayout.NORTH);
        panel_condicion.add(txtarea_condicion, BorderLayout.CENTER);
        panel_condicion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panel_condicion);
        panel.add(createGap(10));

        JPanel fila2 = createFlowRow();
        fila2.add(createLabel("¿Ha tenido lesiones recientes?  "));
        rb_si_lesion = new JRadioButton("Sí");
        rb_si_lesion.setOpaque(false);
        rb_no_lesion = new JRadioButton("No");
        rb_no_lesion.setSelected(true);
        rb_no_lesion.setOpaque(false);
        bg_lesion = new ButtonGroup();
        bg_lesion.add(rb_si_lesion);
        bg_lesion.add(rb_no_lesion);
        fila2.add(rb_si_lesion);
        fila2.add(rb_no_lesion);
        panel.add(fila2);
        panel.add(createGap(6));

        JPanel panel_lesion = new JPanel(new BorderLayout());
        panel_lesion.setOpaque(false);
        JLabel lbl_lesion = new JLabel("En caso afirmativo, describa:");
        lbl_lesion.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl_lesion.setForeground(new Color(100, 100, 100));
        txtarea_lesion = new JTextArea(2, 0);
        txtarea_lesion.setFont(new Font("Arial", Font.PLAIN, 13));
        txtarea_lesion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        txtarea_lesion.setLineWrap(true);
        panel_lesion.add(lbl_lesion, BorderLayout.NORTH);
        panel_lesion.add(txtarea_lesion, BorderLayout.CENTER);
        panel_lesion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panel_lesion);
        panel.add(createGap(10));

        panel.add(createGridRow(
                new String[]{"Estatura (cm)", "Peso (kg)"},
                new JTextField[]{txtf_estatura = createField(0), txtf_peso = createField(0)}
        ));

        return panel;
    }

    private JPanel createTermsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextArea txt_terminos = new JTextArea(
                "He leído y acepto el reglamento interno de GymFat, las políticas de uso de "
                + "instalaciones y las condiciones de mi plan de membresía. Declaro que la "
                + "información proporcionada es verdadera y completa.");
        txt_terminos.setEditable(false);
        txt_terminos.setLineWrap(true);
        txt_terminos.setWrapStyleWord(true);
        txt_terminos.setOpaque(false);
        txt_terminos.setFont(new Font("Arial", Font.PLAIN, 12));
        txt_terminos.setForeground(new Color(90, 90, 90));
        txt_terminos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txt_terminos);
        panel.add(createGap(10));

        JPanel fila = createFlowRow();
        cb_terminos = new JCheckBox("Acepto los términos y condiciones");
        cb_terminos.setFont(new Font("Arial", Font.BOLD, 13));
        cb_terminos.setOpaque(false);
        fila.add(cb_terminos);
        panel.add(fila);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);
        btn_registrar = new JButton("Registrar Miembro");
        btn_registrar.setBackground(new Color(20, 20, 20));
        btn_registrar.setForeground(AMARILLO);
        btn_registrar.setFont(new Font("Arial", Font.BOLD, 14));
        btn_registrar.setBorderPainted(false);
        btn_registrar.setFocusPainted(false);
        btn_registrar.setPreferredSize(new Dimension(230, 44));
        btn_registrar.addActionListener(this);
        panel.add(btn_registrar);
        return panel;
    }

    private JPanel createGridRow(String[] labels, JTextField[] fields) {
        JPanel panel = new JPanel(new GridLayout(1, labels.length, 12, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        for (int i = 0; i < labels.length; i++) {
            JPanel col = new JPanel();
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.setOpaque(false);
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.PLAIN, 12));
            lbl.setForeground(new Color(80, 80, 80));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            fields[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            fields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            col.add(lbl);
            col.add(Box.createRigidArea(new Dimension(0, 3)));
            col.add(fields[i]);
            panel.add(col);
        }
        return panel;
    }

    private JPanel createFlowRow() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(new Color(60, 60, 60));
        return lbl;
    }

    private JTextField createField(int cols) {
        JTextField field = cols > 0 ? new JTextField(cols) : new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return field;
    }

    private Component createGap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    private void loadPlans() {
        try {
            PreparedStatement ps = ConnectionDB.connect().prepareStatement(
                    "SELECT id_plan, nombre_plan, precio_plan FROM planes ORDER BY duracion_dias");
            ResultSet rs = ps.executeQuery();
            cmb_membresia.removeAllItems();
            id_planes.clear();
            while (rs.next()) {
                id_planes.add(rs.getInt("id_plan"));
                cmb_membresia.addItem(rs.getString("nombre_plan") + " — $" + rs.getString("precio_plan"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar planes: " + ex.getMessage());
        }
    }

    private void loadTrainers() {
        try {
            PreparedStatement ps = ConnectionDB.connect().prepareStatement(
                    "SELECT id_entrenador, nombre FROM entrenador ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            cmb_entrenador.removeAllItems();
            id_entrenadores.clear();
            cmb_entrenador.addItem("Sin entrenador");
            id_entrenadores.add(-1);
            while (rs.next()) {
                id_entrenadores.add(rs.getInt("id_entrenador"));
                cmb_entrenador.addItem(rs.getString("nombre"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar entrenadores: " + ex.getMessage());
        }
    }

    private void loadClassesAndDays() {
        try {
            ResultSet rs = ConnectionDB.connect().prepareStatement(
                    "SELECT id_clase FROM clase ORDER BY id_clase").executeQuery();
            id_clases.clear();
            while (rs.next()) {
                id_clases.add(rs.getInt("id_clase"));
            }
            rs = ConnectionDB.connect().prepareStatement(
                    "SELECT id_dia FROM dia ORDER BY id_dia").executeQuery();
            id_dias.clear();
            while (rs.next()) {
                id_dias.add(rs.getInt("id_dia"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clases/dias: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (ac.equals("Registrar Miembro")) {
            registerMember();
        }
    }

    private void registerMember() {
        String nombres = txtf_nombres.getText().trim();
        String apellido_p = txtf_apellido_pat.getText().trim();
        String fecha_nac = txtf_fecha_nac.getText().trim();
        String correo = txtf_correo.getText().trim();
        String telefono = txtf_telefono.getText().trim();
        String contacto = txtf_contacto_emerg.getText().trim();
        String tel_emerg = txtf_tel_emerg.getText().trim();
        String estatura = txtf_estatura.getText().trim();
        String peso = txtf_peso.getText().trim();

        if (nombres.isEmpty() || apellido_p.isEmpty() || contacto.isEmpty() || tel_emerg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Llena todos los campos obligatorios (*).",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nombres.length() > 100) {
            JOptionPane.showMessageDialog(this, "Nombres: máximo 100 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (apellido_p.length() > 80) {
            JOptionPane.showMessageDialog(this, "Apellido paterno: máximo 80 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!txtf_apellido_mat.getText().trim().isEmpty() && txtf_apellido_mat.getText().trim().length() > 80) {
            JOptionPane.showMessageDialog(this, "Apellido materno: máximo 80 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!correo.isEmpty() && correo.length() > 150) {
            JOptionPane.showMessageDialog(this, "Correo: máximo 150 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!telefono.isEmpty() && telefono.length() > 20) {
            JOptionPane.showMessageDialog(this, "Teléfono: máximo 20 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (contacto.length() > 150) {
            JOptionPane.showMessageDialog(this, "Contacto emergencia: máximo 150 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tel_emerg.length() > 20) {
            JOptionPane.showMessageDialog(this, "Tel. emergencia: máximo 20 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!txtf_relacion.getText().trim().isEmpty() && txtf_relacion.getText().trim().length() > 80) {
            JOptionPane.showMessageDialog(this, "Relación: máximo 80 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!txtf_objetivo.getText().trim().isEmpty() && txtf_objetivo.getText().trim().length() > 200) {
            JOptionPane.showMessageDialog(this, "Objetivo: máximo 200 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!fecha_nac.isEmpty()) {
            try {
                LocalDate.parse(fecha_nac);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fecha inválida. Usa YYYY-MM-DD con valores reales.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        double estatura_val = 0, peso_val = 0;
        if (!estatura.isEmpty()) {
            try {
                estatura_val = Double.parseDouble(estatura);
                if (estatura_val <= 0 || estatura_val > 999.99) {
                    JOptionPane.showMessageDialog(this, "Estatura inválida (1-999.99).", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Estatura debe ser un número.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        if (!peso.isEmpty()) {
            try {
                peso_val = Double.parseDouble(peso);
                if (peso_val <= 0 || peso_val > 999.99) {
                    JOptionPane.showMessageDialog(this, "Peso inválido (1-999.99).", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Peso debe ser un número.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (!cb_terminos.isSelected()) {
            JOptionPane.showMessageDialog(this, "Debes aceptar los términos y condiciones.",
                    "Términos no aceptados", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = ConnectionDB.connect();
            String horario = rb_manana.isSelected() ? "Mañana (6-12h)"
                    : rb_tarde.isSelected() ? "Tarde (12-17h)" : "Noche (17-22h)";
            int id_entrenador = id_entrenadores.get(cmb_entrenador.getSelectedIndex());

            String sql_miembro = "INSERT INTO miembro (nombres, apellido_paterno, apellido_materno, "
                    + "fecha_nacimiento, correo_electronico, telefono, quiere_entrenador, "
                    + "objetivo_principal, horario_deseado, estatura, peso, id_entrenador, foto_ruta) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql_miembro, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombres);
            ps.setString(2, apellido_p);
            ps.setString(3, txtf_apellido_mat.getText().trim().isEmpty() ? null : txtf_apellido_mat.getText().trim());
            ps.setString(4, fecha_nac.isEmpty() ? null : fecha_nac);
            ps.setString(5, correo.isEmpty() ? null : correo);
            ps.setString(6, telefono.isEmpty() ? null : telefono);
            ps.setBoolean(7, rb_si_entrenador.isSelected());
            ps.setString(8, txtf_objetivo.getText().trim().isEmpty() ? null : txtf_objetivo.getText().trim());
            ps.setString(9, horario);
            ps.setObject(10, estatura.isEmpty() ? null : estatura_val);
            ps.setObject(11, peso.isEmpty() ? null : peso_val);
            if (id_entrenador == -1) {
                ps.setNull(12, Types.INTEGER);
            } else {
                ps.setInt(12, id_entrenador);
            }
            ps.setString(13, foto_seleccionada != null ? "PENDING" : null);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int id_miembro = -1;
            if (keys.next()) {
                id_miembro = keys.getInt(1);
            }

            if (foto_seleccionada != null && id_miembro != -1) {
                copyPhoto(id_miembro);
                PreparedStatement ps_ruta = con.prepareStatement(
                        "UPDATE miembro SET foto_ruta = ? WHERE id_miembro = ?");
                ps_ruta.setString(1, "C:\\GymFat\\fotos\\" + id_miembro + ".jpg");
                ps_ruta.setInt(2, id_miembro);
                ps_ruta.executeUpdate();
            }

            PreparedStatement ps_contacto = con.prepareStatement(
                    "INSERT INTO contacto_emergencia (id_miembro, nombre, telefono, relacion) VALUES (?, ?, ?, ?)");
            ps_contacto.setInt(1, id_miembro);
            ps_contacto.setString(2, contacto);
            ps_contacto.setString(3, tel_emerg);
            ps_contacto.setString(4, txtf_relacion.getText().trim().isEmpty() ? null : txtf_relacion.getText().trim());
            ps_contacto.executeUpdate();

            int id_plan = id_planes.get(cmb_membresia.getSelectedIndex());
            PreparedStatement ps_plan = con.prepareStatement(
                    "SELECT duracion_dias, precio_plan, nombre_plan FROM planes WHERE id_plan = ?");
            ps_plan.setInt(1, id_plan);
            ResultSet rs_plan = ps_plan.executeQuery();
            int duracion = 30;
            double precio = 0;
            String nombre_plan = "";
            if (rs_plan.next()) {
                duracion = rs_plan.getInt("duracion_dias");
                precio = rs_plan.getDouble("precio_plan");
                nombre_plan = rs_plan.getString("nombre_plan");
            }
            LocalDate hoy = LocalDate.now();
            LocalDate vencimiento = hoy.plusDays(duracion);

            PreparedStatement ps_mem = con.prepareStatement(
                    "INSERT INTO membresia (id_miembro, id_plan, fecha_inicio, fecha_vencimiento, estado) VALUES (?, ?, ?, ?, 'Activa')");
            ps_mem.setInt(1, id_miembro);
            ps_mem.setInt(2, id_plan);
            ps_mem.setDate(3, Date.valueOf(hoy));
            ps_mem.setDate(4, Date.valueOf(vencimiento));
            ps_mem.executeUpdate();

            PreparedStatement ps_salud = con.prepareStatement(
                    "INSERT INTO salud (id_miembro, tiene_condicion_medica, descripcion_condicion, tiene_lesiones) VALUES (?, ?, ?, ?)");
            ps_salud.setInt(1, id_miembro);
            ps_salud.setBoolean(2, rb_si_condicion.isSelected());
            ps_salud.setString(3, rb_si_condicion.isSelected() ? txtarea_condicion.getText().trim() : null);
            ps_salud.setBoolean(4, rb_si_lesion.isSelected());
            ps_salud.executeUpdate();

            JCheckBox[] clases = {cb_yoga, cb_crossfit, cb_spinning, cb_hiit, cb_zumba, cb_pilates};
            for (int i = 0; i < clases.length; i++) {
                if (clases[i].isSelected() && i < id_clases.size()) {
                    PreparedStatement ps_clase = con.prepareStatement(
                            "INSERT INTO miembro_clase (id_miembro, id_clase) VALUES (?, ?)");
                    ps_clase.setInt(1, id_miembro);
                    ps_clase.setInt(2, id_clases.get(i));
                    ps_clase.executeUpdate();
                }
            }

            JCheckBox[] dias = {cb_lunes, cb_martes, cb_miercoles, cb_jueves, cb_viernes, cb_sabado, cb_domingo};
            for (int i = 0; i < dias.length; i++) {
                if (dias[i].isSelected() && i < id_dias.size()) {
                    PreparedStatement ps_dia = con.prepareStatement(
                            "INSERT INTO miembro_dia (id_miembro, id_dia) VALUES (?, ?)");
                    ps_dia.setInt(1, id_miembro);
                    ps_dia.setInt(2, id_dias.get(i));
                    ps_dia.executeUpdate();
                }
            }

            generateReceipt(id_miembro, nombres, apellido_p,
                    txtf_apellido_mat.getText().trim(),
                    correo, telefono,
                    nombre_plan, hoy, vencimiento, precio);

            JOptionPane.showMessageDialog(this, "Miembro registrado correctamente. ID: " + id_miembro,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        for (JTextField f : new JTextField[]{txtf_nombres, txtf_apellido_pat, txtf_apellido_mat,
            txtf_fecha_nac, txtf_correo, txtf_telefono, txtf_contacto_emerg,
            txtf_tel_emerg, txtf_relacion, txtf_objetivo, txtf_estatura, txtf_peso}) {
            f.setText("");
        }
        txtarea_condicion.setText("");
        txtarea_lesion.setText("");
        rb_no_entrenador.setSelected(true);
        rb_no_condicion.setSelected(true);
        rb_no_lesion.setSelected(true);
        cb_terminos.setSelected(false);
        for (JCheckBox cb : new JCheckBox[]{cb_yoga, cb_crossfit, cb_spinning, cb_hiit, cb_zumba, cb_pilates,
            cb_lunes, cb_martes, cb_miercoles, cb_jueves, cb_viernes, cb_sabado, cb_domingo}) {
            cb.setSelected(false);
        }
        cmb_membresia.setSelectedIndex(0);
        cmb_entrenador.setSelectedIndex(0);
        foto_seleccionada = null;
        lbl_preview_foto.setIcon(null);
        lbl_preview_foto.setText("Sin foto");
    }

    private void generateReceipt(int id_miembro, String nombres, String apellido_p,
            String apellido_m, String correo, String telefono,
            String plan, LocalDate fecha_inicio, LocalDate fecha_vencimiento,
            double precio) {

        File carpeta = new File("C:\\GymFat\\recibos");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        File archivo = new File(carpeta, "recibo_" + id_miembro + ".txt");
        String fname = archivo.getAbsolutePath();

        FileWriter output = null;
        try {
            output = new FileWriter(fname);
            BufferedWriter bw = new BufferedWriter(output);

            bw.write("========================================");
            bw.newLine();
            bw.write("        GYMFAT - COMPROBANTE DE PAGO    ");
            bw.newLine();
            bw.write("========================================");
            bw.newLine();
            bw.write("Fecha de expedicion: " + LocalDate.now());
            bw.newLine();
            bw.write("No. de miembro:      " + id_miembro);
            bw.newLine();
            bw.write("----------------------------------------");
            bw.newLine();
            bw.write("DATOS DEL MIEMBRO");
            bw.newLine();
            bw.write("Nombre:   " + nombres + " " + apellido_p + " " + (apellido_m.isEmpty() ? "" : apellido_m));
            bw.newLine();
            bw.write("Correo:   " + (correo.isEmpty() ? "No registrado" : correo));
            bw.newLine();
            bw.write("Telefono: " + (telefono.isEmpty() ? "No registrado" : telefono));
            bw.newLine();
            bw.write("----------------------------------------");
            bw.newLine();
            bw.write("DETALLE DE MEMBRESIA");
            bw.newLine();
            bw.write("Plan:         " + plan);
            bw.newLine();
            bw.write("Inicio:       " + fecha_inicio);
            bw.newLine();
            bw.write("Vencimiento:  " + fecha_vencimiento);
            bw.newLine();
            bw.write("Total:        $" + String.format("%.2f", precio));
            bw.newLine();
            bw.write("========================================");
            bw.newLine();
            bw.write("      Gracias por unirte a GymFat!      ");
            bw.newLine();
            bw.write("========================================");

            bw.close();
            JOptionPane.showMessageDialog(this,
                    "Recibo generado en:\n" + fname,
                    "Recibo generado", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el recibo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    System.out.println("Error cerrando archivo: " + ex.getMessage());
                }
            }
        }
    }
}
