package com.hospital.service;

import com.hospital.dao.UserDAO;
import com.hospital.model.User;

/**
 * Service class for authentication operations.
 */
public class AuthService {
    private static AuthService instance;
    private final UserDAO userDAO;
    private User currentUser;
    
    private AuthService() {
        this.userDAO = new UserDAO();
    }
    
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * Authenticate user
     */
    public boolean login(String username, String password) {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        return false;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Get current logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Update user profile
     */
    public boolean updateProfile(String email, String fullName) {
        if (currentUser != null) {
            currentUser.setEmail(email);
            currentUser.setFullName(fullName);
            return userDAO.updateProfile(currentUser);
        }
        return false;
    }
    
    /**
     * Change password
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentUser != null) {
            // Verify current password
            User verified = userDAO.authenticate(currentUser.getUsername(), currentPassword);
            if (verified != null) {
                return userDAO.updatePassword(currentUser.getId(), newPassword);
            }
        }
        return false;
    }
}
