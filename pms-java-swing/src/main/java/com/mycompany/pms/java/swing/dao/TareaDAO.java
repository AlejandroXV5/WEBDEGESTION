package com.mycompany.pms.java.swing.dao;

import com.mycompany.pms.java.swing.modelo.Tarea;
import com.mycompany.pms.java.swing.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TareaDAO {

    private final LogErrorDAO logErrorDAO = new LogErrorDAO();

    public Tarea create(Tarea tarea) throws SQLException {
        String sql = "INSERT INTO tarea (id_proyecto, id_usuario_asignado, titulo, descripcion, fecha_limite, completada) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_tarea";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, tarea.getIdProyecto());
            if (tarea.getIdUsuarioAsignado() != null) {
                ps.setObject(2, tarea.getIdUsuarioAsignado());
            } else {
                ps.setNull(2, Types.OTHER);
            }
            ps.setString(3, tarea.getTitulo());
            ps.setString(4, tarea.getDescripcion());
            ps.setObject(5, tarea.getFechaLimite());
            ps.setBoolean(6, tarea.isCompletada());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tarea.setIdTarea((UUID) rs.getObject("id_tarea"));
                    return tarea;
                }
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return null;
    }

    public Tarea findById(UUID id) throws SQLException {
        String sql = "SELECT id_tarea, id_proyecto, id_usuario_asignado, titulo, descripcion, fecha_limite, completada FROM tarea WHERE id_tarea = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return null;
    }

    public boolean update(Tarea tarea) throws SQLException {
        String sql = "UPDATE tarea SET id_proyecto = ?, id_usuario_asignado = ?, titulo = ?, descripcion = ?, fecha_limite = ?, completada = ? WHERE id_tarea = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, tarea.getIdProyecto());
            if (tarea.getIdUsuarioAsignado() != null) {
                ps.setObject(2, tarea.getIdUsuarioAsignado());
            } else {
                ps.setNull(2, Types.OTHER);
            }
            ps.setString(3, tarea.getTitulo());
            ps.setString(4, tarea.getDescripcion());
            ps.setObject(5, tarea.getFechaLimite());
            ps.setBoolean(6, tarea.isCompletada());
            ps.setObject(7, tarea.getIdTarea());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
    }

    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM tarea WHERE id_tarea = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
    }

    public List<Tarea> listAll() throws SQLException {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT id_tarea, id_proyecto, id_usuario_asignado, titulo, descripcion, fecha_limite, completada FROM tarea ORDER BY fecha_limite ASC NULLS LAST";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tareas.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return tareas;
    }

    public List<Tarea> listByProyecto(UUID idProyecto) throws SQLException {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT id_tarea, id_proyecto, id_usuario_asignado, titulo, descripcion, fecha_limite, completada FROM tarea WHERE id_proyecto = ? ORDER BY fecha_limite ASC NULLS LAST";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, idProyecto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return tareas;
    }

    private Tarea mapRow(ResultSet rs) throws SQLException {
        Tarea t = new Tarea();
        t.setIdTarea((UUID) rs.getObject("id_tarea"));
        t.setIdProyecto((UUID) rs.getObject("id_proyecto"));
        Object userId = rs.getObject("id_usuario_asignado");
        if (userId != null) {
            t.setIdUsuarioAsignado((UUID) userId);
        } else {
            t.setIdUsuarioAsignado(null);
        }
        t.setTitulo(rs.getString("titulo"));
        t.setDescripcion(rs.getString("descripcion"));
        t.setFechaLimite(rs.getObject("fecha_limite", LocalDate.class));
        t.setCompletada(rs.getBoolean("completada"));
        return t;
    }

    private String getStackTraceString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : t.getStackTrace()) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}
