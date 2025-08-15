package com.mycompany.pms.java.swing.servicio;

import com.mycompany.pms.java.swing.dao.UsuarioDAO;
import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.util.Validadores;

import java.sql.SQLException;
import java.util.UUID;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() { this.usuarioDAO = new UsuarioDAO(); }
    public UsuarioService(UsuarioDAO dao) { this.usuarioDAO = dao; }

    public Usuario registrar(String nombre, String email, String plainPassword, String rol) throws SQLException {
        Validadores.requireNonBlank(nombre, "nombre");
        Validadores.maxLen(nombre, 100, "nombre");
        Validadores.email(email);
        Validadores.passwordFuerte(plainPassword);
        Validadores.rolValido(rol);

        if (usuarioDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        Usuario u = new Usuario();
        u.setNombre(nombre.trim());
        u.setEmail(email.trim().toLowerCase());
        u.setRol(rol);
        // El hash lo gestiona UsuarioDAO.create
        return usuarioDAO.create(u, plainPassword);
    }

    public boolean cambiarRol(UUID idUsuario, String nuevoRol) throws SQLException {
        Validadores.rolValido(nuevoRol);
        Usuario u = usuarioDAO.findById(idUsuario);
        if (u == null) throw new IllegalArgumentException("Usuario no encontrado.");
        u.setRol(nuevoRol);
        return usuarioDAO.update(u);
    }

    public Usuario login(String email, String plainPassword) throws SQLException {
        Validadores.email(email);
        if (!usuarioDAO.verifyPassword(email, plainPassword)) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }
        return usuarioDAO.findByEmail(email);
    }
}
