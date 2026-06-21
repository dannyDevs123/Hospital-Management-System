package com.hospital.controller;

import com.hospital.model.User;
import com.hospital.service.AuthService;
import com.hospital.service.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Settings screen.
 */
public class SettingsController implements Initializable {
    
    @FXML private ComboBox<String> themeCombo;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private Label profileMessageLabel;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordMessageLabel;
    
    private final AuthService authService = AuthService.getInstance();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup theme combo
        themeCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Light", "Dark", "System Default"
        ));
        
        // Load current settings
        loadUserProfile();
        
        // Set current theme
        String currentTheme = SessionManager.getInstance().getCurrentTheme();
        themeCombo.setValue(currentTheme.substring(0, 1).toUpperCase() + currentTheme.substring(1));
    }
    
    private void loadUserProfile() {
        User user = authService.getCurrentUser();
        if (user != null) {
            usernameField.setText(user.getUsername());
            fullNameField.setText(user.getFullName() != null ? user.getFullName() : "");
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
        }
    }
    
    @FXML
    private void handleApplyTheme() {
        String selectedTheme = themeCombo.getValue();
        if (selectedTheme == null) return;
        
        String theme = selectedTheme.toLowerCase();
        if (theme.equals("system default")) {
            theme = "light"; // Default to light
        }
        
        SessionManager.getInstance().setTheme(theme);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Theme Applied");
        alert.setHeaderText(null);
        alert.setContentText("Theme has been applied successfully!");
        alert.show();
    }
    
    @FXML
    private void handleSaveProfile() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        
        if (fullName.isEmpty()) {
            showProfileMessage("Full name is required", true);
            return;
        }
        
        if (email.isEmpty()) {
            showProfileMessage("Email is required", true);
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showProfileMessage("Please enter a valid email", true);
            return;
        }
        
        if (authService.updateProfile(email, fullName)) {
            showProfileMessage("Profile saved successfully!", false);
        } else {
            showProfileMessage("Failed to save profile", true);
        }
    }
    
    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validation
        if (currentPassword.isEmpty()) {
            showPasswordMessage("Current password is required", true);
            return;
        }
        
        if (newPassword.isEmpty()) {
            showPasswordMessage("New password is required", true);
            return;
        }
        
        if (newPassword.length() < 6) {
            showPasswordMessage("New password must be at least 6 characters", true);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showPasswordMessage("Passwords do not match", true);
            return;
        }
        
        if (newPassword.equals(currentPassword)) {
            showPasswordMessage("New password must be different from current", true);
            return;
        }
        
        // Change password
        if (authService.changePassword(currentPassword, newPassword)) {
            showPasswordMessage("Password changed successfully!", false);
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            showPasswordMessage("Current password is incorrect", true);
        }
    }
    
    private void showProfileMessage(String message, boolean isError) {
        profileMessageLabel.setText(message);
        profileMessageLabel.setStyle(isError ? "-fx-text-fill: #EF4444;" : "-fx-text-fill: #22C55E;");
        profileMessageLabel.setVisible(true);
        profileMessageLabel.setManaged(true);
    }
    
    private void showPasswordMessage(String message, boolean isError) {
        passwordMessageLabel.setText(message);
        passwordMessageLabel.setStyle(isError ? "-fx-text-fill: #EF4444;" : "-fx-text-fill: #22C55E;");
        passwordMessageLabel.setVisible(true);
        passwordMessageLabel.setManaged(true);
    }
}
