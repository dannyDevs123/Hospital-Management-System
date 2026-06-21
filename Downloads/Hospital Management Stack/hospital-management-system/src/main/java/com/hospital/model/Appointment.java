package com.hospital.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Appointment model class for appointment management.
 */
public class Appointment {
    private final IntegerProperty id;
    private final StringProperty patientName;
    private final StringProperty doctorName;
    private final ObjectProperty<LocalDate> appointmentDate;
    private final StringProperty appointmentTime;
    private final StringProperty status;
    private final ObjectProperty<LocalDateTime> createdAt;
    
    // Status constants
    public static final String STATUS_SCHEDULED = "Scheduled";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";
    public static final String STATUS_NO_SHOW = "No-Show";
    
    public Appointment() {
        this.id = new SimpleIntegerProperty();
        this.patientName = new SimpleStringProperty();
        this.doctorName = new SimpleStringProperty();
        this.appointmentDate = new SimpleObjectProperty<>();
        this.appointmentTime = new SimpleStringProperty();
        this.status = new SimpleStringProperty(STATUS_SCHEDULED);
        this.createdAt = new SimpleObjectProperty<>();
    }
    
    public Appointment(String patientName, String doctorName, LocalDate appointmentDate, 
                       String appointmentTime, String status) {
        this();
        setPatientName(patientName);
        setDoctorName(doctorName);
        setAppointmentDate(appointmentDate);
        setAppointmentTime(appointmentTime);
        setStatus(status);
    }
    
    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }
    
    // Patient Name
    public String getPatientName() { return patientName.get(); }
    public void setPatientName(String patientName) { this.patientName.set(patientName); }
    public StringProperty patientNameProperty() { return patientName; }
    
    // Doctor Name
    public String getDoctorName() { return doctorName.get(); }
    public void setDoctorName(String doctorName) { this.doctorName.set(doctorName); }
    public StringProperty doctorNameProperty() { return doctorName; }
    
    // Appointment Date
    public LocalDate getAppointmentDate() { return appointmentDate.get(); }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate.set(appointmentDate); }
    public ObjectProperty<LocalDate> appointmentDateProperty() { return appointmentDate; }
    
    // Appointment Time
    public String getAppointmentTime() { return appointmentTime.get(); }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime.set(appointmentTime); }
    public StringProperty appointmentTimeProperty() { return appointmentTime; }
    
    // Status
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }
    
    // Created At
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    
    /**
     * Get CSS style class based on status
     */
    public String getStatusStyleClass() {
        return switch (getStatus()) {
            case STATUS_SCHEDULED -> "status-scheduled";
            case STATUS_COMPLETED -> "status-completed";
            case STATUS_CANCELLED -> "status-cancelled";
            case STATUS_NO_SHOW -> "status-no-show";
            default -> "";
        };
    }
    
    @Override
    public String toString() {
        return getPatientName() + " - " + getDoctorName() + " on " + getAppointmentDate();
    }
}
