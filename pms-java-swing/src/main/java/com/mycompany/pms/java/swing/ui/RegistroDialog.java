package com.mycompany.pms.java.swing.ui;

import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.UsuarioService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegistroDialog extends JDialog {
    private final UsuarioService usuarioService;
    private Usuario usuarioRegistrado;
    
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> cmbRol;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    
    public RegistroDialog(Frame parent, UsuarioService usuarioService) {
        super(parent, "Registrar Usuario", true);
        this.usuarioService = usuarioService;
        initComponents();
        configurarDialog();
    }
    
    // Constructor adicional para Dialog parent (para compatibilidad)
    public RegistroDialog(Dialog parent, UsuarioService usuarioService) {
        super(parent, "Registrar Usuario", true);
        this.usuarioService = usuarioService;
        initComponents();
        configurarDialog();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Título
        JLabel lblTitulo = new JLabel("Crear Nueva Cuenta");
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
        add(new JLabel("Nombre completo:"), gbc);
        
        txtNombre = new JTextField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtNombre, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Email:"), gbc);
        
        txtEmail = new JTextField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtEmail, gbc);
        
        // Contraseña
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Contraseña:"), gbc);
        
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtPassword, gbc);
        
        // Confirmar contraseña
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Confirmar contraseña:"), gbc);
        
        txtConfirmPassword = new JPasswordField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtConfirmPassword, gbc);
        
        // Rol
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Rol:"), gbc);
        
        cmbRol = new JComboBox<>(new String[]{"USUARIO", "ADMIN"});
        cmbRol.setSelectedIndex(0); // Por defecto USUARIO
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cmbRol, gbc);
        
        // Información de seguridad
        JTextArea txtInfo = new JTextArea(3, 20);
        txtInfo.setText("La contraseña debe tener mínimo 8 caracteres,\nincluyendo mayúscula, minúscula, número\ny al menos un símbolo especial.");
        txtInfo.setEditable(false);
        txtInfo.setBackground(getBackground());
        txtInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        txtInfo.setBorder(BorderFactory.createEmptyBorder());
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.BOTH;
        add(txtInfo, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        
        btnRegistrar.addActionListener(this::registrarUsuario);
        btnCancelar.addActionListener(e -> dispose());
        
        // Estilo de botones
        btnRegistrar.setBackground(new Color(40, 167, 69));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 20, 20, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(panelBotones, gbc);
        
        // Enter para registrar
        getRootPane().setDefaultButton(btnRegistrar);
    }
    
    private void configurarDialog() {
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void registrarUsuario(ActionEvent e) {
        // Obtener datos del formulario
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String rol = (String) cmbRol.getSelectedItem();
        
        // Validaciones básicas de UI
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio");
            txtNombre.requestFocus();
            return;
        }
        
        if (email.isEmpty()) {
            mostrarError("El email es obligatorio");
            txtEmail.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            mostrarError("La contraseña es obligatoria");
            txtPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden");
            txtConfirmPassword.requestFocus();
            return;
        }
        
        // Validación de email básica
        if (!email.contains("@") || !email.contains(".")) {
            mostrarError("Formato de email inválido");
            txtEmail.requestFocus();
            return;
        }
        
        // Mostrar indicador de carga
        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");
        
        try {
            // Intentar registrar el usuario
            usuarioRegistrado = usuarioService.registrar(nombre, email, password, rol);
            
            // Si llegamos aquí, el registro fue exitoso
            dispose();
            
        } catch (Exception ex) {
            // Mostrar error específico
            mostrarError("Error al registrar usuario:\n" + ex.getMessage());
            
            // Limpiar contraseñas por seguridad
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            
        } finally {
            // Restaurar botón
            btnRegistrar.setEnabled(true);
            btnRegistrar.setText("Registrar");
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Registro", JOptionPane.ERROR_MESSAGE);
    }
    
    public Usuario getUsuarioRegistrado() {
        return usuarioRegistrado;
    }
}