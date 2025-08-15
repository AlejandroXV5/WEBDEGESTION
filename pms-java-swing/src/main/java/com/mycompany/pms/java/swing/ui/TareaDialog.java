package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.modelo.Tarea;
import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.ProyectoService;
import com.mycompany.pms.java.swing.servicio.UsuarioService;
import com.mycompany.pms.java.swing.dao.UsuarioDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TareaDialog extends JDialog {
    private Tarea tarea;
    private boolean aceptado = false;
    private final ProyectoService proyectoService;
    private final UsuarioService usuarioService;
    
    private JComboBox<Proyecto> cmbProyecto;
    private JTextField txtTitulo;
    private JTextArea txtDescripcion;
    private JComboBox<Usuario> cmbUsuarioAsignado;
    private JTextField txtFechaLimite;
    private JCheckBox chkCompletada;
    private JButton btnAceptar;
    private JButton btnCancelar;
    
    public TareaDialog(Frame parent, Tarea tareaExistente, ProyectoService proyectoService, UsuarioService usuarioService) {
        super(parent, tareaExistente == null ? "Nueva Tarea" : "Editar Tarea", true);
        this.tarea = tareaExistente;
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
        initComponents();
        configurarDialog();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Título
        JLabel lblTitulo = new JLabel(tarea == null ? "Crear Nueva Tarea" : "Editar Tarea");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(lblTitulo, gbc);
        
        // Resetear configuración
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 20, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Proyecto
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Proyecto:"), gbc);
        
        cmbProyecto = new JComboBox<>();
        cmbProyecto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Proyecto) {
                    setText(((Proyecto) value).getNombre());
                }
                return this;
            }
        });
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cmbProyecto, gbc);
        
        // Título de la tarea
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Título:"), gbc);
        
        txtTitulo = new JTextField(25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtTitulo, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Descripción:"), gbc);
        
        txtDescripcion = new JTextArea(4, 25);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(scrollDesc, gbc);
        
        // Usuario asignado
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Asignado a:"), gbc);
        
        cmbUsuarioAsignado = new JComboBox<>();
        cmbUsuarioAsignado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    setText(((Usuario) value).getNombre());
                } else if (value == null) {
                    setText("Sin asignar");
                }
                return this;
            }
        });
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cmbUsuarioAsignado, gbc);
        
        // Fecha límite
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Fecha límite (YYYY-MM-DD):"), gbc);
        
        txtFechaLimite = new JTextField(25);
        txtFechaLimite.setToolTipText("Formato: 2024-12-31 (opcional)");
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtFechaLimite, gbc);
        
        // Completada
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Estado:"), gbc);
        
        chkCompletada = new JCheckBox("Tarea completada");
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(chkCompletada, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAceptar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(this::aceptar);
        btnCancelar.addActionListener(e -> dispose());
        
        // Estilo de botones
        btnAceptar.setBackground(new Color(40, 167, 69));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusPainted(false);
        
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(panelBotones, gbc);
        
        // Enter para aceptar
        getRootPane().setDefaultButton(btnAceptar);
    }
    
    private void configurarDialog() {
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void cargarDatos() {
        try {
            // Cargar proyectos
            List<Proyecto> proyectos = proyectoService.listarTodos();
            for (Proyecto proyecto : proyectos) {
                cmbProyecto.addItem(proyecto);
            }
            
            // Cargar usuarios
            cmbUsuarioAsignado.addItem(null); // Opción "Sin asignar"
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.listAll();
            for (Usuario usuario : usuarios) {
                cmbUsuarioAsignado.addItem(usuario);
            }
            
            // Si es edición, cargar datos existentes
            if (tarea != null) {
                txtTitulo.setText(tarea.getTitulo());
                txtDescripcion.setText(tarea.getDescripcion());
                chkCompletada.setSelected(tarea.isCompletada());
                
                if (tarea.getFechaLimite() != null) {
                    txtFechaLimite.setText(tarea.getFechaLimite().toString());
                }
                
                // Seleccionar proyecto
                for (int i = 0; i < cmbProyecto.getItemCount(); i++) {
                    Proyecto p = cmbProyecto.getItemAt(i);
                    if (p.getIdProyecto().equals(tarea.getIdProyecto())) {
                        cmbProyecto.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Seleccionar usuario asignado
                if (tarea.getIdUsuarioAsignado() != null) {
                    for (int i = 0; i < cmbUsuarioAsignado.getItemCount(); i++) {
                        Usuario u = cmbUsuarioAsignado.getItemAt(i);
                        if (u != null && u.getIdUsuario().equals(tarea.getIdUsuarioAsignado())) {
                            cmbUsuarioAsignado.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } else {
                // Valores por defecto para nueva tarea
                txtFechaLimite.setText(LocalDate.now().plusDays(7).toString());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aceptar(ActionEvent e) {
        // Validaciones
        Proyecto proyectoSeleccionado = (Proyecto) cmbProyecto.getSelectedItem();
        if (proyectoSeleccionado == null) {
            mostrarError("Debe seleccionar un proyecto");
            cmbProyecto.requestFocus();
            return;
        }
        
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            mostrarError("El título es obligatorio");
            txtTitulo.requestFocus();
            return;
        }
        
        if (titulo.length() > 150) {
            mostrarError("El título no puede exceder 150 caracteres");
            txtTitulo.requestFocus();
            return;
        }
        
        // Validar fecha límite
        LocalDate fechaLimite = null;
        String fechaLimiteStr = txtFechaLimite.getText().trim();
        if (!fechaLimiteStr.isEmpty()) {
            try {
                fechaLimite = LocalDate.parse(fechaLimiteStr, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ex) {
                mostrarError("Formato de fecha límite inválido. Use YYYY-MM-DD");
                txtFechaLimite.requestFocus();
                return;
            }
        }
        
        // Crear o actualizar tarea
        if (tarea == null) {
            tarea = new Tarea();
        }
        
        tarea.setIdProyecto(proyectoSeleccionado.getIdProyecto());
        tarea.setTitulo(titulo);
        tarea.setDescripcion(txtDescripcion.getText().trim());
        tarea.setFechaLimite(fechaLimite);
        tarea.setCompletada(chkCompletada.isSelected());
        
        Usuario usuarioSeleccionado = (Usuario) cmbUsuarioAsignado.getSelectedItem();
        tarea.setIdUsuarioAsignado(usuarioSeleccionado != null ? usuarioSeleccionado.getIdUsuario() : null);
        
        aceptado = true;
        dispose();
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public Tarea getTarea() {
        return aceptado ? tarea : null;
    }
}