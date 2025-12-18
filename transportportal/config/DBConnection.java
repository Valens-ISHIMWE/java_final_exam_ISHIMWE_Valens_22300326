package com.transportportal.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection {

    private static Connection conn;

    public static Connection getConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) return conn;

        Properties p = new Properties();
        InputStream in = null;

        try {
            // Try to load from classpath (for development)
            in = DBConnection.class.getClassLoader().getResourceAsStream("config.properties");
            
            if (in == null) {
                // Try to load from file system (for production)
                in = new FileInputStream("config.properties");
            }
            
            if (in == null) {
                throw new SQLException("config.properties not found in classpath or current directory!");
            }

            p.load(in);
            String driver = p.getProperty("db.driver");
            String url = p.getProperty("db.url");
            String user = p.getProperty("db.user");
            String pass = p.getProperty("db.password");

            // Validate required properties
            if (url == null || user == null) {
                throw new SQLException("Database configuration properties are missing in config.properties");
            }

            // Load driver if specified
            if (driver != null && !driver.trim().isEmpty()) {
                try {
                    Class.forName(driver);
                } catch (ClassNotFoundException e) {
                    System.err.println("JDBC driver not found: " + driver);
                    // Continue anyway, DriverManager might still work
                }
            }

            conn = DriverManager.getConnection(url, user, pass);
            return conn;
            
        } catch (Exception e) {
            throw new SQLException("Failed to load DB connection: " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try { in.close(); } catch (Exception e) { /* ignore */ }
            }
        }
    }

    public static void closeConnection() {
        if (conn != null) {
            try { 
                conn.close(); 
                conn = null;
            } catch (SQLException ignored) {}
        }
    }
}