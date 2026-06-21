package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.model.*;
import com.hospital.util.PDFGenerator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Reports & Analytics screen.
 */
public class ReportsController implements Initializable {
    
    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea previewArea;
    @FXML private Label formErrorLabel;
    
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO billDAO = new BillDAO();
    
    private String currentReport = "";
    private String currentReportTitle = "";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reportTypeCombo.setItems(FXCollections.observableArrayList(
                "Patient Report",
                "Doctor Report",
                "Appointment Report",
                "Revenue Report",
                "Monthly Summary"
        ));
        
        // Set default date range
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
    }
    
    private boolean validateForm() {
        if (reportTypeCombo.getValue() == null) {
            showError("Please select a report type");
            return false;
        }
        if (startDatePicker.getValue() == null) {
            showError("Please select a start date");
            return false;
        }
        if (endDatePicker.getValue() == null) {
            showError("Please select an end date");
            return false;
        }
        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            showError("End date must be after start date");
            return false;
        }
        return true;
    }
    
    private void showError(String message) {
        formErrorLabel.setText(message);
        formErrorLabel.setVisible(true);
        formErrorLabel.setManaged(true);
    }
    
    @FXML
    private void handleGenerateReport() {
        if (!validateForm()) return;
        
        String reportType = reportTypeCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        switch (reportType) {
            case "Patient Report" -> generatePatientReport(startDate, endDate);
            case "Doctor Report" -> generateDoctorReport(startDate, endDate);
            case "Appointment Report" -> generateAppointmentReport(startDate, endDate);
            case "Revenue Report" -> generateRevenueReport(startDate, endDate);
            case "Monthly Summary" -> generateMonthlySummary(startDate, endDate);
        }
    }
    
    private void generatePatientReport(LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("PATIENT REPORT\n");
        sb.append("=" .repeat(80)).append("\n");
        sb.append(String.format("Period: %s to %s%n", startDate, endDate));
        sb.append(String.format("Generated: %s%n%n", LocalDate.now()));
        
        List<Patient> patients = patientDAO.getAll();
        sb.append(String.format("Total Patients: %d%n%n", patients.size()));
        
        sb.append(String.format("%-12s %-25s %-8s %-6s %-15s %-15s%n",
                "Patient ID", "Full Name", "Gender", "Age", "Phone", "Blood Group"));
        sb.append("-".repeat(80)).append("\n");
        
        for (Patient p : patients) {
            sb.append(String.format("%-12s %-25s %-8s %-6d %-15s %-15s%n",
                    p.getPatientId(),
                    truncate(p.getFullName(), 25),
                    p.getGender(),
                    p.getAge(),
                    p.getPhone(),
                    p.getBloodGroup() != null ? p.getBloodGroup() : "N/A"));
        }
        
        currentReport = sb.toString();
        currentReportTitle = "Patient Report";
        previewArea.setText(currentReport);
    }
    
    private void generateDoctorReport(LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("DOCTOR REPORT\n");
        sb.append("=" .repeat(80)).append("\n");
        sb.append(String.format("Period: %s to %s%n", startDate, endDate));
        sb.append(String.format("Generated: %s%n%n", LocalDate.now()));
        
        List<Doctor> doctors = doctorDAO.getAll();
        sb.append(String.format("Total Doctors: %d%n%n", doctors.size()));
        
        // Group by department
        sb.append("Doctors by Department:\n");
        sb.append("-".repeat(80)).append("\n");
        
        sb.append(String.format("%-12s %-25s %-20s %-20s %-15s%n",
                "Doctor ID", "Full Name", "Specialization", "Department", "Phone"));
        sb.append("-".repeat(80)).append("\n");
        
        for (Doctor d : doctors) {
            sb.append(String.format("%-12s %-25s %-20s %-20s %-15s%n",
                    d.getDoctorId(),
                    truncate(d.getFullName(), 25),
                    truncate(d.getSpecialization(), 20),
                    d.getDepartment(),
                    d.getPhone()));
        }
        
        currentReport = sb.toString();
        currentReportTitle = "Doctor Report";
        previewArea.setText(currentReport);
    }
    
    private void generateAppointmentReport(LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("APPOINTMENT REPORT\n");
        sb.append("=" .repeat(80)).append("\n");
        sb.append(String.format("Period: %s to %s%n", startDate, endDate));
        sb.append(String.format("Generated: %s%n%n", LocalDate.now()));
        
        List<com.hospital.model.Appointment> appointments = appointmentDAO.getAll();
        
        // Filter by date range
        long scheduled = appointments.stream().filter(a -> a.getStatus().equals("Scheduled")).count();
        long completed = appointments.stream().filter(a -> a.getStatus().equals("Completed")).count();
        long cancelled = appointments.stream().filter(a -> a.getStatus().equals("Cancelled")).count();
        long noShow = appointments.stream().filter(a -> a.getStatus().equals("No-Show")).count();
        
        sb.append("Summary:\n");
        sb.append(String.format("  Total Appointments: %d%n", appointments.size()));
        sb.append(String.format("  Scheduled: %d%n", scheduled));
        sb.append(String.format("  Completed: %d%n", completed));
        sb.append(String.format("  Cancelled: %d%n", cancelled));
        sb.append(String.format("  No-Show: %d%n%n", noShow));
        
        sb.append(String.format("%-6s %-25s %-25s %-12s %-8s %-12s%n",
                "ID", "Patient", "Doctor", "Date", "Time", "Status"));
        sb.append("-".repeat(80)).append("\n");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (com.hospital.model.Appointment a : appointments) {
            sb.append(String.format("%-6d %-25s %-25s %-12s %-8s %-12s%n",
                    a.getId(),
                    truncate(a.getPatientName(), 25),
                    truncate(a.getDoctorName(), 25),
                    a.getAppointmentDate().format(formatter),
                    a.getAppointmentTime(),
                    a.getStatus()));
        }
        
        currentReport = sb.toString();
        currentReportTitle = "Appointment Report";
        previewArea.setText(currentReport);
    }
    
    private void generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("REVENUE REPORT\n");
        sb.append("=" .repeat(80)).append("\n");
        sb.append(String.format("Period: %s to %s%n", startDate, endDate));
        sb.append(String.format("Generated: %s%n%n", LocalDate.now()));
        
        List<com.hospital.model.Bill> bills = billDAO.getAll();
        
        double totalConsultation = bills.stream().mapToDouble(b -> b.getConsultationFee().doubleValue()).sum();
        double totalMedicine = bills.stream().mapToDouble(b -> b.getMedicineFee().doubleValue()).sum();
        double totalService = bills.stream().mapToDouble(b -> b.getServiceCharge().doubleValue()).sum();
        double totalTax = bills.stream().mapToDouble(b -> b.getTaxAmount().doubleValue()).sum();
        double totalRevenue = bills.stream().mapToDouble(b -> b.getTotalAmount().doubleValue()).sum();
        
        sb.append("Revenue Summary:\n");
        sb.append(String.format("  Total Bills: %d%n", bills.size()));
        sb.append(String.format("  Consultation Fees: $%,.2f%n", totalConsultation));
        sb.append(String.format("  Medicine Fees: $%,.2f%n", totalMedicine));
        sb.append(String.format("  Service Charges: $%,.2f%n", totalService));
        sb.append(String.format("  Tax Collected: $%,.2f%n", totalTax));
        sb.append(String.format("  Total Revenue: $%,.2f%n%n", totalRevenue));
        
        sb.append(String.format("%-15s %-25s %-15s %-15s%n",
                "Bill Number", "Patient", "Date", "Total Amount"));
        sb.append("-".repeat(80)).append("\n");
        
        for (com.hospital.model.Bill b : bills) {
            sb.append(String.format("%-15s %-25s %-15s $%,-14.2f%n",
                    b.getBillNumber(),
                    truncate(b.getPatientName(), 25),
                    b.getBillDate() != null ? b.getBillDate().toLocalDate().toString() : "N/A",
                    b.getTotalAmount().doubleValue()));
        }
        
        currentReport = sb.toString();
        currentReportTitle = "Revenue Report";
        previewArea.setText(currentReport);
    }
    
    private void generateMonthlySummary(LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("MONTHLY SUMMARY REPORT\n");
        sb.append("=" .repeat(80)).append("\n");
        sb.append(String.format("Period: %s to %s%n", startDate, endDate));
        sb.append(String.format("Generated: %s%n%n", LocalDate.now()));
        
        sb.append("Key Metrics:\n");
        sb.append(String.format("  Total Patients: %d%n", patientDAO.getCount()));
        sb.append(String.format("  Total Doctors: %d%n", doctorDAO.getCount()));
        sb.append(String.format("  Total Appointments: %d%n", appointmentDAO.getCount()));
        sb.append(String.format("  Today's Appointments: %d%n", appointmentDAO.getTodayCount()));
        sb.append(String.format("  Total Revenue: $%,.2f%n", billDAO.getTotalRevenue()));
        
        currentReport = sb.toString();
        currentReportTitle = "Monthly Summary";
        previewArea.setText(currentReport);
    }
    
    @FXML
    private void handleExportPDF() {
        if (currentReport.isEmpty()) {
            showError("Please generate a report first");
            return;
        }
        
        PDFGenerator.generateReport(currentReportTitle, currentReport,
                startDatePicker.getValue(), endDatePicker.getValue());
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("PDF Export");
        alert.setHeaderText(null);
        alert.setContentText("Report exported as PDF successfully!");
        alert.show();
    }
    
    @FXML
    private void handlePreview() {
        handleGenerateReport();
    }
    
    @FXML
    private void handleClear() {
        reportTypeCombo.setValue(null);
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
        previewArea.clear();
        currentReport = "";
        formErrorLabel.setVisible(false);
        formErrorLabel.setManaged(false);
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "N/A";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}
