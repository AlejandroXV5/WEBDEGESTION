/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pms.java.swing.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

public final class DBConnection {

    private static HikariDataSource dataSource;

    static {
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            if (is != null) props.load(is);
            // Allow env var override for production
            String url = System.getenv().getOrDefault("JDBC_URL", props.getProperty("jdbc.url"));
            String user = System.getenv().getOrDefault("JDBC_USER", props.getProperty("jdbc.username"));
            String pass = System.getenv().getOrDefault("JDBC_PASS", props.getProperty("jdbc.password"));
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(pass);
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("jdbc.maxPoolSize","10")));
            config.setDriverClassName(props.getProperty("jdbc.driverClassName","org.postgresql.Driver"));
            // tuning recommended
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error inicializando DataSource: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
