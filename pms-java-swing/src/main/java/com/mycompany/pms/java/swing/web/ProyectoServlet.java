package com.mycompany.pms.java.swing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.servicio.ProyectoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProyectoServlet extends HttpServlet {
    private final ProyectoService proyectoService;
    private final ObjectMapper objectMapper;
    
    public ProyectoServlet() {
        this.proyectoService = new ProyectoService();
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
                // GET /api/proyectos - Listar todos
                List<Proyecto> proyectos = proyectoService.listarTodos();
                objectMapper.writeValue(resp.getWriter(), proyectos);
                
            } else if (pathInfo.startsWith("/")) {
                // GET /api/proyectos/{id} - Buscar por ID
                String idStr = pathInfo.substring(1);
                try {
                    UUID id = UUID.fromString(idStr);
                    Proyecto proyecto = proyectoService.buscarPorId(id);
                    
                    if (proyecto != null) {
                        objectMapper.writeValue(resp.getWriter(), proyecto);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\":\"Proyecto no encontrado\"}");
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
            ProyectoDTO dto = objectMapper.readValue(req.getReader(), ProyectoDTO.class);
            
            // Crear proyecto usando el servicio existente
            Proyecto proyecto = proyectoService.crear(
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getFechaInicio(),
                dto.getFechaFin(),
                dto.getEstado()
            );
            
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), proyecto);
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error creando proyecto: " + e.getMessage() + "\"}");
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
            
            ProyectoDTO dto = objectMapper.readValue(req.getReader(), ProyectoDTO.class);
            
            // Buscar proyecto existente
            Proyecto proyecto = proyectoService.buscarPorId(id);
            if (proyecto == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Proyecto no encontrado\"}");
                return;
            }
            
            // Actualizar campos
            proyecto.setNombre(dto.getNombre());
            proyecto.setDescripcion(dto.getDescripcion());
            proyecto.setFechaInicio(dto.getFechaInicio());
            proyecto.setFechaFin(dto.getFechaFin());
            proyecto.setEstado(dto.getEstado());
            
            boolean actualizado = proyectoService.actualizar(proyecto);
            
            if (actualizado) {
                objectMapper.writeValue(resp.getWriter(), proyecto);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Error actualizando proyecto\"}");
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error actualizando proyecto: " + e.getMessage() + "\"}");
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
            
            boolean eliminado = proyectoService.eliminar(id);
            
            if (eliminado) {
                resp.getWriter().write("{\"mensaje\":\"Proyecto eliminado exitosamente\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Proyecto no encontrado\"}");
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error eliminando proyecto: " + e.getMessage() + "\"}");
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
    
    // DTO para recibir datos JSON
    public static class ProyectoDTO {
        private String nombre;
        private String descripcion;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String estado;
        
        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        
        public LocalDate getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
        
        public LocalDate getFechaFin() { return fechaFin; }
        public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
        
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}