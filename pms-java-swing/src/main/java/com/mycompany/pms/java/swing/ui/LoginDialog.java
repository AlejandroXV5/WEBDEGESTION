package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.UsuarioService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog {
    private final UsuarioService usuarioService;
    private Usuario usuarioAutenticado;
    
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistrar;
    
    public LoginDialog(Frame parent, UsuarioService usuarioService) {
        super(parent, "Iniciar Sesión", true);
        this.usuarioService = usuarioService;
        initComponents();
        configurarDialog();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Título
        JLabel lblTitulo = new JLabel("Sistema de Gestión de Proyectos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(lblTitulo, gbc);
        
        // Email
        gbc.gridwidth = 1; gbc.insets = new Insets(5, 20, 5, 5);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);
        
        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        add(txtEmail, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Contraseña:"), gbc);
        
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        add(txtPassword, gbc);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnLogin = new JButton("Ingresar");
        btnRegistrar = new JButton("Registrarse");
        
        btnLogin.addActionListener(this::login);
        btnRegistrar.addActionListener(this::mostrarRegistro);
        
        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistrar);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(panelBotones, gbc);
        
        // Enter para login
        getRootPane().setDefaultButton(btnLogin);
    }
    
    private void configurarDialog() {
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void login(ActionEvent e) {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            usuarioAutenticado = usuarioService.login(email, password);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de autenticación: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }
    
    private void mostrarRegistro(ActionEvent e) {
        RegistroDialog registroDialog = new RegistroDialog(this, usuarioService);
        registroDialog.setVisible(true);
        
        if (registroDialog.getUsuarioRegistrado() != null) {
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente. Puede iniciar sesión.", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}