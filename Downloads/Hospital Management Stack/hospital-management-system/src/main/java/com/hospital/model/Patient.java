package com.hospital.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

/**
 * Patient model class for patient management.
 */
public class Patient {
    private final IntegerProperty id;
    private final StringProperty patientId;
    private final StringProperty fullName;
    private final StringProperty gender;
    private final IntegerProperty age;
    private final StringProperty phone;
    private final StringProperty address;
    private final StringProperty bloodGroup;
    private final StringProperty medicalHistory;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final ObjectProperty<LocalDateTime> updatedAt;
    
    public Patient() {
        this.id = new SimpleIntegerProperty();
        this.patientId = new SimpleStringProperty();
        this.fullName = new SimpleStringProperty();
        this.gender = new SimpleStringProperty();
        this.age = new SimpleIntegerProperty();
        this.phone = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.bloodGroup = new SimpleStringProperty();
        this.medicalHistory = new SimpleStringProperty();
        this.createdAt = new SimpleObjectProperty<>();
        this.updatedAt = new SimpleObjectProperty<>();
    }
    
    public Patient(String patientId, String fullName, String gender, int age, 
                   String phone, String address, String bloodGroup, String medicalHistory) {
        this();
        setPatientId(patientId);
        setFullName(fullName);
        setGender(gender);
        setAge(age);
        setPhone(phone);
        setAddress(address);
        setBloodGroup(bloodGroup);
        setMedicalHistory(medicalHistory);
    }
    
    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }
    
    // Patient ID
    public String getPatientId() { return patientId.get(); }
    public void setPatientId(String patientId) { this.patientId.set(patientId); }
    public StringProperty patientIdProperty() { return patientId; }
    
    // Full Name
    public String getFullName() { return fullName.get(); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }
    public StringProperty fullNameProperty() { return fullName; }
    
    // Gender
    public String getGender() { return gender.get(); }
    public void setGender(String gender) { this.gender.set(gender); }
    public StringProperty genderProperty() { return gender; }
    
    // Age
    public int getAge() { return age.get(); }
    public void setAge(int age) { this.age.set(age); }
    public IntegerProperty ageProperty() { return age; }
    
    // Phone
    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public StringProperty phoneProperty() { return phone; }
    
    // Address
    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }
    public StringProperty addressProperty() { return address; }
    
    // Blood Group
    public String getBloodGroup() { return bloodGroup.get(); }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup.set(bloodGroup); }
    public StringProperty bloodGroupProperty() { return bloodGroup; }
    
    // Medical History
    public String getMedicalHistory() { return medicalHistory.get(); }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory.set(medicalHistory); }
    public StringProperty medicalHistoryProperty() { return medicalHistory; }
    
    // Created At
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    
    // Updated At
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
    
    @Override
    public String toString() {
        return getFullName();
    }
}
