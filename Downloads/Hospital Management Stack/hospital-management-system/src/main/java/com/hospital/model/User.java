package com.hospital.model;

import javafx.beans.property.*;

/**
 * User model class for authentication and user management.
 */
public class User {
    private final IntegerProperty id;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty email;
    private final StringProperty fullName;
    private final StringProperty role;
    private final ObjectProperty<java.time.LocalDateTime> createdAt;
    
    public User() {
        this.id = new SimpleIntegerProperty();
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.fullName = new SimpleStringProperty();
        this.role = new SimpleStringProperty("ADMIN");
        this.createdAt = new SimpleObjectProperty<>();
    }
    
    public User(String username, String password, String email, String fullName) {
        this();
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setFullName(fullName);
    }
    
    // ID Property
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }
    
    // Username Property
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }
    
    // Password Property
    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }
    
    // Email Property
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }
    
    // Full Name Property
    public String getFullName() { return fullName.get(); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }
    public StringProperty fullNameProperty() { return fullName; }
    
    // Role Property
    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }
    public StringProperty roleProperty() { return role; }
    
    // Created At Property
    public java.time.LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<java.time.LocalDateTime> createdAtProperty() { return createdAt; }
    
    @Override
    public String toString() {
        return "User{" + "username='" + getUsername() + '\'' + ", fullName='" + getFullName() + '\'' + '}';
    }
}
