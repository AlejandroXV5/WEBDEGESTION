/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pms.java.swing.modelo;

import java.util.UUID;

/**
 *
 * @author Ronny
 */
public class Recurso {
    private UUID idRecurso;
    private UUID idProyecto;
    private String nombre;
    private String tipo;
    private String url;

    public Recurso() {
    }

    public Recurso(UUID idRecurso, UUID idProyecto, String nombre, String tipo, String url) {
        this.idRecurso = idRecurso;
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.tipo = tipo;
        this.url = url;
    }

    public UUID getIdRecurso() {
        return idRecurso;
    }

    public void setIdRecurso(UUID idRecurso) {
        this.idRecurso = idRecurso;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Recurso{" +
                "idRecurso=" + idRecurso +
                ", idProyecto=" + idProyecto +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
