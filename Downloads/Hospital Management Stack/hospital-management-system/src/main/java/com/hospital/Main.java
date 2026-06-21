package com.hospital;

import com.hospital.service.SessionManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application entry point for MediCare Hospital Management System.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Configure primary stage
        primaryStage.setTitle("MediCare Hospital Management System");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Initialize session manager with primary stage
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.setPrimaryStage(primaryStage);
        
        // Show splash screen
        sessionManager.navigateToSplash();
    }
    
    @Override
    public void stop() {
        // Clean up resources
        com.hospital.util.DatabaseManager.getInstance().closeConnection();
    }
    
    /**
     * Application entry point
     */
    public static void main(String[] args) {
        launch(args);
    }
}
