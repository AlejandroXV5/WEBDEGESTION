package com.mycompany.pms.java.swing.servicio;

import com.mycompany.pms.java.swing.dao.ProyectoDAO;
import com.mycompany.pms.java.swing.dao.TareaDAO;
import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.modelo.Tarea;
import com.mycompany.pms.java.swing.util.Validadores;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProyectoService {
    public final ProyectoDAO proyectoDAO; // Hacer público para acceso desde UI
    private final TareaDAO tareaDAO;
    
    public ProyectoService() {
        this.proyectoDAO = new ProyectoDAO();
        this.tareaDAO = new TareaDAO();
    }
    
    public ProyectoService(ProyectoDAO pDao, TareaDAO tDao) {
        this.proyectoDAO = pDao;
        this.tareaDAO = tDao;
    }
    
    public Proyecto crear(String nombre, String descripcion, LocalDate inicio, LocalDate fin, String estado) throws SQLException {
        Validadores.requireNonBlank(nombre, "nombre");
        Validadores.maxLen(nombre, 150, "nombre");
        Validadores.estadoProyectoValido(estado);
        Validadores.rangoFechas(inicio, fin);
        
        Proyecto p = new Proyecto();
        p.setNombre(nombre.trim());
        p.setDescripcion(descripcion);
        p.setFechaInicio(inicio);
        p.setFechaFin(fin);
        p.setEstado(estado);
        
        return proyectoDAO.create(p);
    }
    
    public boolean actualizar(Proyecto p) throws SQLException {
        if (p.getIdProyecto() == null) throw new IllegalArgumentException("idProyecto requerido.");
        Validadores.requireNonBlank(p.getNombre(), "nombre");
        Validadores.maxLen(p.getNombre(), 150, "nombre");
        Validadores.estadoProyectoValido(p.getEstado());
        Validadores.rangoFechas(p.getFechaInicio(), p.getFechaFin());
        
        return proyectoDAO.update(p);
    }
    
    public double progreso(UUID idProyecto) throws SQLException {
        List<Tarea> tareas = tareaDAO.listByProyecto(idProyecto);
        if (tareas.isEmpty()) return 0.0;
        
        long done = tareas.stream().filter(Tarea::isCompletada).count();
        return (done * 100.0) / tareas.size();
    }
    
    public boolean cerrarProyecto(UUID idProyecto) throws SQLException {
        List<Tarea> tareas = tareaDAO.listByProyecto(idProyecto);
        boolean hayPendientes = tareas.stream().anyMatch(t -> !t.isCompletada());
        
        if (hayPendientes) {
            throw new IllegalStateException("No se puede cerrar: hay tareas pendientes.");
        }
        
        Proyecto p = proyectoDAO.findById(idProyecto);
        if (p == null) throw new IllegalArgumentException("Proyecto no encontrado.");
        
        p.setEstado("COMPLETADO");
        return proyectoDAO.update(p);
    }
    
    public List<Proyecto> listarTodos() throws SQLException {
        return proyectoDAO.listAll();
    }
    
    public Proyecto buscarPorId(UUID id) throws SQLException {
        return proyectoDAO.findById(id);
    }
    
    public boolean eliminar(UUID id) throws SQLException {
        return proyectoDAO.delete(id);
    }
}