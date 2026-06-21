package com.hospital.controller;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;
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
import java.util.ResourceBundle;

/**
 * Controller for Doctor Management screen.
 */
public class DoctorController implements Initializable {
    
    @FXML private TextField doctorIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField specializationField;
    @FXML private ComboBox<String> departmentCombo;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField searchField;
    @FXML private Label formErrorLabel;
    @FXML private Label recordCountLabel;
    
    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor, String> colDoctorId;
    @FXML private TableColumn<Doctor, String> colName;
    @FXML private TableColumn<Doctor, String> colSpecialization;
    @FXML private TableColumn<Doctor, String> colDepartment;
    @FXML private TableColumn<Doctor, String> colPhone;
    @FXML private TableColumn<Doctor, String> colEmail;
    
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private Doctor selectedDoctor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupTable();
        setupKeyboardShortcuts();
        loadDoctors();
        generateNewId();
    }
    
    private void setupComboBoxes() {
        departmentCombo.setItems(FXCollections.observableArrayList(
                "Cardiology", "Neurology", "Pediatrics", "Orthopedics", 
                "Radiology", "Surgery", "General Medicine", "Dermatology",
                "Ophthalmology", "Gynecology", "ENT", "Psychiatry"
        ));
    }
    
    private void setupTable() {
        colDoctorId.setCellValueFactory(data -> data.getValue().doctorIdProperty());
        colName.setCellValueFactory(data -> data.getValue().fullNameProperty());
        colSpecialization.setCellValueFactory(data -> data.getValue().specializationProperty());
        colDepartment.setCellValueFactory(data -> data.getValue().departmentProperty());
        colPhone.setCellValueFactory(data -> data.getValue().phoneProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());
        
        doctorTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedDoctor = selected;
                populateForm(selected);
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> {
            doctorTable.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                this::handleClear
            );
            doctorTable.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                this::handleAdd
            );
        });
    }
    
    private void loadDoctors() {
        ObservableList<Doctor> doctors = FXCollections.observableArrayList(doctorDAO.getAll());
        doctorTable.setItems(doctors);
        recordCountLabel.setText("Total: " + doctors.size() + " records");
    }
    
    private void generateNewId() {
        doctorIdField.setText(doctorDAO.getNextDoctorId());
    }
    
    private void populateForm(Doctor doctor) {
        doctorIdField.setText(doctor.getDoctorId());
        fullNameField.setText(doctor.getFullName());
        specializationField.setText(doctor.getSpecialization());
        departmentCombo.setValue(doctor.getDepartment());
        phoneField.setText(doctor.getPhone());
        emailField.setText(doctor.getEmail());
    }
    
    private void clearForm() {
        generateNewId();
        fullNameField.clear();
        specializationField.clear();
        departmentCombo.setValue(null);
        phoneField.clear();
        emailField.clear();
        formErrorLabel.setVisible(false);
        formErrorLabel.setManaged(false);
        selectedDoctor = null;
        doctorTable.getSelectionModel().clearSelection();
    }
    
    private boolean validateForm() {
        if (fullNameField.getText().trim().isEmpty()) {
            showError("Full name is required");
            return false;
        }
        if (specializationField.getText().trim().isEmpty()) {
            showError("Specialization is required");
            return false;
        }
        if (departmentCombo.getValue() == null) {
            showError("Department is required");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showError("Phone number is required");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showError("Email is required");
            return false;
        }
        // Basic email validation
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showError("Please enter a valid email address");
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
    private void handleAdd() {
        if (!validateForm()) return;
        
        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorIdField.getText());
        doctor.setFullName(fullNameField.getText().trim());
        doctor.setSpecialization(specializationField.getText().trim());
        doctor.setDepartment(departmentCombo.getValue());
        doctor.setPhone(phoneField.getText().trim());
        doctor.setEmail(emailField.getText().trim());
        
        if (doctorDAO.insert(doctor)) {
            loadDoctors();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor added successfully");
        } else {
            showError("Failed to add doctor");
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedDoctor == null) {
            showError("Please select a doctor to update");
            return;
        }
        
        if (!validateForm()) return;
        
        selectedDoctor.setFullName(fullNameField.getText().trim());
        selectedDoctor.setSpecialization(specializationField.getText().trim());
        selectedDoctor.setDepartment(departmentCombo.getValue());
        selectedDoctor.setPhone(phoneField.getText().trim());
        selectedDoctor.setEmail(emailField.getText().trim());
        
        if (doctorDAO.update(selectedDoctor)) {
            loadDoctors();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor updated successfully");
        } else {
            showError("Failed to update doctor");
        }
    }
    
    @FXML
    private void handleDelete() {
        if (selectedDoctor == null) {
            showError("Please select a doctor to delete");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Doctor");
        alert.setContentText("Are you sure you want to delete Dr. " + selectedDoctor.getFullName() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (doctorDAO.delete(selectedDoctor.getId())) {
                    loadDoctors();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor deleted successfully");
                } else {
                    showError("Failed to delete doctor");
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
            loadDoctors();
        } else {
            ObservableList<Doctor> doctors = FXCollections.observableArrayList(
                    doctorDAO.searchByNameOrSpecialization(searchTerm)
            );
            doctorTable.setItems(doctors);
            recordCountLabel.setText("Found: " + doctors.size() + " records");
        }
    }
    
    @FXML
    private void exportToCSV() {
        CSVExporter.exportDoctors(doctorTable.getItems());
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
