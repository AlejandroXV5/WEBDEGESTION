/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pms.java.swing.modelo;

import java.time.LocalDateTime;

/**
 *
 * @author Ronny
 */
public class LogError {

    private int idError;
    private String mensaje;
    private LocalDateTime fecha;
    private String stacktrace;

    public LogError() {
    }

    public LogError(int idError, String mensaje, LocalDateTime fecha, String stacktrace) {
        this.idError = idError;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.stacktrace = stacktrace;
    }

    public int getIdError() {
        return idError;
    }

    public void setIdError(int idError) {
        this.idError = idError;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    @Override
    public String toString() {
        return "LogError{"
                + "idError=" + idError
                + ", mensaje='" + mensaje + '\''
                + ", fecha=" + fecha
                + ", stacktrace='" + stacktrace + '\''
                + '}';
    }
}
