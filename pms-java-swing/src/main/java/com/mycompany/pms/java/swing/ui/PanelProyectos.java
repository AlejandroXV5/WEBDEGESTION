package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.servicio.ProyectoService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PanelProyectos extends JPanel {
    private final ProyectoService proyectoService;
    private final MainFrame mainFrame;
    
    private JTable tablaProyectos;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo, btnEditar, btnEliminar, btnProgreso;
    
    public PanelProyectos(ProyectoService proyectoService, MainFrame mainFrame) {
        this.proyectoService = proyectoService;
        this.mainFrame = mainFrame;
        initComponents();
        cargarProyectos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "Descripción", "Inicio", "Fin", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProyectos = new JTable(modeloTabla);
        tablaProyectos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProyectos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaProyectos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaProyectos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(tablaProyectos);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnNuevo = new JButton("➕ Nuevo");
        btnEditar = new JButton("✏️ Editar");
        btnEliminar = new JButton("🗑️ Eliminar");
        btnProgreso = new JButton("📊 Ver Progreso");
        
        btnNuevo.addActionListener(this::nuevoProyecto);
        btnEditar.addActionListener(this::editarProyecto);
        btnEliminar.addActionListener(this::eliminarProyecto);
        btnProgreso.addActionListener(this::verProgreso);
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnProgreso);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarProyectos() {
        try {
            List<Proyecto> proyectos = proyectoService.proyectoDAO.listAll();
            modeloTabla.setRowCount(0);
            
            for (Proyecto p : proyectos) {
                Object[] fila = {
                    p.getIdProyecto(),
                    p.getNombre(),
                    p.getDescripcion(),
                    p.getFechaInicio(),
                    p.getFechaFin(),
                    p.getEstado()
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            mainFrame.mostrarError("Error cargando proyectos: " + e.getMessage());
        }
    }
    
    private void nuevoProyecto(ActionEvent e) {
        ProyectoDialog dialog = new ProyectoDialog(mainFrame, null);
        dialog.setVisible(true);
        
        Proyecto nuevo = dialog.getProyecto();
        if (nuevo != null) {
            try {
                proyectoService.crear(nuevo.getNombre(), nuevo.getDescripcion(),
                        nuevo.getFechaInicio(), nuevo.getFechaFin(), nuevo.getEstado());
                cargarProyectos();
                mainFrame.mostrarMensaje("Proyecto creado exitosamente");
            } catch (Exception ex) {
                mainFrame.mostrarError("Error creando proyecto: " + ex.getMessage());
            }
        }
    }
    
    private void editarProyecto(ActionEvent e) {
        int fila = tablaProyectos.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un proyecto para editar");
            return;
        }
        
        try {
            UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
            Proyecto proyecto = proyectoService.proyectoDAO.findById(id);
            
            ProyectoDialog dialog = new ProyectoDialog(mainFrame, proyecto);
            dialog.setVisible(true);
            
            Proyecto editado = dialog.getProyecto();
            if (editado != null) {
                editado.setIdProyecto(id);
                proyectoService.actualizar(editado);
                cargarProyectos();
                mainFrame.mostrarMensaje("Proyecto actualizado exitosamente");
            }
        } catch (Exception ex) {
            mainFrame.mostrarError("Error editando proyecto: " + ex.getMessage());
        }
    }
    
    private void eliminarProyecto(ActionEvent e) {
        int fila = tablaProyectos.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un proyecto para eliminar");
            return;
        }
        
        if (mainFrame.confirmar("¿Está seguro de eliminar este proyecto?")) {
            try {
                UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
                proyectoService.proyectoDAO.delete(id);
                cargarProyectos();
                mainFrame.mostrarMensaje("Proyecto eliminado exitosamente");
            } catch (Exception ex) {
                mainFrame.mostrarError("Error eliminando proyecto: " + ex.getMessage());
            }
        }
    }
    
    private void verProgreso(ActionEvent e) {
        int fila = tablaProyectos.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un proyecto para ver el progreso");
            return;
        }
        
        try {
            UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
            double progreso = proyectoService.progreso(id);
            String nombre = (String) modeloTabla.getValueAt(fila, 1);
            
            JOptionPane.showMessageDialog(mainFrame, 
                    String.format("Progreso del proyecto '%s': %.1f%%", nombre, progreso),
                    "Progreso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            mainFrame.mostrarError("Error calculando progreso: " + ex.getMessage());
        }
    }
    
    public void refrescar() {
        cargarProyectos();
    }
}