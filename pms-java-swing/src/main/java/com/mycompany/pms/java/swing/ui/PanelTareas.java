package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.modelo.Tarea;
import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.ProyectoService;
import com.mycompany.pms.java.swing.servicio.TareaService;
import com.mycompany.pms.java.swing.servicio.UsuarioService;
import com.mycompany.pms.java.swing.dao.TareaDAO;
import com.mycompany.pms.java.swing.dao.UsuarioDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.UUID;

public class PanelTareas extends JPanel {
    private final TareaService tareaService;
    private final ProyectoService proyectoService;
    private final UsuarioService usuarioService;
    private final MainFrame mainFrame;
    
    private JTable tablaTareas;
    private DefaultTableModel modeloTabla;
    private JButton btnNueva, btnEditar, btnEliminar, btnCompletar, btnAsignar;
    private JComboBox<Proyecto> cmbFiltroProyecto;
    
    public PanelTareas(TareaService tareaService, ProyectoService proyectoService, 
                      UsuarioService usuarioService, MainFrame mainFrame) {
        this.tareaService = tareaService;
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
        this.mainFrame = mainFrame;
        initComponents();
        cargarProyectos();
        cargarTareas();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel superior con filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.add(new JLabel("Filtrar por proyecto:"));
        
        cmbFiltroProyecto = new JComboBox<>();
        cmbFiltroProyecto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Proyecto) {
                    setText(((Proyecto) value).getNombre());
                } else if (value == null) {
                    setText("-- Todos los proyectos --");
                }
                return this;
            }
        });
        cmbFiltroProyecto.addActionListener(e -> cargarTareas());
        panelFiltros.add(cmbFiltroProyecto);
        
        JButton btnLimpiarFiltro = new JButton("Mostrar Todas");
        btnLimpiarFiltro.addActionListener(e -> {
            cmbFiltroProyecto.setSelectedIndex(0);
            cargarTareas();
        });
        panelFiltros.add(btnLimpiarFiltro);
        
        add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de tareas
        String[] columnas = {"ID", "Proyecto", "Título", "Descripción", "Asignado a", "Fecha Límite", "Completada"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) return Boolean.class; // Columna completada
                return String.class;
            }
        };
        
        tablaTareas = new JTable(modeloTabla);
        tablaTareas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Ocultar columna ID
        tablaTareas.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaTareas.getColumnModel().getColumn(0).setMinWidth(0);
        tablaTareas.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Ajustar anchos de columnas
        tablaTareas.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaTareas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaTareas.getColumnModel().getColumn(3).setPreferredWidth(200);
        tablaTareas.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaTareas.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaTareas.getColumnModel().getColumn(6).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tablaTareas);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnNueva = new JButton("➕ Nueva Tarea");
        btnEditar = new JButton("✏️ Editar");
        btnEliminar = new JButton("🗑️ Eliminar");
        btnCompletar = new JButton("✅ Completar/Descompletar");
        btnAsignar = new JButton("👤 Asignar Usuario");
        
        btnNueva.addActionListener(this::nuevaTarea);
        btnEditar.addActionListener(this::editarTarea);
        btnEliminar.addActionListener(this::eliminarTarea);
        btnCompletar.addActionListener(this::toggleCompletar);
        btnAsignar.addActionListener(this::asignarUsuario);
        
        panelBotones.add(btnNueva);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCompletar);
        panelBotones.add(btnAsignar);
        
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
    
    private void cargarTareas() {
        try {
            List<Tarea> tareas;
            Proyecto proyectoSeleccionado = (Proyecto) cmbFiltroProyecto.getSelectedItem();
            TareaDAO tareaDAO = new TareaDAO();
            
            if (proyectoSeleccionado == null) {
                // Cargar todas las tareas
                tareas = tareaDAO.listAll();
            } else {
                // Cargar tareas del proyecto seleccionado
                tareas = tareaDAO.listByProyecto(proyectoSeleccionado.getIdProyecto());
            }
            
            modeloTabla.setRowCount(0);
            
            for (Tarea tarea : tareas) {
                // Obtener nombres para mostrar en lugar de IDs
                String nombreProyecto = obtenerNombreProyecto(tarea.getIdProyecto());
                String nombreUsuario = obtenerNombreUsuario(tarea.getIdUsuarioAsignado());
                
                Object[] fila = {
                    tarea.getIdTarea(),
                    nombreProyecto,
                    tarea.getTitulo(),
                    tarea.getDescripcion(),
                    nombreUsuario,
                    tarea.getFechaLimite(),
                    tarea.isCompletada()
                };
                modeloTabla.addRow(fila);
            }
            
        } catch (Exception e) {
            mainFrame.mostrarError("Error cargando tareas: " + e.getMessage());
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
    
    private String obtenerNombreUsuario(UUID idUsuario) {
        if (idUsuario == null) return "Sin asignar";
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.findById(idUsuario);
            return usuario != null ? usuario.getNombre() : "Usuario no encontrado";
        } catch (Exception e) {
            return "Error cargando usuario";
        }
    }
    
    private void nuevaTarea(ActionEvent e) {
        TareaDialog dialog = new TareaDialog(mainFrame, null, proyectoService, usuarioService);
        dialog.setVisible(true);
        
        Tarea nueva = dialog.getTarea();
        if (nueva != null) {
            try {
                tareaService.crear(nueva.getIdProyecto(), nueva.getIdUsuarioAsignado(),
                        nueva.getTitulo(), nueva.getDescripcion(), 
                        nueva.getFechaLimite(), nueva.isCompletada());
                cargarTareas();
                mainFrame.mostrarMensaje("Tarea creada exitosamente");
            } catch (Exception ex) {
                mainFrame.mostrarError("Error creando tarea: " + ex.getMessage());
            }
        }
    }
    
    private void editarTarea(ActionEvent e) {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione una tarea para editar");
            return;
        }
        
        try {
            UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
            TareaDAO tareaDAO = new TareaDAO();
            Tarea tarea = tareaDAO.findById(id);
            
            TareaDialog dialog = new TareaDialog(mainFrame, tarea, proyectoService, usuarioService);
            dialog.setVisible(true);
            
            Tarea editada = dialog.getTarea();
            if (editada != null) {
                editada.setIdTarea(id);
                tareaDAO.update(editada);
                cargarTareas();
                mainFrame.mostrarMensaje("Tarea actualizada exitosamente");
            }
        } catch (Exception ex) {
            mainFrame.mostrarError("Error editando tarea: " + ex.getMessage());
        }
    }
    
    private void eliminarTarea(ActionEvent e) {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione una tarea para eliminar");
            return;
        }
        
        if (mainFrame.confirmar("¿Está seguro de eliminar esta tarea?")) {
            try {
                UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
                TareaDAO tareaDAO = new TareaDAO();
                tareaDAO.delete(id);
                cargarTareas();
                mainFrame.mostrarMensaje("Tarea eliminada exitosamente");
            } catch (Exception ex) {
                mainFrame.mostrarError("Error eliminando tarea: " + ex.getMessage());
            }
        }
    }
    
    private void toggleCompletar(ActionEvent e) {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione una tarea para cambiar estado");
            return;
        }
        
        try {
            UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
            boolean completadaActual = (Boolean) modeloTabla.getValueAt(fila, 6);
            
            tareaService.marcarCompletada(id, !completadaActual);
            cargarTareas();
            
            String estado = !completadaActual ? "completada" : "pendiente";
            mainFrame.mostrarMensaje("Tarea marcada como " + estado);
        } catch (Exception ex) {
            mainFrame.mostrarError("Error cambiando estado de tarea: " + ex.getMessage());
        }
    }
    
    private void asignarUsuario(ActionEvent e) {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione una tarea para asignar usuario");
            return;
        }
        
        try {
            UUID idTarea = (UUID) modeloTabla.getValueAt(fila, 0);
            
            // Mostrar diálogo para seleccionar usuario
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.listAll();
            
            // Crear array con opción "Sin asignar"
            Object[] opciones = new Object[usuarios.size() + 1];
            opciones[0] = "-- Sin asignar --";
            for (int i = 0; i < usuarios.size(); i++) {
                opciones[i + 1] = usuarios.get(i);
            }
            
            Object seleccion = JOptionPane.showInputDialog(
                    mainFrame,
                    "Seleccione el usuario a asignar:",
                    "Asignar Usuario",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0] // Valor por defecto
            );
            
            // Verificar si el usuario presionó OK (seleccion no es null)
            if (seleccion != null) {
                UUID idUsuario = null;
                String mensaje;
                
                if (seleccion instanceof Usuario) {
                    Usuario usuarioSeleccionado = (Usuario) seleccion;
                    idUsuario = usuarioSeleccionado.getIdUsuario();
                    mensaje = "Tarea asignada a " + usuarioSeleccionado.getNombre();
                } else {
                    // Seleccionó "Sin asignar"
                    mensaje = "Tarea desasignada";
                }
                
                tareaService.asignarUsuario(idTarea, idUsuario);
                cargarTareas();
                mainFrame.mostrarMensaje(mensaje);
            }
            
        } catch (Exception ex) {
            mainFrame.mostrarError("Error asignando usuario: " + ex.getMessage());
        }
    }
    
    public void refrescar() {
        cargarProyectos();
        cargarTareas();
    }
}