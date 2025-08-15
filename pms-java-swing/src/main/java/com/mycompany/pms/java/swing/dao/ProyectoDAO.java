package com.mycompany.pms.java.swing.dao;

import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProyectoDAO {

    private final LogErrorDAO logErrorDAO = new LogErrorDAO();

    public Proyecto create(Proyecto proyecto) throws SQLException {
        String sql = "INSERT INTO proyecto (nombre, descripcion, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?) RETURNING id_proyecto";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setObject(3, proyecto.getFechaInicio());
            ps.setObject(4, proyecto.getFechaFin());
            ps.setString(5, proyecto.getEstado());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    proyecto.setIdProyecto((UUID) rs.getObject("id_proyecto"));
                    return proyecto;
                }
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return null;
    }

    public Proyecto findById(UUID id) throws SQLException {
        String sql = "SELECT id_proyecto, nombre, descripcion, fecha_inicio, fecha_fin, estado FROM proyecto WHERE id_proyecto = ?";
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

    public boolean update(Proyecto proyecto) throws SQLException {
        String sql = "UPDATE proyecto SET nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, estado = ? WHERE id_proyecto = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setObject(3, proyecto.getFechaInicio());
            ps.setObject(4, proyecto.getFechaFin());
            ps.setString(5, proyecto.getEstado());
            ps.setObject(6, proyecto.getIdProyecto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
    }

    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM proyecto WHERE id_proyecto = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
    }

    public List<Proyecto> listAll() throws SQLException {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT id_proyecto, nombre, descripcion, fecha_inicio, fecha_fin, estado FROM proyecto ORDER BY fecha_inicio DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                proyectos.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return proyectos;
    }

    private Proyecto mapRow(ResultSet rs) throws SQLException {
        Proyecto p = new Proyecto();
        p.setIdProyecto((UUID) rs.getObject("id_proyecto"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setFechaInicio(rs.getObject("fecha_inicio", LocalDate.class));
        p.setFechaFin(rs.getObject("fecha_fin", LocalDate.class));
        p.setEstado(rs.getString("estado"));
        return p;
    }

    private String getStackTraceString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : t.getStackTrace()) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}
