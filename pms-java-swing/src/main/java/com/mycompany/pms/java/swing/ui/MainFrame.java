package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    private Usuario usuarioActual;
    private final UsuarioService usuarioService;
    private final ProyectoService proyectoService;
    private final TareaService tareaService;
    private final RecursoService recursoService;
    
    private JTabbedPane tabbedPane;
    private PanelProyectos panelProyectos;
    private PanelTareas panelTareas;
    private PanelUsuarios panelUsuarios;
    private PanelRecursos panelRecursos;
    
    public MainFrame() {
        this.usuarioService = new UsuarioService();
        this.proyectoService = new ProyectoService();
        this.tareaService = new TareaService();
        this.recursoService = new RecursoService();
        
        // Primero mostrar login
        if (!mostrarLogin()) {
            System.exit(0);
        }
        
        initComponents();
        configurarVentana();
    }
    
    private boolean mostrarLogin() {
        LoginDialog loginDialog = new LoginDialog(this, usuarioService);
        loginDialog.setVisible(true);
        
        Usuario user = loginDialog.getUsuarioAutenticado();
        if (user != null) {
            this.usuarioActual = user;
            return true;
        }
        return false;
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Inicializar paneles
        panelProyectos = new PanelProyectos(proyectoService, this);
        panelTareas = new PanelTareas(tareaService, proyectoService, usuarioService, this);
        panelRecursos = new PanelRecursos(recursoService, proyectoService, this);
        
        // Solo admin puede gestionar usuarios
        if ("ADMIN".equals(usuarioActual.getRol())) {
            panelUsuarios = new PanelUsuarios(usuarioService, this);
            tabbedPane.addTab("👥 Usuarios", panelUsuarios);
        }
        
        tabbedPane.addTab("📋 Proyectos", panelProyectos);
        tabbedPane.addTab("✅ Tareas", panelTareas);
        tabbedPane.addTab("📎 Recursos", panelRecursos);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Barra de estado
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.add(new JLabel("Usuario: " + usuarioActual.getNombre() + " | Rol: " + usuarioActual.getRol()));
        add(statusBar, BorderLayout.SOUTH);
        
        // Menu bar
        setJMenuBar(crearMenuBar());
    }
    
    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);
        
        JMenu menuVer = new JMenu("Ver");
        JMenuItem itemRefrescar = new JMenuItem("Refrescar Todo");
        itemRefrescar.addActionListener(this::refrescarTodo);
        menuVer.add(itemRefrescar);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuVer);
        
        return menuBar;
    }
    
    private void refrescarTodo(ActionEvent e) {
        panelProyectos.refrescar();
        panelTareas.refrescar();
        panelRecursos.refrescar();
        if (panelUsuarios != null) {
            panelUsuarios.refrescar();
        }
    }
    
    private void configurarVentana() {
        setTitle("Sistema de Gestión de Proyectos - " + usuarioActual.getNombre());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Icono de la aplicación (opcional)
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());
        } catch (Exception e) {
            // Ignorar si no hay icono
        }
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void mostrarError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", 
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}