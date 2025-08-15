package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.modelo.Recurso;
import com.mycompany.pms.java.swing.servicio.ProyectoService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RecursoDialog extends JDialog {
    private Recurso recurso;
    private boolean aceptado = false;
    private final ProyectoService proyectoService;
    
    private JComboBox<Proyecto> cmbProyecto;
    private JTextField txtNombre;
    private JComboBox<String> cmbTipo;
    private JTextField txtUrl;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private JButton btnProbarUrl;
    
    public RecursoDialog(Frame parent, Recurso recursoExistente, ProyectoService proyectoService) {
        super(parent, recursoExistente == null ? "Nuevo Recurso" : "Editar Recurso", true);
        this.recurso = recursoExistente;
        this.proyectoService = proyectoService;
        initComponents();
        configurarDialog();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Título
        JLabel lblTitulo = new JLabel(recurso == null ? "Agregar Nuevo Recurso" : "Editar Recurso");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
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
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cmbProyecto, gbc);
        
        // Nombre del recurso
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Nombre:"), gbc);
        
        txtNombre = new JTextField(25);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtNombre, gbc);
        
        // Tipo de recurso
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Tipo:"), gbc);
        
        String[] tipos = {
            "Documento", "Enlace Web", "Imagen", "Video", "Audio", 
            "Archivo", "Repositorio", "API", "Base de Datos", "Otro"
        };
        cmbTipo = new JComboBox<>(tipos);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cmbTipo, gbc);
        
        // URL
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("URL:"), gbc);
        
        txtUrl = new JTextField(20);
        txtUrl.setToolTipText("Ejemplo: https://ejemplo.com/archivo.pdf");
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtUrl, gbc);
        
        btnProbarUrl = new JButton("🔗 Probar");
        btnProbarUrl.setToolTipText("Verificar que la URL es válida");
        btnProbarUrl.addActionListener(this::probarUrl);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        add(btnProbarUrl, gbc);
        
        // Información adicional
        JTextArea txtInfo = new JTextArea(3, 25);
        txtInfo.setText("💡 Consejos:\n" +
                "• Use URLs completas (https://...)\n" +
                "• Para archivos locales, use file:///ruta/al/archivo\n" +
                "• Para documentos de Google Drive, use el enlace de compartir");
        txtInfo.setEditable(false);
        txtInfo.setBackground(new Color(248, 249, 250));
        txtInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(txtInfo, gbc);
        
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
        
        btnProbarUrl.setBackground(new Color(23, 162, 184));
        btnProbarUrl.setForeground(Color.WHITE);
        btnProbarUrl.setFocusPainted(false);
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
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
            
            // Si es edición, cargar datos existentes
            if (recurso != null) {
                txtNombre.setText(recurso.getNombre());
                txtUrl.setText(recurso.getUrl());
                
                // Seleccionar tipo
                if (recurso.getTipo() != null) {
                    cmbTipo.setSelectedItem(recurso.getTipo());
                }
                
                // Seleccionar proyecto
                for (int i = 0; i < cmbProyecto.getItemCount(); i++) {
                    Proyecto p = cmbProyecto.getItemAt(i);
                    if (p.getIdProyecto().equals(recurso.getIdProyecto())) {
                        cmbProyecto.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                // Valores por defecto para nuevo recurso
                cmbTipo.setSelectedItem("Enlace Web");
            }
            
        } catch (Exception e) {
            mostrarError("Error cargando datos: " + e.getMessage());
        }
    }
    
    private void probarUrl(ActionEvent e) {
        String url = txtUrl.getText().trim();
        if (url.isEmpty()) {
            mostrarError("Ingrese una URL para probar");
            return;
        }
        
        try {
            // Agregar protocolo si no lo tiene
            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
                url = "https://" + url;
                txtUrl.setText(url);
            }
            
            // Validar formato de URL
            new URL(url);
            
            // Intentar abrir en el navegador
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new java.net.URI(url));
                JOptionPane.showMessageDialog(this, 
                    "✅ URL válida y abierta en el navegador", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "✅ URL válida (no se puede abrir automáticamente)", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (MalformedURLException ex) {
            mostrarError("❌ Formato de URL inválido:\n" + ex.getMessage());
        } catch (Exception ex) {
            mostrarError("❌ Error al abrir URL:\n" + ex.getMessage());
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
        
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre del recurso es obligatorio");
            txtNombre.requestFocus();
            return;
        }
        
        if (nombre.length() > 150) {
            mostrarError("El nombre no puede exceder 150 caracteres");
            txtNombre.requestFocus();
            return;
        }
        
        String url = txtUrl.getText().trim();
        if (!url.isEmpty()) {
            // Validar formato de URL básico
            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
                url = "https://" + url;
                txtUrl.setText(url);
            }
            
            try {
                new URL(url);
            } catch (MalformedURLException ex) {
                mostrarError("Formato de URL inválido. Ejemplo: https://ejemplo.com");
                txtUrl.requestFocus();
                return;
            }
        }
        
        // Crear o actualizar recurso
        if (recurso == null) {
            recurso = new Recurso();
        }
        
        recurso.setIdProyecto(proyectoSeleccionado.getIdProyecto());
        recurso.setNombre(nombre);
        recurso.setTipo((String) cmbTipo.getSelectedItem());
        recurso.setUrl(url.isEmpty() ? null : url);
        
        aceptado = true;
        dispose();
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public Recurso getRecurso() {
        return aceptado ? recurso : null;
    }
}