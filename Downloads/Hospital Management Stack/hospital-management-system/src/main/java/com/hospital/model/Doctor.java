package com.hospital.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

/**
 * Doctor model class for doctor management.
 */
public class Doctor {
    private final IntegerProperty id;
    private final StringProperty doctorId;
    private final StringProperty fullName;
    private final StringProperty specialization;
    private final StringProperty phone;
    private final StringProperty email;
    private final StringProperty department;
    private final ObjectProperty<LocalDateTime> createdAt;
    
    public Doctor() {
        this.id = new SimpleIntegerProperty();
        this.doctorId = new SimpleStringProperty();
        this.fullName = new SimpleStringProperty();
        this.specialization = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.department = new SimpleStringProperty();
        this.createdAt = new SimpleObjectProperty<>();
    }
    
    public Doctor(String doctorId, String fullName, String specialization, 
                  String phone, String email, String department) {
        this();
        setDoctorId(doctorId);
        setFullName(fullName);
        setSpecialization(specialization);
        setPhone(phone);
        setEmail(email);
        setDepartment(department);
    }
    
    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }
    
    // Doctor ID
    public String getDoctorId() { return doctorId.get(); }
    public void setDoctorId(String doctorId) { this.doctorId.set(doctorId); }
    public StringProperty doctorIdProperty() { return doctorId; }
    
    // Full Name
    public String getFullName() { return fullName.get(); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }
    public StringProperty fullNameProperty() { return fullName; }
    
    // Specialization
    public String getSpecialization() { return specialization.get(); }
    public void setSpecialization(String specialization) { this.specialization.set(specialization); }
    public StringProperty specializationProperty() { return specialization; }
    
    // Phone
    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public StringProperty phoneProperty() { return phone; }
    
    // Email
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }
    
    // Department
    public String getDepartment() { return department.get(); }
    public void setDepartment(String department) { this.department.set(department); }
    public StringProperty departmentProperty() { return department; }
    
    // Created At
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    
    @Override
    public String toString() {
        return getFullName() + " (" + getSpecialization() + ")";
    }
}
