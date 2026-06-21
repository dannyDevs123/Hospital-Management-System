package com.hospital.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Session Manager for handling application navigation and session state.
 */
public class SessionManager {
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());
    private static SessionManager instance;
    
    private Stage primaryStage;
    private String currentTheme = "light";
    
    private SessionManager() {}
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Set the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Get the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Load a scene onto the primary stage
     */
    public void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Apply theme
            applyTheme(scene);
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading scene: " + fxmlPath, e);
        }
    }
    
    /**
     * Load a scene with specific dimensions
     */
    public void loadScene(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            
            // Apply theme
            applyTheme(scene);
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(width);
            primaryStage.setMinHeight(height);
            primaryStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading scene: " + fxmlPath, e);
        }
    }
    
    /**
     * Navigate to login screen
     */
    public void navigateToLogin() {
        loadScene("/fxml/LoginScreen.fxml", "MediCare - Login", 500, 600);
    }
    
    /**
     * Navigate to dashboard
     */
    public void navigateToDashboard() {
        loadScene("/fxml/MainDashboard.fxml", "MediCare Hospital Management System", 1200, 800);
    }
    
    /**
     * Navigate to splash screen
     */
    public void navigateToSplash() {
        loadScene("/fxml/SplashScreen.fxml", "MediCare - Loading", 600, 400);
    }
    
    /**
     * Apply theme to scene
     */
    public void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        
        // Always load base CSS
        var baseCss = getClass().getResource("/css/dark-theme.css");
        if (baseCss != null) {
            scene.getStylesheets().add(baseCss.toExternalForm());
        }
        
        // Load theme-specific CSS
        if ("dark".equals(currentTheme)) {
            var darkCss = getClass().getResource("/css/dark-theme.css");
            if (darkCss != null) {
                scene.getStylesheets().add(darkCss.toExternalForm());
            }
        }
    }
    
    /**
     * Set theme
     */
    public void setTheme(String theme) {
        this.currentTheme = theme;
        // Reapply theme to current scene
        if (primaryStage != null && primaryStage.getScene() != null) {
            applyTheme(primaryStage.getScene());
        }
    }
    
    /**
     * Get current theme
     */
    public String getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Save theme preference to database
     */
    public void saveThemePreference(String theme) {
        setTheme(theme);
        // Theme is saved via Settings screen controller
    }
}
