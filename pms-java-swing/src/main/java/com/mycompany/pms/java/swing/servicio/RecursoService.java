package com.mycompany.pms.java.swing.servicio;

import com.mycompany.pms.java.swing.dao.ProyectoDAO;
import com.mycompany.pms.java.swing.dao.RecursoDAO;
import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.modelo.Recurso;
import com.mycompany.pms.java.swing.util.Validadores;

import java.sql.SQLException;
import java.util.UUID;

public class RecursoService {

    private final RecursoDAO recursoDAO;
    private final ProyectoDAO proyectoDAO;

    public RecursoService() {
        this.recursoDAO = new RecursoDAO();
        this.proyectoDAO = new ProyectoDAO();
    }

    public Recurso crear(UUID idProyecto, String nombre, String tipo, String url) throws SQLException {
        Validadores.requireNonBlank(nombre, "nombre");
        Validadores.maxLen(nombre, 150, "nombre");
        Validadores.urlValida(url);

        Proyecto p = proyectoDAO.findById(idProyecto);
        if (p == null) throw new IllegalArgumentException("Proyecto no existe.");

        Recurso r = new Recurso();
        r.setIdProyecto(idProyecto);
        r.setNombre(nombre.trim());
        r.setTipo(tipo);
        r.setUrl(url);
        return recursoDAO.create(r);
    }
}
