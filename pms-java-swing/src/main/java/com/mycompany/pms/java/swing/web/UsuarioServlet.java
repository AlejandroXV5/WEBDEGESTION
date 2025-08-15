package com.mycompany.pms.java.swing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.pms.java.swing.dao.UsuarioDAO;
import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class UsuarioServlet extends HttpServlet {
    private final UsuarioDAO usuarioDAO;
    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper;
    
    public UsuarioServlet() {
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioService = new UsuarioService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // Habilitar CORS
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        try {
            String pathInfo = req.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/usuarios - Listar todos
                List<Usuario> usuarios = usuarioDAO.listAll();
                
                // Remover password hash por seguridad
                usuarios.forEach(u -> u.setPasswordHash(null));
                
                objectMapper.writeValue(resp.getWriter(), usuarios);
                
            } else if (pathInfo.startsWith("/")) {
                // GET /api/usuarios/{id} - Buscar por ID
                String idStr = pathInfo.substring(1);
                try {
                    UUID id = UUID.fromString(idStr);
                    Usuario usuario = usuarioDAO.findById(id);
                    
                    if (usuario != null) {
                        // Remover password hash por seguridad
                        usuario.setPasswordHash(null);
                        objectMapper.writeValue(resp.getWriter(), usuario);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
                    }
                } catch (IllegalArgumentException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\":\"ID inválido\"}");
                }
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            // Leer JSON del request
            UsuarioDTO dto = objectMapper.readValue(req.getReader(), UsuarioDTO.class);
            
            // Crear usuario usando el servicio existente
            Usuario usuario = usuarioService.registrar(
                dto.getNombre(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getRol() != null ? dto.getRol() : "USUARIO"
            );
            
            // Remover password hash antes de enviar respuesta
            usuario.setPasswordHash(null);
            
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), usuario);
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error creando usuario: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID requerido para actualización\"}");
                return;
            }
            
            String idStr = pathInfo.substring(1);
            UUID id = UUID.fromString(idStr);
            
            UsuarioUpdateDTO dto = objectMapper.readValue(req.getReader(), UsuarioUpdateDTO.class);
            
            // Buscar usuario existente
            Usuario usuario = usuarioDAO.findById(id);
            if (usuario == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
                return;
            }
            
            // Actualizar solo nombre (por seguridad)
            if (dto.getNombre() != null) {
                usuario.setNombre(dto.getNombre());
            }
            
            boolean actualizado = usuarioDAO.update(usuario);
            
            if (actualizado) {
                // Remover password hash antes de enviar respuesta
                usuario.setPasswordHash(null);
                objectMapper.writeValue(resp.getWriter(), usuario);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Error actualizando usuario\"}");
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error actualizando usuario: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID requerido para eliminación\"}");
                return;
            }
            
            String idStr = pathInfo.substring(1);
            UUID id = UUID.fromString(idStr);
            
            boolean eliminado = usuarioDAO.delete(id);
            
            if (eliminado) {
                resp.getWriter().write("{\"mensaje\":\"Usuario eliminado exitosamente\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error eliminando usuario: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Manejo de CORS preflight
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    // DTOs para recibir datos JSON
    public static class UsuarioDTO {
        private String nombre;
        private String email;
        private String password;
        private String rol;
        
        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }
    
    public static class UsuarioUpdateDTO {
        private String nombre;
        private String rol;
        
        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }
}