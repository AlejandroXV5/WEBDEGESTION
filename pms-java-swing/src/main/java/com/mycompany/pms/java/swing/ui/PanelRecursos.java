package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.modelo.Recurso;
import com.mycompany.pms.java.swing.servicio.ProyectoService;
import com.mycompany.pms.java.swing.servicio.RecursoService;
import com.mycompany.pms.java.swing.dao.RecursoDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class PanelRecursos extends JPanel {
    private final RecursoService recursoService;
    private final ProyectoService proyectoService;
    private final MainFrame mainFrame;
    
    private JTable tablaRecursos;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo, btnEditar, btnEliminar, btnAbrir;
    private JComboBox<Proyecto> cmbFiltroProyecto;
    
    public PanelRecursos(RecursoService recursoService, ProyectoService proyectoService, MainFrame mainFrame) {
        this.recursoService = recursoService;
        this.proyectoService = proyectoService;
        this.mainFrame = mainFrame;
        initComponents();
        cargarProyectos();
        cargarRecursos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel superior con filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.add(new JLabel("Filtrar por proyecto:"));
        
        cmbFiltroProyecto = new JComboBox<>();
        cmbFiltroProyecto.addActionListener(e -> cargarRecursos());
        panelFiltros.add(cmbFiltroProyecto);
        
        JButton btnLimpiarFiltro = new JButton("Mostrar Todos");
        btnLimpiarFiltro.addActionListener(e -> {
            cmbFiltroProyecto.setSelectedIndex(0);
            cargarRecursos();
        });
        panelFiltros.add(btnLimpiarFiltro);
        
        add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de recursos
        String[] columnas = {"ID", "Proyecto", "Nombre", "Tipo", "URL"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaRecursos = new JTable(modeloTabla);
        tablaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Ocultar columna ID
        tablaRecursos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaRecursos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaRecursos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Ajustar anchos de columnas
        tablaRecursos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaRecursos.getColumnModel().getColumn(2).setPreferredWidth(200);
        tablaRecursos.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaRecursos.getColumnModel().getColumn(4).setPreferredWidth(300);
        
        // Renderer personalizado para URLs (azul y subrayado)
        tablaRecursos.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && value != null) {
                    setForeground(Color.BLUE);
                    setText("<html><u>" + value.toString() + "</u></html>");
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                
                return c;
            }
        });
        
        // Doble clic para abrir URL
        tablaRecursos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaRecursos.getSelectedRow();
                    if (fila != -1) {
                        abrirRecurso();
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaRecursos);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnNuevo = new JButton("➕ Nuevo Recurso");
        btnEditar = new JButton("✏️ Editar");
        btnEliminar = new JButton("🗑️ Eliminar");
        btnAbrir = new JButton("🔗 Abrir URL");
        
        btnNuevo.addActionListener(this::nuevoRecurso);
        btnEditar.addActionListener(this::editarRecurso);
        btnEliminar.addActionListener(this::eliminarRecurso);
        btnAbrir.addActionListener(e -> abrirRecurso());
        
        // Estilo de botones
        btnNuevo.setBackground(new Color(40, 167, 69));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        
        btnEditar.setBackground(new Color(0, 123, 255));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        
        btnAbrir.setBackground(new Color(23, 162, 184));
        btnAbrir.setForeground(Color.WHITE);
        btnAbrir.setFocusPainted(false);
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnAbrir);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarProyectos() {
        try {
            List<Proyecto> proyectos = proyectoService.listarTodos();
            cmbFiltroProyecto.removeAllItems();
            cmbFiltroProyecto.addItem(null); // Opción "Todos"
            
            for (Proyecto proyecto : proyectos) {
                cmbFiltroProyecto.addItem(proyecto);
            }
            
        } catch (Exception e) {
            mainFrame.mostrarError("Error cargando proyectos: " + e.getMessage());
        }
    }
    
    private void cargarRecursos() {
        try {
            List<Recurso> recursos;
            Proyecto proyectoSeleccionado = (Proyecto) cmbFiltroProyecto.getSelectedItem();
            
            if (proyectoSeleccionado == null) {
                // Cargar todos los recursos
                RecursoDAO recursoDAO = new RecursoDAO();
                recursos = recursoDAO.listAll();
            } else {
                // Cargar recursos del proyecto seleccionado
                RecursoDAO recursoDAO = new RecursoDAO();
                recursos = recursoDAO.listByProyecto(proyectoSeleccionado.getIdProyecto());
            }
            
            modeloTabla.setRowCount(0);
            
            for (Recurso recurso : recursos) {
                // Obtener nombre del proyecto
                String nombreProyecto = obtenerNombreProyecto(recurso.getIdProyecto());
                
                Object[] fila = {
                    recurso.getIdRecurso(),
                    nombreProyecto,
                    recurso.getNombre(),
                    recurso.getTipo(),
                    recurso.getUrl()
                };
                modeloTabla.addRow(fila);
            }
            
        } catch (Exception e) {
            mainFrame.mostrarError("Error cargando recursos: " + e.getMessage());
        }
    }
    
    private String obtenerNombreProyecto(UUID idProyecto) {
        try {
            Proyecto proyecto = proyectoService.buscarPorId(idProyecto);
            return proyecto != null ? proyecto.getNombre() : "Proyecto no encontrado";
        } catch (Exception e) {
            return "Error cargando proyecto";
        }
    }
    
    private void nuevoRecurso(ActionEvent e) {
        RecursoDialog dialog = new RecursoDialog(mainFrame, null, proyectoService);
        dialog.setVisible(true);
        
        Recurso nuevo = dialog.getRecurso();
        if (nuevo != null) {
            try {
                recursoService.crear(nuevo.getIdProyecto(), nuevo.getNombre(), 
                        nuevo.getTipo(), nuevo.getUrl());
                cargarRecursos();
                mainFrame.mostrarMensaje("Recurso creado exitosamente");
            } catch (Exception ex) {
                mainFrame.mostrarError("Error creando recurso: " + ex.getMessage());
            }
        }
    }
    
    private void editarRecurso(ActionEvent e) {
        int fila = tablaRecursos.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un recurso para editar");
            return;
        }
        
        try {
            UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
            RecursoDAO recursoDAO = new RecursoDAO();
            Recurso recurso = recursoDAO.findById(id);
            
            RecursoDialog dialog = new RecursoDialog(mainFrame, recurso, proyectoService);
            dialog.setVisible(true);
            
            Recurso editado = dialog.getRecurso();
            if (editado != null) {
                editado.setIdRecurso(id);
                recursoDAO.update(editado);
                cargarRecursos();
                mainFrame.mostrarMensaje("Recurso actualizado exitosamente");
            }
        } catch (Exception ex) {
            mainFrame.mostrarError("Error editando recurso: " + ex.getMessage());
        }
    }
    
    private void eliminarRecurso(ActionEvent e) {
        int fila = tablaRecursos.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un recurso para eliminar");
            return;
        }
        
        if (mainFrame.confirmar("¿Está seguro de eliminar este recurso?")) {
            try {
                UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
                RecursoDAO recursoDAO = new RecursoDAO();
                recursoDAO.delete(id);
                cargarRecursos();
                mainFrame.mostrarMensaje("Recurso eliminado exitosamente");
            } catch (Exception ex) {
                mainFrame.mostrarError("Error eliminando recurso: " + ex.getMessage());
            }
        }
    }
    
    private void abrirRecurso() {
        int fila = tablaRecursos.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un recurso para abrir");
            return;
        }
        
        String url = (String) modeloTabla.getValueAt(fila, 4);
        if (url == null || url.trim().isEmpty()) {
            mainFrame.mostrarError("Este recurso no tiene URL configurada");
            return;
        }
        
        try {
            // Verificar si la URL tiene protocolo
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            
            // Intentar abrir en el navegador
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback: copiar al portapapeles
                java.awt.datatransfer.StringSelection stringSelection = 
                    new java.awt.datatransfer.StringSelection(url);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(stringSelection, null);
                mainFrame.mostrarMensaje("URL copiada al portapapeles: " + url);
            }
        } catch (Exception ex) {
            mainFrame.mostrarError("Error abriendo URL: " + ex.getMessage());
        }
    }
    
    public void refrescar() {
        cargarProyectos();
        cargarRecursos();
    }
}