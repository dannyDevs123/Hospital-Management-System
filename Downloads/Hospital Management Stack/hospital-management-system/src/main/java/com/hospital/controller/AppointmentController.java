package com.hospital.controller;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.util.CSVExporter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controller for Appointment Management screen.
 */
public class AppointmentController implements Initializable {
    
    @FXML private TextField idField;
    @FXML private ComboBox<String> patientCombo;
    @FXML private ComboBox<String> doctorCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField searchField;
    @FXML private Label formErrorLabel;
    @FXML private Label recordCountLabel;
    
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> colId;
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;
    
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private Appointment selectedAppointment;
    
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupTable();
        setupKeyboardShortcuts();
        loadAppointments();
        loadComboData();
    }
    
    private void setupComboBoxes() {
        statusCombo.setItems(FXCollections.observableArrayList(
                Appointment.STATUS_SCHEDULED,
                Appointment.STATUS_COMPLETED,
                Appointment.STATUS_CANCELLED,
                Appointment.STATUS_NO_SHOW
        ));
        statusCombo.setValue(Appointment.STATUS_SCHEDULED);
    }
    
    private void setupTable() {
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colPatient.setCellValueFactory(data -> data.getValue().patientNameProperty());
        colDoctor.setCellValueFactory(data -> data.getValue().doctorNameProperty());
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getAppointmentDate().toString()));
        colTime.setCellValueFactory(data -> data.getValue().appointmentTimeProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        
        // Style status column
        colStatus.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().clear();
                    switch (item) {
                        case "Scheduled" -> getStyleClass().add("status-scheduled");
                        case "Completed" -> getStyleClass().add("status-completed");
                        case "Cancelled" -> getStyleClass().add("status-cancelled");
                        case "No-Show" -> getStyleClass().add("status-no-show");
                    }
                }
            }
        });
        
        appointmentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedAppointment = selected;
                populateForm(selected);
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> {
            appointmentTable.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                this::handleClear
            );
            appointmentTable.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                this::handleBook
            );
        });
    }
    
    private void loadComboData() {
        // Load patients
        ObservableList<String> patientNames = FXCollections.observableArrayList();
        for (Patient p : patientDAO.getAll()) {
            patientNames.add(p.getFullName());
        }
        patientCombo.setItems(patientNames);
        
        // Load doctors
        ObservableList<String> doctorNames = FXCollections.observableArrayList();
        for (Doctor d : doctorDAO.getAll()) {
            doctorNames.add(d.getFullName() + " (" + d.getSpecialization() + ")");
        }
        doctorCombo.setItems(doctorNames);
    }
    
    private void loadAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList(appointmentDAO.getAll());
        appointmentTable.setItems(appointments);
        recordCountLabel.setText("Total: " + appointments.size() + " records");
    }
    
    private void populateForm(Appointment appointment) {
        idField.setText(String.valueOf(appointment.getId()));
        patientCombo.setValue(appointment.getPatientName());
        doctorCombo.setValue(appointment.getDoctorName());
        datePicker.setValue(appointment.getAppointmentDate());
        timeField.setText(appointment.getAppointmentTime());
        statusCombo.setValue(appointment.getStatus());
    }
    
    private void clearForm() {
        idField.setText("New");
        patientCombo.setValue(null);
        doctorCombo.setValue(null);
        datePicker.setValue(LocalDate.now());
        timeField.clear();
        statusCombo.setValue(Appointment.STATUS_SCHEDULED);
        formErrorLabel.setVisible(false);
        formErrorLabel.setManaged(false);
        selectedAppointment = null;
        appointmentTable.getSelectionModel().clearSelection();
    }
    
    private boolean validateForm() {
        if (patientCombo.getValue() == null) {
            showError("Please select a patient");
            return false;
        }
        if (doctorCombo.getValue() == null) {
            showError("Please select a doctor");
            return false;
        }
        if (datePicker.getValue() == null) {
            showError("Please select a date");
            return false;
        }
        if (datePicker.getValue().isBefore(LocalDate.now())) {
            showError("Appointment date cannot be in the past");
            return false;
        }
        if (timeField.getText().trim().isEmpty()) {
            showError("Please enter appointment time");
            return false;
        }
        if (!TIME_PATTERN.matcher(timeField.getText().trim()).matches()) {
            showError("Time must be in HH:mm format (e.g., 09:30)");
            return false;
        }
        if (statusCombo.getValue() == null) {
            showError("Please select a status");
            return false;
        }
        return true;
    }
    
    private void showError(String message) {
        formErrorLabel.setText(message);
        formErrorLabel.setVisible(true);
        formErrorLabel.setManaged(true);
    }
    
    private String extractDoctorName(String comboValue) {
        if (comboValue == null) return "";
        int idx = comboValue.indexOf(" (");
        return idx > 0 ? comboValue.substring(0, idx) : comboValue;
    }
    
    @FXML
    private void handleBook() {
        if (!validateForm()) return;
        
        Appointment appointment = new Appointment();
        appointment.setPatientName(patientCombo.getValue());
        appointment.setDoctorName(extractDoctorName(doctorCombo.getValue()));
        appointment.setAppointmentDate(datePicker.getValue());
        appointment.setAppointmentTime(timeField.getText().trim());
        appointment.setStatus(statusCombo.getValue());
        
        if (appointmentDAO.insert(appointment)) {
            loadAppointments();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment booked successfully");
        } else {
            showError("Failed to book appointment");
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedAppointment == null) {
            showError("Please select an appointment to update");
            return;
        }
        
        if (!validateForm()) return;
        
        selectedAppointment.setPatientName(patientCombo.getValue());
        selectedAppointment.setDoctorName(extractDoctorName(doctorCombo.getValue()));
        selectedAppointment.setAppointmentDate(datePicker.getValue());
        selectedAppointment.setAppointmentTime(timeField.getText().trim());
        selectedAppointment.setStatus(statusCombo.getValue());
        
        if (appointmentDAO.update(selectedAppointment)) {
            loadAppointments();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment updated successfully");
        } else {
            showError("Failed to update appointment");
        }
    }
    
    @FXML
    private void handleCancel() {
        if (selectedAppointment == null) {
            showError("Please select an appointment to cancel");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancellation");
        alert.setHeaderText("Cancel Appointment");
        alert.setContentText("Are you sure you want to cancel this appointment?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selectedAppointment.setStatus(Appointment.STATUS_CANCELLED);
                if (appointmentDAO.update(selectedAppointment)) {
                    loadAppointments();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment cancelled");
                } else {
                    showError("Failed to cancel appointment");
                }
            }
        });
    }
    
    @FXML
    private void handleClear() {
        clearForm();
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAppointments();
        } else {
            ObservableList<Appointment> appointments = FXCollections.observableArrayList(
                    appointmentDAO.search(searchTerm)
            );
            appointmentTable.setItems(appointments);
            recordCountLabel.setText("Found: " + appointments.size() + " records");
        }
    }
    
    @FXML
    private void exportToCSV() {
        CSVExporter.exportAppointments(appointmentTable.getItems());
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
