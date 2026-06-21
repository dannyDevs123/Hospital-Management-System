package com.hospital.controller;

import com.hospital.service.SessionManager;
import com.hospital.util.DatabaseManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Splash Screen.
 * Handles loading animation and auto-navigation to login.
 */
public class SplashScreenController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(SplashScreenController.class.getName());
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Label loadingLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize database and load system
        new Thread(this::loadSystem).start();
    }
    
    /**
     * Simulate loading process
     */
    private void loadSystem() {
        try {
            // Step 1: Initialize database (0-30%)
            updateProgress(0.1, "Initializing database...");
            Thread.sleep(400);
            
            DatabaseManager.getInstance();
            updateProgress(0.3, "Database connected");
            Thread.sleep(300);
            
            // Step 2: Load configuration (30-60%)
            updateProgress(0.45, "Loading configuration...");
            Thread.sleep(400);
            
            // Initialize sample data
            updateProgress(0.6, "Loading sample data...");
            SampleDataInitializer.initialize();
            Thread.sleep(300);
            
            // Step 3: Prepare UI (60-90%)
            updateProgress(0.75, "Preparing user interface...");
            Thread.sleep(400);
            
            updateProgress(0.9, "Almost ready...");
            Thread.sleep(300);
            
            // Step 4: Complete (90-100%)
            updateProgress(1.0, "Welcome!");
            Thread.sleep(200);
            
            // Navigate to login screen
            Platform.runLater(() -> SessionManager.getInstance().navigateToLogin());
            
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Loading interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during loading", e);
            Platform.runLater(() -> loadingLabel.setText("Error: " + e.getMessage()));
        }
    }
    
    /**
     * Update progress bar and loading text
     */
    private void updateProgress(double progress, String message) {
        Platform.runLater(() -> {
            progressBar.setProgress(progress);
            loadingLabel.setText(message);
        });
    }
}
