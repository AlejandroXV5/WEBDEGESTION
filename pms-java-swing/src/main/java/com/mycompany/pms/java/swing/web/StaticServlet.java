package com.mycompany.pms.java.swing.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet para servir páginas estáticas HTML del Sistema de Gestión de Proyectos.
 * Maneja las rutas principales de la aplicación web.
 * 
 * @author Sistema de Gestión de Proyectos
 * @version 1.0
 */
public class StaticServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        
        // Manejar diferentes rutas
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/app")) {
            servirPaginaPrincipal(resp);
        } else if (pathInfo.equals("/login")) {
            servirPaginaLogin(resp);
        } else if (pathInfo.equals("/proyectos")) {
            servirPaginaProyectos(resp);
        } else if (pathInfo.equals("/tareas")) {
            servirPaginaTareas(resp);
        } else {
            servirPaginaError404(resp);
        }
    }
    
    /**
     * Sirve la página principal del sistema
     */
    private void servirPaginaPrincipal(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        try (PrintWriter writer = resp.getWriter()) {
            String html = """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema de Gestión de Proyectos</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: white;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: rgba(255, 255, 255, 0.1);
            padding: 30px;
            border-radius: 15px;
            backdrop-filter: blur(10px);
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            margin-bottom: 30px;
            font-size: 2.5em;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }
        .form-container {
            background: rgba(255, 255, 255, 0.2);
            padding: 25px;
            border-radius: 10px;
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            font-size: 1.1em;
        }
        input, textarea {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            background: rgba(255, 255, 255, 0.9);
            color: #333;
            box-sizing: border-box;
        }
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        button {
            background: #4CAF50;
            color: white;
            padding: 15px 30px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            transition: background 0.3s;
            width: 100%;
        }
        button:hover {
            background: #45a049;
        }
        .nav-links {
            text-align: center;
            margin-bottom: 30px;
        }
        .nav-links a {
            color: white;
            text-decoration: none;
            margin: 0 15px;
            padding: 10px 20px;
            border: 2px solid white;
            border-radius: 25px;
            transition: all 0.3s;
        }
        .nav-links a:hover {
            background: white;
            color: #667eea;
        }
        .status-message {
            padding: 15px;
            border-radius: 8px;
            margin-top: 20px;
            display: none;
        }
        .success {
            background: rgba(76, 175, 80, 0.8);
            border: 1px solid #4CAF50;
        }
        .error {
            background: rgba(244, 67, 54, 0.8);
            border: 1px solid #f44336;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 Sistema de Gestión de Proyectos</h1>
        
        <div class="nav-links">
            <a href="/app">Inicio</a>
            <a href="/app/proyectos">Proyectos</a>
            <a href="/app/tareas">Tareas</a>
            <a href="/app/login">Login</a>
        </div>
        
        <div class="form-container">
            <h2>📋 Crear Nuevo Proyecto</h2>
            <form id="form-proyecto">
                <div class="form-group">
                    <label for="nombre">Nombre del Proyecto:</label>
                    <input type="text" id="nombre" name="nombre" required 
                           placeholder="Ej: Sistema de Inventario">
                </div>
                
                <div class="form-group">
                    <label for="descripcion">Descripción:</label>
                    <textarea id="descripcion" name="descripcion" 
                              placeholder="Describe el objetivo y alcance del proyecto..."></textarea>
                </div>
                
                <div class="form-group">
                    <label for="fechaInicio">Fecha de Inicio:</label>
                    <input type="date" id="fechaInicio" name="fechaInicio" required>
                </div>
                
                <div class="form-group">
                    <label for="fechaFin">Fecha de Finalización:</label>
                    <input type="date" id="fechaFin" name="fechaFin">
                </div>
                
                <button type="submit">✨ Crear Proyecto</button>
            </form>
            
            <div id="status-message" class="status-message"></div>
        </div>
    </div>
    
    <script>
        // Establecer fecha mínima como hoy
        document.getElementById('fechaInicio').valueAsDate = new Date();
        
        // Manejar envío del formulario
        document.getElementById('form-proyecto').addEventListener('submit', async (event) => {
            event.preventDefault();
            
            const formData = {
                nombre: document.getElementById('nombre').value.trim(),
                descripcion: document.getElementById('descripcion').value.trim(),
                fechaInicio: document.getElementById('fechaInicio').value,
                fechaFin: document.getElementById('fechaFin').value
            };
            
            // Validaciones básicas
            if (!formData.nombre) {
                mostrarMensaje('El nombre del proyecto es obligatorio', 'error');
                return;
            }
            
            if (formData.fechaFin && formData.fechaFin < formData.fechaInicio) {
                mostrarMensaje('La fecha de finalización no puede ser anterior a la fecha de inicio', 'error');
                return;
            }
            
            try {
                mostrarMensaje('Creando proyecto...', 'success');
                
                const response = await fetch('/api/proyectos', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });
                
                if (response.ok) {
                    const resultado = await response.json();
                    mostrarMensaje(`✅ Proyecto "${formData.nombre}" creado exitosamente`, 'success');
                    document.getElementById('form-proyecto').reset();
                    document.getElementById('fechaInicio').valueAsDate = new Date();
                } else {
                    const error = await response.text();
                    mostrarMensaje(`❌ Error al crear el proyecto: ${error}`, 'error');
                }
            } catch (error) {
                console.error('Error creando proyecto:', error);
                mostrarMensaje('❌ Error de conexión. Verifica que el servidor esté funcionando.', 'error');
            }
        });
        
        function mostrarMensaje(mensaje, tipo) {
            const statusDiv = document.getElementById('status-message');
            statusDiv.textContent = mensaje;
            statusDiv.className = `status-message ${tipo}`;
            statusDiv.style.display = 'block';
            
            // Ocultar mensaje después de 5 segundos si es de éxito
            if (tipo === 'success') {
                setTimeout(() => {
                    statusDiv.style.display = 'none';
                }, 5000);
            }
        }
    </script>
</body>
</html>
""";
            writer.write(html);
        }
    }
    
    /**
     * Sirve la página de login
     */
    private void servirPaginaLogin(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        try (PrintWriter writer = resp.getWriter()) {
            String html = """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema de Gestión de Proyectos</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-container {
            background: rgba(255, 255, 255, 0.1);
            padding: 40px;
            border-radius: 15px;
            backdrop-filter: blur(10px);
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
            color: white;
        }
        h1 {
            text-align: center;
            margin-bottom: 30px;
            font-size: 2em;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }
        input {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            background: rgba(255, 255, 255, 0.9);
            color: #333;
            box-sizing: border-box;
        }
        button {
            background: #4CAF50;
            color: white;
            padding: 15px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            width: 100%;
            transition: background 0.3s;
        }
        button:hover {
            background: #45a049;
        }
        .back-link {
            text-align: center;
            margin-top: 20px;
        }
        .back-link a {
            color: white;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h1>🔐 Iniciar Sesión</h1>
        <form id="form-login">
            <div class="form-group">
                <label for="usuario">Usuario:</label>
                <input type="text" id="usuario" name="usuario" required>
            </div>
            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit">Iniciar Sesión</button>
        </form>
        <div class="back-link">
            <a href="/app">← Volver al inicio</a>
        </div>
    </div>
    
    <script>
        document.getElementById('form-login').addEventListener('submit', async (event) => {
            event.preventDefault();
            alert('🚧 Funcionalidad de login será implementada en Día 4');
        });
    </script>
</body>
</html>
""";
            writer.write(html);
        }
    }
    
    /**
     * Sirve la página de gestión de proyectos
     */
    private void servirPaginaProyectos(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("<html><body><h1>📋 Gestión de Proyectos</h1>");
            writer.write("<p>🚧 Esta página será implementada en los próximos días</p>");
            writer.write("<a href='/app'>← Volver</a></body></html>");
        }
    }
    
    /**
     * Sirve la página de gestión de tareas
     */
    private void servirPaginaTareas(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("<html><body><h1>✅ Gestión de Tareas</h1>");
            writer.write("<p>🚧 Esta página será implementada en los próximos días</p>");
            writer.write("<a href='/app'>← Volver</a></body></html>");
        }
    }
    
    /**
     * Sirve página de error 404
     */
    private void servirPaginaError404(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        
        try (PrintWriter writer = resp.getWriter()) {
            String html = """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>404 - Página no encontrada</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
            padding: 50px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            min-height: 100vh;
            margin: 0;
        }
        h1 { font-size: 3em; }
        a {
            color: white;
            text-decoration: none;
            border: 2px solid white;
            padding: 10px 20px;
            border-radius: 25px;
            display: inline-block;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <h1>404</h1>
    <p>❌ Página no encontrada</p>
    <a href="/app">🏠 Volver al inicio</a>
</body>
</html>
""";
            writer.write(html);
        }
    }
}