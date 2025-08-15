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
    }
    
    private static void startWebServer() throws Exception {
        // Railway asigna el puerto automáticamente
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
        
        // Iniciar servidor
        server.start();
        System.out.println("🚀 Servidor web iniciado en puerto " + port);
        System.out.println("📡 Servidor listo para recibir peticiones HTTP");
        
        // Mantener el servidor vivo
        server.join();
    }
}