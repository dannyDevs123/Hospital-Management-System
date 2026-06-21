package com.hospital.util;

import com.hospital.model.*;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for exporting data to CSV format.
 */
public class CSVExporter {
    private static final Logger LOGGER = Logger.getLogger(CSVExporter.class.getName());
    
    /**
     * Export patients to CSV
     */
    public static void exportPatients(ObservableList<Patient> patients) {
        String[] headers = {"Patient ID", "Full Name", "Gender", "Age", "Phone", "Address", "Blood Group", "Medical History"};
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append("\n");
        
        for (Patient p : patients) {
            sb.append(escapeCSV(p.getPatientId())).append(",")
              .append(escapeCSV(p.getFullName())).append(",")
              .append(escapeCSV(p.getGender())).append(",")
              .append(p.getAge()).append(",")
              .append(escapeCSV(p.getPhone())).append(",")
              .append(escapeCSV(p.getAddress())).append(",")
              .append(escapeCSV(p.getBloodGroup())).append(",")
              .append(escapeCSV(p.getMedicalHistory())).append("\n");
        }
        
        saveToFile(sb.toString(), "patients_export_" + getTimestamp() + ".csv");
    }
    
    /**
     * Export doctors to CSV
     */
    public static void exportDoctors(ObservableList<Doctor> doctors) {
        String[] headers = {"Doctor ID", "Full Name", "Specialization", "Department", "Phone", "Email"};
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append("\n");
        
        for (Doctor d : doctors) {
            sb.append(escapeCSV(d.getDoctorId())).append(",")
              .append(escapeCSV(d.getFullName())).append(",")
              .append(escapeCSV(d.getSpecialization())).append(",")
              .append(escapeCSV(d.getDepartment())).append(",")
              .append(escapeCSV(d.getPhone())).append(",")
              .append(escapeCSV(d.getEmail())).append("\n");
        }
        
        saveToFile(sb.toString(), "doctors_export_" + getTimestamp() + ".csv");
    }
    
    /**
     * Export appointments to CSV
     */
    public static void exportAppointments(ObservableList<Appointment> appointments) {
        String[] headers = {"ID", "Patient", "Doctor", "Date", "Time", "Status"};
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append("\n");
        
        for (Appointment a : appointments) {
            sb.append(a.getId()).append(",")
              .append(escapeCSV(a.getPatientName())).append(",")
              .append(escapeCSV(a.getDoctorName())).append(",")
              .append(a.getAppointmentDate()).append(",")
              .append(escapeCSV(a.getAppointmentTime())).append(",")
              .append(escapeCSV(a.getStatus())).append("\n");
        }
        
        saveToFile(sb.toString(), "appointments_export_" + getTimestamp() + ".csv");
    }
    
    /**
     * Export bills to CSV
     */
    public static void exportBills(ObservableList<Bill> bills) {
        String[] headers = {"Bill Number", "Patient", "Consultation", "Medicine", "Service", "Tax", "Total", "Date"};
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append("\n");
        
        for (Bill b : bills) {
            sb.append(escapeCSV(b.getBillNumber())).append(",")
              .append(escapeCSV(b.getPatientName())).append(",")
              .append(b.getConsultationFee()).append(",")
              .append(b.getMedicineFee()).append(",")
              .append(b.getServiceCharge()).append(",")
              .append(b.getTaxAmount()).append(",")
              .append(b.getTotalAmount()).append(",")
              .append(b.getBillDate() != null ? b.getBillDate().toLocalDate().toString() : "").append("\n");
        }
        
        saveToFile(sb.toString(), "bills_export_" + getTimestamp() + ".csv");
    }
    
    /**
     * Escape CSV values
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            escaped = "\"" + escaped + "\"";
        }
        return escaped;
    }
    
    /**
     * Get timestamp string
     */
    private static String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
    
    /**
     * Save content to file
     */
    private static void saveToFile(String content, String defaultFilename) {
        try {
            String userHome = System.getProperty("user.home");
            File exportFile = new File(userHome + File.separator + "Downloads" + File.separator + defaultFilename);
            File downloadsDir = exportFile.getParentFile();
            if (downloadsDir != null && !downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(exportFile))) {
                writer.print(content);
            }
            
            LOGGER.info("CSV exported to: " + exportFile.getAbsolutePath());
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Export Complete");
            alert.setHeaderText(null);
            alert.setContentText("File saved to:\n" + exportFile.getAbsolutePath());
            alert.show();
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error exporting CSV", e);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export CSV: " + e.getMessage());
            alert.show();
        }
    }
}
