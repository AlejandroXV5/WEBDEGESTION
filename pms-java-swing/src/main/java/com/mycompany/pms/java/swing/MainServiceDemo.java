package com.mycompany.pms.java.swing;

import com.mycompany.pms.java.swing.modelo.Proyecto;
import com.mycompany.pms.java.swing.modelo.Tarea;
import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.servicio.*;

import java.time.LocalDate;
import java.util.UUID;

public class MainServiceDemo {
    public static void main(String[] args) throws Exception {
        UsuarioService us = new UsuarioService();
        ProyectoService ps = new ProyectoService();
        TareaService ts = new TareaService();

        Usuario u = us.registrar("Ana Admin", "ana@example.com", "Str0ng!Pass", "ADMIN");
        Proyecto p = ps.crear("Proyecto Demo", "Prueba de servicios",
                LocalDate.now(), LocalDate.now().plusDays(30), "EN_PROGRESO");

        Tarea t = ts.crear(p.getIdProyecto(), u.getIdUsuario(), "Configurar CI",
                "Agregar workflow", LocalDate.now().plusDays(7), false);

        ts.marcarCompletada(t.getIdTarea(), true);
        double progreso = ps.progreso(p.getIdProyecto());
        System.out.println("Progreso: " + progreso + "%");
    }
}
