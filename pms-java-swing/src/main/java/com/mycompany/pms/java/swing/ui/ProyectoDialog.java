package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Proyecto;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ProyectoDialog extends JDialog {
    private Proyecto proyecto;
    private boolean aceptado = false;
    
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JComboBox<String> cmbEstado;
    private JButton btnAceptar;
    private JButton btnCancelar;
    
    public ProyectoDialog(Frame parent, Proyecto proyectoExistente) {
        super(parent, proyectoExistente == null ? "Nuevo Proyecto" : "Editar Proyecto", true);
        this.proyecto = proyectoExistente;
        initComponents();
        configurarDialog();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Título
        JLabel lblTitulo = new JLabel(proyecto == null ? "Crear Nuevo Proyecto" : "Editar Proyecto");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(lblTitulo, gbc);
        
        // Resetear configuración
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 20, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Nombre del proyecto:"), gbc);
        
        txtNombre = new JTextField(25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtNombre, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Descripción:"), gbc);
        
        txtDescripcion = new JTextArea(4, 25);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(scrollDesc, gbc);
        
        // Fecha inicio
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Fecha inicio (YYYY-MM-DD):"), gbc);
        
        txtFechaInicio = new JTextField(25);
        txtFechaInicio.setToolTipText("Formato: 2024-01-15");
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtFechaInicio, gbc);
        
        // Fecha fin
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Fecha fin (YYYY-MM-DD):"), gbc);
        
        txtFechaFin = new JTextField(25);
        txtFechaFin.setToolTipText("Formato: 2024-12-31");
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtFechaFin, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Estado:"), gbc);
        
        cmbEstado = new JComboBox<>(new String[]{"PENDIENTE", "EN_PROGRESO", "COMPLETADO"});
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cmbEstado, gbc);
        
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
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
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
        if (proyecto != null) {
            txtNombre.setText(proyecto.getNombre());
            txtDescripcion.setText(proyecto.getDescripcion());
            
            if (proyecto.getFechaInicio() != null) {
                txtFechaInicio.setText(proyecto.getFechaInicio().toString());
            }
            
            if (proyecto.getFechaFin() != null) {
                txtFechaFin.setText(proyecto.getFechaFin().toString());
            }
            
            cmbEstado.setSelectedItem(proyecto.getEstado());
        } else {
            // Valores por defecto para nuevo proyecto
            txtFechaInicio.setText(LocalDate.now().toString());
            txtFechaFin.setText(LocalDate.now().plusDays(30).toString());
            cmbEstado.setSelectedItem("PENDIENTE");
        }
    }
    
    private void aceptar(ActionEvent e) {
        // Validaciones
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre del proyecto es obligatorio");
            txtNombre.requestFocus();
            return;
        }
        
        if (nombre.length() > 150) {
            mostrarError("El nombre no puede exceder 150 caracteres");
            txtNombre.requestFocus();
            return;
        }
        
        // Validar fechas
        LocalDate fechaInicio = null;
        LocalDate fechaFin = null;
        
        try {
            String fechaInicioStr = txtFechaInicio.getText().trim();
            if (!fechaInicioStr.isEmpty()) {
                fechaInicio = LocalDate.parse(fechaInicioStr, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException ex) {
            mostrarError("Formato de fecha de inicio inválido. Use YYYY-MM-DD");
            txtFechaInicio.requestFocus();
            return;
        }
        
        try {
            String fechaFinStr = txtFechaFin.getText().trim();
            if (!fechaFinStr.isEmpty()) {
                fechaFin = LocalDate.parse(fechaFinStr, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException ex) {
            mostrarError("Formato de fecha de fin inválido. Use YYYY-MM-DD");
            txtFechaFin.requestFocus();
            return;
        }
        
        // Validar que fecha fin sea posterior a fecha inicio
        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            mostrarError("La fecha de fin no puede ser anterior a la fecha de inicio");
            txtFechaFin.requestFocus();
            return;
        }
        
        // Crear o actualizar proyecto
        if (proyecto == null) {
            proyecto = new Proyecto();
        }
        
        proyecto.setNombre(nombre);
        proyecto.setDescripcion(txtDescripcion.getText().trim());
        proyecto.setFechaInicio(fechaInicio);
        proyecto.setFechaFin(fechaFin);
        proyecto.setEstado((String) cmbEstado.getSelectedItem());
        
        aceptado = true;
        dispose();
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public Proyecto getProyecto() {
        return aceptado ? proyecto : null;
    }
}