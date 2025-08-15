package com.mycompany.pms.java.swing.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 * Clase para probar la conexión a la base de datos y mostrar información básica.
 * Adaptada para funcionar con tu implementación de DBConnection.
 * 
 * @author Sistema de Gestión de Proyectos
 * @version 1.0
 */
public class TestConnection {
    
    /**
     * Método principal para ejecutar pruebas de conexión
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE GESTIÓN DE PROYECTOS ===");
        System.out.println("Iniciando prueba de conexión a PostgreSQL...\n");
        
        TestConnection tester = new TestConnection();
        tester.runAllTests();
        
        System.out.println("\n=== PRUEBA COMPLETADA ===");
    }
    
    /**
     * Ejecuta todas las pruebas de conexión
     */
    public void runAllTests() {
        // 1. Probar conexión básica
        testBasicConnection();
        
        // 2. Probar información de base de datos
        testDatabaseInfo();
        
        // 3. Probar ejecución de consulta simple
        testSimpleQuery();
        
        // 4. Probar DataSource
        testDataSource();
    }
    
    /**
     * Prueba la conexión básica a la base de datos
     */
    private void testBasicConnection() {
        System.out.println("🔍 Probando conexión básica...");
        
        try (Connection conn = DBConnection.getConnection()) {
            
            if (conn != null && conn.isValid(5)) {
                System.out.println("✅ Conexión establecida correctamente");
                System.out.println("   - AutoCommit: " + conn.getAutoCommit());
                System.out.println("   - Catalog: " + conn.getCatalog());
                System.out.println("   - Schema: " + conn.getSchema());
                
                // Mostrar diálogo de éxito (opcional para interfaz gráfica)
                showSuccessDialog();
                
            } else {
                System.out.println("❌ ERROR: Conexión no válida");
                showErrorDialog();
            }
            
        } catch (SQLException e) {
            System.out.println("❌ ERROR: No se pudo establecer conexión");
            System.out.println("   Detalle: " + e.getMessage());
            showErrorDialog();
        }
    }
    
    /**
     * Obtiene y muestra información de la base de datos
     */
    private void testDatabaseInfo() {
        System.out.println("\n🔍 Obteniendo información de base de datos...");
        
        try (Connection conn = DBConnection.getConnection()) {
            
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("📊 Información de PostgreSQL:");
            System.out.println("   - Producto: " + metaData.getDatabaseProductName());
            System.out.println("   - Versión: " + metaData.getDatabaseProductVersion());
            System.out.println("   - Driver: " + metaData.getDriverName());
            System.out.println("   - Versión Driver: " + metaData.getDriverVersion());
            System.out.println("   - URL: " + metaData.getURL());
            System.out.println("   - Usuario: " + metaData.getUserName());
            System.out.println("   - Máx. Conexiones: " + metaData.getMaxConnections());
            
        } catch (SQLException e) {
            System.out.println("❌ ERROR obteniendo info BD: " + e.getMessage());
        }
    }
    
    /**
     * Ejecuta una consulta simple para verificar funcionalidad
     */
    private void testSimpleQuery() {
        System.out.println("\n🔍 Probando consulta simple...");
        
        String query = "SELECT current_database(), current_user, version(), now()";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                System.out.println("✅ Consulta ejecutada correctamente:");
                System.out.println("   - Base de datos actual: " + rs.getString(1));
                System.out.println("   - Usuario actual: " + rs.getString(2));
                System.out.println("   - Versión PostgreSQL: " + rs.getString(3).split(" ")[0] + " " + rs.getString(3).split(" ")[1]);
                System.out.println("   - Fecha/Hora servidor: " + rs.getTimestamp(4));
            }
            
        } catch (SQLException e) {
            System.out.println("❌ ERROR en consulta: " + e.getMessage());
        }
    }
    
    /**
     * Prueba el DataSource de HikariCP
     */
    private void testDataSource() {
        System.out.println("\n🔍 Probando DataSource de HikariCP...");
        
        try {
            javax.sql.DataSource ds = DBConnection.getDataSource();
            
            if (ds != null) {
                System.out.println("✅ DataSource disponible");
                System.out.println("   - Clase: " + ds.getClass().getSimpleName());
                
                // Probar múltiples conexiones para verificar el pool
                testMultipleConnections();
                
            } else {
                System.out.println("❌ DataSource no disponible");
            }
            
        } catch (Exception e) {
            System.out.println("❌ ERROR en DataSource: " + e.getMessage());
        }
    }
    
    /**
     * Prueba múltiples conexiones para verificar el pool
     */
    private void testMultipleConnections() {
        System.out.println("\n🔍 Probando pool de conexiones...");
        
        try {
            // Abrir 3 conexiones simultáneamente
            Connection conn1 = DBConnection.getConnection();
            Connection conn2 = DBConnection.getConnection();
            Connection conn3 = DBConnection.getConnection();
            
            System.out.println("✅ Pool funcionando correctamente:");
            System.out.println("   - Conexión 1: " + (conn1.isValid(1) ? "Válida" : "Inválida"));
            System.out.println("   - Conexión 2: " + (conn2.isValid(1) ? "Válida" : "Inválida"));
            System.out.println("   - Conexión 3: " + (conn3.isValid(1) ? "Válida" : "Inválida"));
            
            // Cerrar conexiones
            conn1.close();
            conn2.close();
            conn3.close();
            
            System.out.println("   - Conexiones cerradas correctamente");
            
        } catch (SQLException e) {
            System.out.println("❌ ERROR en pool: " + e.getMessage());
        }
    }
    
    /**
     * Muestra diálogo de éxito (para interfaz gráfica)
     */
    private void showSuccessDialog() {
        try {
            JOptionPane.showMessageDialog(
                null,
                "✅ Conexión a PostgreSQL establecida correctamente\n" +
                "Base de datos: gestion_proyectos\n" +
                "Pool de conexiones HikariCP funcionando\n" +
                "¡El sistema está listo para usar!",
                "Conexión Exitosa",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            // Si no hay entorno gráfico, ignorar
            System.out.println("(Diálogo gráfico no disponible en este entorno)");
        }
    }
    
    /**
     * Muestra diálogo de error (para interfaz gráfica)
     */
    private void showErrorDialog() {
        try {
            JOptionPane.showMessageDialog(
                null,
                "❌ Error estableciendo conexión con PostgreSQL\n\n" +
                "Verifica:\n" +
                "• PostgreSQL está ejecutándose\n" +
                "• La base de datos 'gestion_proyectos' existe\n" +
                "• Las credenciales en db.properties son correctas\n" +
                "• El puerto 5432 está disponible\n" +
                "• El archivo db.properties está en src/main/resources/",
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            // Si no hay entorno gráfico, ignorar
            System.out.println("(Diálogo gráfico no disponible en este entorno)");
        }
    }
    
    /**
     * Método utilitario para probar conexión desde otras clases
     * @return true si la conexión es exitosa
     */
    public static boolean isConnectionAvailable() {
        try (Connection conn = DBConnection.getConnection()) {
            return conn != null && conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}