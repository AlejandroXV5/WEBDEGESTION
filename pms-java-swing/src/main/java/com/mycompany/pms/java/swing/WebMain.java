// WebMain.java
package com.mycompany.pms.java.swing;

import com.mycompany.pms.java.swing.web.AuthServlet;
import com.mycompany.pms.java.swing.web.ProyectoServlet;
import com.mycompany.pms.java.swing.web.RecursoServlet;
import com.mycompany.pms.java.swing.web.StaticServlet;
import com.mycompany.pms.java.swing.web.StatusServlet;
import com.mycompany.pms.java.swing.web.TareaServlet;
import com.mycompany.pms.java.swing.web.UsuarioServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebMain {
    public static void main(String[] args) throws Exception {
        startWebServer();

        // Opción: Iniciar UI de Swing (solo para prueba de carga)
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Main().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al abrir MainFrame: " + e.getMessage());
            }
        });

        Thread.currentThread().join(); // Mantener vivo el proceso
    }

    private static void startWebServer() throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Registra los servlets
        context.addServlet(new ServletHolder(new StatusServlet()), "/");
        context.addServlet(new ServletHolder(new AuthServlet()), "/auth");
        context.addServlet(new ServletHolder(new ProyectoServlet()), "/proyectos");
        context.addServlet(new ServletHolder(new RecursoServlet()), "/recursos");
        context.addServlet(new ServletHolder(new StaticServlet()), "/static");
        context.addServlet(new ServletHolder(new TareaServlet()), "/tareas");
        context.addServlet(new ServletHolder(new UsuarioServlet()), "/usuarios");

        server.start();
        System.out.println("🌐 Servidor web iniciado en puerto " + port);
    }
}