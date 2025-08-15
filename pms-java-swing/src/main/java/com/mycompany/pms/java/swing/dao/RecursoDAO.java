package com.mycompany.pms.java.swing.dao;

import com.mycompany.pms.java.swing.modelo.Recurso;
import com.mycompany.pms.java.swing.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecursoDAO {

    private final LogErrorDAO logErrorDAO = new LogErrorDAO();

    public Recurso create(Recurso recurso) throws SQLException {
        String sql = "INSERT INTO recurso (id_proyecto, nombre, tipo, url) VALUES (?, ?, ?, ?) RETURNING id_recurso";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, recurso.getIdProyecto());
            ps.setString(2, recurso.getNombre());
            ps.setString(3, recurso.getTipo());
            ps.setString(4, recurso.getUrl());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    recurso.setIdRecurso((UUID) rs.getObject("id_recurso"));
                    return recurso;
                }
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return null;
    }

    public Recurso findById(UUID id) throws SQLException {
        String sql = "SELECT id_recurso, id_proyecto, nombre, tipo, url FROM recurso WHERE id_recurso = ?";
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

    public boolean update(Recurso recurso) throws SQLException {
        String sql = "UPDATE recurso SET id_proyecto = ?, nombre = ?, tipo = ?, url = ? WHERE id_recurso = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, recurso.getIdProyecto());
            ps.setString(2, recurso.getNombre());
            ps.setString(3, recurso.getTipo());
            ps.setString(4, recurso.getUrl());
            ps.setObject(5, recurso.getIdRecurso());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
    }

    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM recurso WHERE id_recurso = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
    }

    public List<Recurso> listAll() throws SQLException {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT id_recurso, id_proyecto, nombre, tipo, url FROM recurso ORDER BY nombre ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                recursos.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return recursos;
    }

    public List<Recurso> listByProyecto(UUID idProyecto) throws SQLException {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT id_recurso, id_proyecto, nombre, tipo, url FROM recurso WHERE id_proyecto = ? ORDER BY nombre ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, idProyecto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recursos.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logErrorDAO.log(e.getMessage(), getStackTraceString(e));
            throw e;
        }
        return recursos;
    }

    private Recurso mapRow(ResultSet rs) throws SQLException {
        Recurso r = new Recurso();
        r.setIdRecurso((UUID) rs.getObject("id_recurso"));
        r.setIdProyecto((UUID) rs.getObject("id_proyecto"));
        r.setNombre(rs.getString("nombre"));
        r.setTipo(rs.getString("tipo"));
        r.setUrl(rs.getString("url"));
        return r;
    }

    private String getStackTraceString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : t.getStackTrace()) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}
