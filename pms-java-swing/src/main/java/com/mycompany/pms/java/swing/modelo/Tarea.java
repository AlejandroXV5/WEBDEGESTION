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
public class Tarea {
    private UUID idTarea;
    private UUID idProyecto;
    private UUID idUsuarioAsignado;
    private String titulo;
    private String descripcion;
    private LocalDate fechaLimite;
    private boolean completada;

    public Tarea() {
    }

    public Tarea(UUID idTarea, UUID idProyecto, UUID idUsuarioAsignado, String titulo, String descripcion, LocalDate fechaLimite, boolean completada) {
        this.idTarea = idTarea;
        this.idProyecto = idProyecto;
        this.idUsuarioAsignado = idUsuarioAsignado;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.completada = completada;
    }

    public UUID getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(UUID idTarea) {
        this.idTarea = idTarea;
    }

    public UUID getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(UUID idProyecto) {
        this.idProyecto = idProyecto;
    }

    public UUID getIdUsuarioAsignado() {
        return idUsuarioAsignado;
    }

    public void setIdUsuarioAsignado(UUID idUsuarioAsignado) {
        this.idUsuarioAsignado = idUsuarioAsignado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "idTarea=" + idTarea +
                ", idProyecto=" + idProyecto +
                ", idUsuarioAsignado=" + idUsuarioAsignado +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaLimite=" + fechaLimite +
                ", completada=" + completada +
                '}';
    }
}
