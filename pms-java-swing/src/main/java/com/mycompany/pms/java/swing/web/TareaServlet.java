package com.mycompany.pms.java.swing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.pms.java.swing.dao.TareaDAO;
import com.mycompany.pms.java.swing.modelo.Tarea;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class TareaServlet extends HttpServlet {
    private final TareaDAO tareaDAO;
    private final ObjectMapper objectMapper;
    
    public TareaServlet() {
        this.tareaDAO = new TareaDAO();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            List<Tarea> tareas = tareaDAO.listAll();
            objectMapper.writeValue(resp.getWriter(), tareas);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Error cargando tareas: " + e.getMessage() + "\"}");
        }
    }
}