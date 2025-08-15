package com.mycompany.pms.java.swing.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;

public final class DBConnection {

    private static HikariDataSource dataSource;

    static {
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            if (is != null) {
                props.load(is);
            } else {
                throw new RuntimeException("No se encontró db.properties");
            }

            String url = resolveProperty(props.getProperty("jdbc.url"), "JDBC_URL");
            String user = resolveProperty(props.getProperty("jdbc.username"), "JDBC_USER");
            String pass = resolveProperty(props.getProperty("jdbc.password"), "JDBC_PASS");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(pass);
            config.setDriverClassName(props.getProperty("jdbc.driverClassName", "org.postgresql.Driver"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("jdbc.maxPoolSize", "10")));

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error inicializando DataSource: " + e.getMessage());
        }
    }

    private static String resolveProperty(String property, String envVarName) {
        if (property == null) return null;

        Pattern pattern = Pattern.compile("\\$\\{([^}:]+):([^}]*)\\}");
        Matcher matcher = pattern.matcher(property);

        if (matcher.find()) {
            String envVarKey = matcher.group(1);
            String defaultValue = matcher.group(2);
            String envValue = System.getenv(envVarKey);
            String resolvedValue = (envValue != null) ? envValue : defaultValue;
            return property.replace(matcher.group(0), resolvedValue);
        }

        return property;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}