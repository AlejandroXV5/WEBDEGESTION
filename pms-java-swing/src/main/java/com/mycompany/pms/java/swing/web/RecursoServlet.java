package com.mycompany.pms.java.swing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.pms.java.swing.dao.RecursoDAO;
import com.mycompany.pms.java.swing.modelo.Recurso;
import com.mycompany.pms.java.swing.servicio.RecursoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RecursoServlet extends HttpServlet {
    private final RecursoDAO recursoDAO;
    private final RecursoService recursoService;
    private final ObjectMapper objectMapper;
    
    public RecursoServlet() {
        this.recursoDAO = new RecursoDAO();
        this.recursoService = new RecursoService();
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
            String proyectoParam = req.getParameter("proyecto");
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/recursos o /api/recursos?proyecto=uuid
                List<Recurso> recursos;
                
                if (proyectoParam != null) {
                    // Filtrar por proyecto
                    UUID idProyecto = UUID.fromString(proyectoParam);
                    recursos = recursoDAO.listByProyecto(idProyecto);
                } else {
                    // Listar todos
                    recursos = recursoDAO.listAll();
                }
                
                objectMapper.writeValue(resp.getWriter(), recursos);
                
            } else if (pathInfo.startsWith("/")) {
                // GET /api/recursos/{id} - Buscar por ID
                String idStr = pathInfo.substring(1);
                try {
                    UUID id = UUID.fromString(idStr);
                    Recurso recurso = recursoDAO.findById(id);
                    
                    if (recurso != null) {
                        objectMapper.writeValue(resp.getWriter(), recurso);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\":\"Recurso no encontrado\"}");
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
            RecursoDTO dto = objectMapper.readValue(req.getReader(), RecursoDTO.class);
            
            // Validar campos obligatorios
            if (dto.getIdProyecto() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID de proyecto es obligatorio\"}");
                return;
            }
            
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Nombre del recurso es obligatorio\"}");
                return;
            }
            
            // Crear recurso usando el servicio existente
            Recurso recurso = recursoService.crear(
                dto.getIdProyecto(),
                dto.getNombre(),
                dto.getTipo() != null ? dto.getTipo() : "Documento",
                dto.getUrl()
            );
            
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), recurso);
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error creando recurso: " + e.getMessage() + "\"}");
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
            
            RecursoDTO dto = objectMapper.readValue(req.getReader(), RecursoDTO.class);
            
            // Buscar recurso existente
            Recurso recurso = recursoDAO.findById(id);
            if (recurso == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Recurso no encontrado\"}");
                return;
            }
            
            // Actualizar campos
            if (dto.getIdProyecto() != null) {
                recurso.setIdProyecto(dto.getIdProyecto());
            }
            if (dto.getNombre() != null) {
                recurso.setNombre(dto.getNombre());
            }
            if (dto.getTipo() != null) {
                recurso.setTipo(dto.getTipo());
            }
            if (dto.getUrl() != null) {
                recurso.setUrl(dto.getUrl());
            }
            
            boolean actualizado = recursoDAO.update(recurso);
            
            if (actualizado) {
                objectMapper.writeValue(resp.getWriter(), recurso);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Error actualizando recurso\"}");
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error actualizando recurso: " + e.getMessage() + "\"}");
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
            
            boolean eliminado = recursoDAO.delete(id);
            
            if (eliminado) {
                resp.getWriter().write("{\"mensaje\":\"Recurso eliminado exitosamente\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Recurso no encontrado\"}");
            }
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error eliminando recurso: " + e.getMessage() + "\"}");
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
    public static class RecursoDTO {
        private UUID idProyecto;
        private String nombre;
        private String tipo;
        private String url;
        
        // Getters y setters
        public UUID getIdProyecto() { return idProyecto; }
        public void setIdProyecto(UUID idProyecto) { this.idProyecto = idProyecto; }
        
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}