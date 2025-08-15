package com.mycompany.pms.java.swing.servicio;

import com.mycompany.pms.java.swing.dao.ProyectoDAO;
import com.mycompany.pms.java.swing.dao.TareaDAO;
import com.mycompany.pms.java.swing.dao.UsuarioDAO;
import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.modelo.Tarea;
import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.util.Validadores;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class TareaService {

    private final TareaDAO tareaDAO;
    private final ProyectoDAO proyectoDAO;
    private final UsuarioDAO usuarioDAO;

    public TareaService() {
        this.tareaDAO = new TareaDAO();
        this.proyectoDAO = new ProyectoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public Tarea crear(UUID idProyecto, UUID idUsuarioAsignado,
                       String titulo, String descripcion, LocalDate fechaLimite,
                       boolean completada) throws SQLException {

        Validadores.requireNonBlank(titulo, "titulo");
        Validadores.maxLen(titulo, 150, "titulo");

        Proyecto p = proyectoDAO.findById(idProyecto);
        if (p == null) throw new IllegalArgumentException("Proyecto no existe.");

        if (fechaLimite != null && p.getFechaInicio() != null && fechaLimite.isBefore(p.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha límite no puede ser anterior al inicio del proyecto.");
        }

        if (idUsuarioAsignado != null) {
            Usuario u = usuarioDAO.findById(idUsuarioAsignado);
            if (u == null) throw new IllegalArgumentException("Usuario asignado no existe.");
        }

        Tarea t = new Tarea();
        t.setIdProyecto(idProyecto);
        t.setIdUsuarioAsignado(idUsuarioAsignado);
        t.setTitulo(titulo.trim());
        t.setDescripcion(descripcion);
        t.setFechaLimite(fechaLimite);
        t.setCompletada(completada);
        return tareaDAO.create(t);
    }

    public boolean asignarUsuario(UUID idTarea, UUID idUsuario) throws SQLException {
        Tarea t = tareaDAO.findById(idTarea);
        if (t == null) throw new IllegalArgumentException("Tarea no encontrada.");
        if (idUsuario != null && usuarioDAO.findById(idUsuario) == null)
            throw new IllegalArgumentException("Usuario no existe.");

        t.setIdUsuarioAsignado(idUsuario);
        return tareaDAO.update(t);
    }

    public boolean marcarCompletada(UUID idTarea, boolean completada) throws SQLException {
        Tarea t = tareaDAO.findById(idTarea);
        if (t == null) throw new IllegalArgumentException("Tarea no encontrada.");
        t.setCompletada(completada);
        return tareaDAO.update(t);
    }
}
