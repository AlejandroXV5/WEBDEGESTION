package com.mycompany.pms.java.swing.dao;

import com.mycompany.pms.java.swing.modelo.LogError;
import com.mycompany.pms.java.swing.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogErrorDAO {

    public void log(String mensaje, String stacktrace) {
        String sql = "INSERT INTO log_errores (mensaje, stacktrace) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mensaje);
            ps.setString(2, stacktrace);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();  // Evitar log infinito
        }
    }

    public List<LogError> listAll() {
        List<LogError> logs = new ArrayList<>();
        String sql = "SELECT id_error, mensaje, fecha, stacktrace FROM log_errores ORDER BY fecha DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LogError log = new LogError();
                log.setIdError(rs.getInt("id_error"));
                log.setMensaje(rs.getString("mensaje"));
                Timestamp ts = rs.getTimestamp("fecha");
                log.setFecha(ts != null ? ts.toLocalDateTime() : null);
                log.setStacktrace(rs.getString("stacktrace"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
