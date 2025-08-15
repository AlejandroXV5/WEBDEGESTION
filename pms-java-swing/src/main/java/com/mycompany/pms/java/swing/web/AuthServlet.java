package com.mycompany.pms.java.swing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthServlet extends HttpServlet {
    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper;
    
    public AuthServlet() {
        this.usuarioService = new UsuarioService();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        String pathInfo = req.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/login")) {
            manejarLogin(req, resp);
        } else if (pathInfo != null && pathInfo.equals("/register")) {
            manejarRegistro(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Endpoint no encontrado\"}");
        }
    }
    
    private void manejarLogin(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            LoginRequest loginReq = objectMapper.readValue(req.getReader(), LoginRequest.class);
            
            Usuario usuario = usuarioService.login(loginReq.getEmail(), loginReq.getPassword());
            
            if (usuario != null) {
                // En una implementación real, aquí generarías un JWT token
                LoginResponse response = new LoginResponse();
                response.setUsuario(usuario);
                response.setToken("dummy-token"); // Por simplicidad
                response.setMensaje("Login exitoso");
                
                objectMapper.writeValue(resp.getWriter(), response);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Credenciales inválidas\"}");
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error en login: " + e.getMessage() + "\"}");
        }
    }
    
    private void manejarRegistro(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            RegistroRequest regReq = objectMapper.readValue(req.getReader(), RegistroRequest.class);
            
            Usuario usuario = usuarioService.registrar(
                regReq.getNombre(),
                regReq.getEmail(),
                regReq.getPassword(),
                regReq.getRol() != null ? regReq.getRol() : "USUARIO"
            );
            
            if (usuario != null) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"mensaje\":\"Usuario registrado exitosamente\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Error registrando usuario\"}");
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error en registro: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    // DTOs
    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class LoginResponse {
        private Usuario usuario;
        private String token;
        private String mensaje;
        
        public Usuario getUsuario() { return usuario; }
        public void setUsuario(Usuario usuario) { this.usuario = usuario; }
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }
    
    public static class RegistroRequest {
        private String nombre;
        private String email;
        private String password;
        private String rol;
        
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }
}