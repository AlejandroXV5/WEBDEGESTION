/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.pms.java.swing;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Ronny
 */
public class PmsJavaSwing {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/pmsdb";
        String user = "pms_user";
        String password = "admin";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conexion exitosa a PostgreSQL");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
