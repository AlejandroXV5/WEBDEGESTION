/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pms.java.swing.modelo;

import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * @author Ronny
 */
public class Proyecto {

    private UUID idProyecto;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;

    public Proyecto() {
    }

    public Proyecto(UUID idProyecto, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, String estado) {
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public UUID getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(UUID idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Proyecto{"
                + "idProyecto=" + idProyecto
                + ", nombre='" + nombre + '\''
                + ", descripcion='" + descripcion + '\''
                + ", fechaInicio=" + fechaInicio
                + ", fechaFin=" + fechaFin
                + ", estado='" + estado + '\''
                + '}';
    }
}
