package com.hospital.util;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database Manager class for H2 embedded database connection handling.
 * Implements Singleton pattern to ensure single connection pool.
 */
public class DatabaseManager {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final String DB_URL = "jdbc:h2:./data/hospital_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        try {
            Class.forName("org.h2.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "H2 Driver not found", e);
            throw new RuntimeException("Database driver not found", e);
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }
    
    /**
     * Initialize database schema and tables
     */
    private void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100)," +
                    "full_name VARCHAR(100)," +
                    "role VARCHAR(20) DEFAULT 'ADMIN'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            // Create Patients table
            stmt.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "patient_id VARCHAR(20) NOT NULL UNIQUE," +
                    "full_name VARCHAR(100) NOT NULL," +
                    "gender VARCHAR(10) NOT NULL," +
                    "age INT NOT NULL," +
                    "phone VARCHAR(20) NOT NULL," +
                    "address VARCHAR(255) NOT NULL," +
                    "blood_group VARCHAR(10)," +
                    "medical_history TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            // Create Doctors table
            stmt.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "doctor_id VARCHAR(20) NOT NULL UNIQUE," +
                    "full_name VARCHAR(100) NOT NULL," +
                    "specialization VARCHAR(100) NOT NULL," +
                    "phone VARCHAR(20) NOT NULL," +
                    "email VARCHAR(100) NOT NULL," +
                    "department VARCHAR(50) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            // Create Appointments table
            stmt.execute("CREATE TABLE IF NOT EXISTS appointments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "patient_name VARCHAR(100) NOT NULL," +
                    "doctor_name VARCHAR(100) NOT NULL," +
                    "appointment_date DATE NOT NULL," +
                    "appointment_time VARCHAR(10) NOT NULL," +
                    "status VARCHAR(20) NOT NULL DEFAULT 'Scheduled'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            // Create Bills table
            stmt.execute("CREATE TABLE IF NOT EXISTS bills (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "bill_number VARCHAR(30) NOT NULL UNIQUE," +
                    "patient_name VARCHAR(100) NOT NULL," +
                    "consultation_fee DECIMAL(10,2) NOT NULL," +
                    "medicine_fee DECIMAL(10,2) NOT NULL," +
                    "service_charge DECIMAL(10,2) NOT NULL," +
                    "tax_amount DECIMAL(10,2) NOT NULL," +
                    "total_amount DECIMAL(10,2) NOT NULL," +
                    "bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            // Create Settings table
            stmt.execute("CREATE TABLE IF NOT EXISTS settings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "setting_key VARCHAR(50) NOT NULL UNIQUE," +
                    "setting_value VARCHAR(255) NOT NULL" +
                    ")");
            
            LOGGER.info("Database schema initialized successfully");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Database connection closed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing database connection", e);
        }
    }
    
    /**
     * Check if database is accessible
     */
    public boolean isDatabaseAccessible() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database accessibility check failed", e);
            return false;
        }
    }
}
