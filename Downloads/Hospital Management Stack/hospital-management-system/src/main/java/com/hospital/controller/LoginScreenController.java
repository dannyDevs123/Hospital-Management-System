package com.hospital.controller;

import com.hospital.service.AuthService;
import com.hospital.service.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Login Screen.
 * Handles user authentication.
 */
public class LoginScreenController implements Initializable {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField visiblePasswordField;
    
    @FXML
    private Button togglePasswordBtn;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Hyperlink forgotPasswordLink;
    
    private boolean passwordVisible = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up keyboard shortcuts
        Platform.runLater(() -> {
            usernameField.getScene().setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER -> handleLogin();
                }
            });
        });
        
        // Set initial focus
        Platform.runLater(() -> usernameField.requestFocus());
    }
    
    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordVisible ? visiblePasswordField.getText() : passwordField.getText();
        
        // Validation
        if (username.isEmpty()) {
            showError("Please enter your username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        // Authenticate
        AuthService authService = AuthService.getInstance();
        if (authService.login(username, password)) {
            hideError();
            SessionManager.getInstance().navigateToDashboard();
        } else {
            showError("Invalid username or password");
            passwordField.clear();
            if (passwordVisible) {
                visiblePasswordField.clear();
            }
            passwordField.requestFocus();
        }
    }
    
    /**
     * Toggle password visibility
     */
    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordBtn.setText("🙈");
            visiblePasswordField.requestFocus();
            visiblePasswordField.positionCaret(visiblePasswordField.getText().length());
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            togglePasswordBtn.setText("👁");
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }
    
    /**
     * Handle forgot password
     */
    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText("Password Recovery");
        alert.setContentText("Please contact your system administrator to reset your password.\n\n" +
                "Default credentials:\nUsername: admin\nPassword: admin123");
        alert.showAndWait();
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
    
    /**
     * Hide error message
     */
    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
