package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.UsuarioService;
import com.mycompany.pms.java.swing.dao.UsuarioDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.UUID;

public class PanelUsuarios extends JPanel {
    private final UsuarioService usuarioService;
    private final MainFrame mainFrame;
    
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo, btnEditar, btnEliminar, btnCambiarRol;
    
    public PanelUsuarios(UsuarioService usuarioService, MainFrame mainFrame) {
        this.usuarioService = usuarioService;
        this.mainFrame = mainFrame;
        initComponents();
        cargarUsuarios();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel superior con información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblInfo = new JLabel("👤 Gestión de Usuarios (Solo para Administradores)");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblInfo);
        add(panelInfo, BorderLayout.NORTH);
        
        // Tabla de usuarios
        String[] columnas = {"ID", "Nombre", "Email", "Rol", "Fecha Creación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Ocultar columna ID
        tablaUsuarios.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaUsuarios.getColumnModel().getColumn(0).setMinWidth(0);
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Ajustar anchos de columnas
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(250);
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        // Colores alternados para las filas
        tablaUsuarios.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(240, 240, 240));
                    }
                }
                
                // Resaltar administradores
                if (!isSelected && column == 3 && "ADMIN".equals(value)) {
                    c.setBackground(new Color(255, 248, 220)); // Amarillo claro
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnNuevo = new JButton("➕ Nuevo Usuario");
        btnEditar = new JButton("✏️ Editar");
        btnEliminar = new JButton("🗑️ Eliminar");
        btnCambiarRol = new JButton("🔄 Cambiar Rol");
        
        btnNuevo.addActionListener(this::nuevoUsuario);
        btnEditar.addActionListener(this::editarUsuario);
        btnEliminar.addActionListener(this::eliminarUsuario);
        btnCambiarRol.addActionListener(this::cambiarRol);
        
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
        
        btnCambiarRol.setBackground(new Color(255, 193, 7));
        btnCambiarRol.setForeground(Color.BLACK);
        btnCambiarRol.setFocusPainted(false);
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCambiarRol);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarUsuarios() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.listAll();
            modeloTabla.setRowCount(0);
            
            for (Usuario usuario : usuarios) {
                Object[] fila = {
                    usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRol(),
                    usuario.getFechaCreacion()
                };
                modeloTabla.addRow(fila);
            }
            
        } catch (Exception e) {
            mainFrame.mostrarError("Error cargando usuarios: " + e.getMessage());
        }
    }
    
    private void nuevoUsuario(ActionEvent e) {
        RegistroDialog dialog = new RegistroDialog(mainFrame, usuarioService);
        dialog.setVisible(true);
        
        if (dialog.getUsuarioRegistrado() != null) {
            cargarUsuarios();
            mainFrame.mostrarMensaje("Usuario creado exitosamente");
        }
    }
    
    private void editarUsuario(ActionEvent e) {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un usuario para editar");
            return;
        }
        
        try {
            UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.findById(id);
            
            if (usuario == null) {
                mainFrame.mostrarError("Usuario no encontrado");
                return;
            }
            
            // Crear diálogo simple para editar nombre
            String nuevoNombre = JOptionPane.showInputDialog(
                    mainFrame,
                    "Nombre del usuario:",
                    usuario.getNombre()
            );
            
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                usuario.setNombre(nuevoNombre.trim());
                usuarioDAO.update(usuario);
                cargarUsuarios();
                mainFrame.mostrarMensaje("Usuario actualizado exitosamente");
            }
            
        } catch (Exception ex) {
            mainFrame.mostrarError("Error editando usuario: " + ex.getMessage());
        }
    }
    
    private void eliminarUsuario(ActionEvent e) {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un usuario para eliminar");
            return;
        }
        
        try {
            UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
            String nombre = (String) modeloTabla.getValueAt(fila, 1);
            String email = (String) modeloTabla.getValueAt(fila, 2);
            
            // Prevenir eliminación del usuario actual
            if (email.equals(mainFrame.getUsuarioActual().getEmail())) {
                mainFrame.mostrarError("No puede eliminar su propia cuenta");
                return;
            }
            
            if (mainFrame.confirmar("¿Está seguro de eliminar al usuario '" + nombre + "'?\n" +
                    "Esta acción eliminará también todas sus asignaciones de tareas.")) {
                
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.delete(id);
                cargarUsuarios();
                mainFrame.mostrarMensaje("Usuario eliminado exitosamente");
            }
            
        } catch (Exception ex) {
            mainFrame.mostrarError("Error eliminando usuario: " + ex.getMessage());
        }
    }
    
    private void cambiarRol(ActionEvent e) {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            mainFrame.mostrarError("Seleccione un usuario para cambiar rol");
            return;
        }
        
        try {
            UUID id = (UUID) modeloTabla.getValueAt(fila, 0);
            String nombre = (String) modeloTabla.getValueAt(fila, 1);
            String email = (String) modeloTabla.getValueAt(fila, 2);
            String rolActual = (String) modeloTabla.getValueAt(fila, 3);
            
            // Prevenir cambio de rol del usuario actual
            if (email.equals(mainFrame.getUsuarioActual().getEmail())) {
                mainFrame.mostrarError("No puede cambiar su propio rol");
                return;
            }
            
            String[] opciones = {"ADMIN", "USUARIO"};
            String nuevoRol = (String) JOptionPane.showInputDialog(
                    mainFrame,
                    "Seleccione el nuevo rol para " + nombre + ":",
                    "Cambiar Rol",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    rolActual
            );
            
            if (nuevoRol != null && !nuevoRol.equals(rolActual)) {
                usuarioService.cambiarRol(id, nuevoRol);
                cargarUsuarios();
                mainFrame.mostrarMensaje("Rol cambiado exitosamente a " + nuevoRol);
            }
            
        } catch (Exception ex) {
            mainFrame.mostrarError("Error cambiando rol: " + ex.getMessage());
        }
    }
    
    public void refrescar() {
        cargarUsuarios();
    }
}