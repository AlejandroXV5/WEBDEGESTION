package com.mycompany.pms.java.swing;

import com.mycompany.pms.java.swing.util.DBConnection;
import com.mycompany.pms.java.swing.util.TestConnection;

/**
 * Clase principal del Sistema de Gestión de Proyectos.
 * Versión simplificada sin errores de compilación.
 * 
 * @author Sistema de Gestión de Proyectos
 * @version 1.0
 */
public class WebMain {
    
    /**
     * Método principal de la aplicación
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Mostrar banner de inicio
        showStartupBanner();
        
        // Probar conexión a BD
        if (!testDatabaseConnection()) {
            System.err.println("❌ Error: No se pudo conectar a la base de datos");
            System.err.println("Verifica la configuración en db.properties");
            System.exit(1);
        }
        
        // Determinar modo de ejecución
        String mode = getExecutionMode(args);
        
        switch (mode.toLowerCase()) {
            case "test":
                runDatabaseTests();
                break;
            case "web":
                startWebMode();
                break;
            default:
                runDatabaseTests();
                break;
        }
    }
    
    /**
     * Muestra el banner de inicio de la aplicación
     */
    private static void showStartupBanner() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║            SISTEMA DE GESTIÓN DE PROYECTOS                  ║");
        System.out.println("║                    Versión 1.0                              ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Tecnologías: Java 21 + Swing + PostgreSQL + HikariCP      ║");
        System.out.println("║  Autor: Proyecto Universitario                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Prueba la conexión a la base de datos
     * @return true si la conexión es exitosa
     */
    private static boolean testDatabaseConnection() {
        System.out.println("🔍 Verificando conexión a PostgreSQL...");
        
        try {
            java.sql.Connection conn = DBConnection.getConnection();
            
            if (conn != null && conn.isValid(5)) {
                System.out.println("✅ Conexión a base de datos establecida");
                System.out.println("📊 DataSource: " + DBConnection.getDataSource().getClass().getSimpleName());
                conn.close();
                return true;
            } else {
                System.out.println("❌ Error: Conexión no válida");
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error crítico en BD: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Determina el modo de ejecución basado en argumentos
     * @param args Argumentos de línea de comandos
     * @return Modo de ejecución
     */
    private static String getExecutionMode(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        
        // Si está en Railway (tiene PORT), ejecutar en modo web
        if (System.getenv("PORT") != null) {
            return "web";
        }
        
        // Por defecto, modo test
        return "test";
    }
    
    /**
     * Ejecuta pruebas de base de datos
     */
    private static void runDatabaseTests() {
        System.out.println("\n🧪 Ejecutando pruebas de base de datos...");
        
        try {
            TestConnection tester = new TestConnection();
            tester.runAllTests();
            System.out.println("\n✅ Todas las pruebas completadas");
        } catch (Exception e) {
            System.err.println("❌ Error en pruebas: " + e.getMessage());
        }
    }
    
    /**
     * Inicia modo web (para Railway)
     */
    private static void startWebMode() {
        System.out.println("🌐 Modo Web activado");
        System.out.println("📝 Servidor web será implementado en Día 8");
        
        // Por ahora, ejecutar pruebas
        runDatabaseTests();
    }
}