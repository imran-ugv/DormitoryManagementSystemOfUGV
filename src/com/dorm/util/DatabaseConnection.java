package com.dorm.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ugv_dorm_db";
    private static final String USER = "root";
    private static final String PASS = "imran@ugv963";
    public static Connection getConnection() {
        try {
            // Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Return connection object
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.err.println("CRITICAL: Database Connection Failed!");
            System.err.println("Check if MySQL Server is running and Password is correct.");
            e.printStackTrace();
            return null;
        }
    }
}