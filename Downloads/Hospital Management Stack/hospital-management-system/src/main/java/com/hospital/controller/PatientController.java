package com.hospital.controller;

import com.hospital.dao.PatientDAO;
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
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for Patient Management screen.
 */
public class PatientController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(PatientController.class.getName());
    
    @FXML private TextField patientIdField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField ageField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private ComboBox<String> bloodGroupCombo;
    @FXML private TextArea medicalHistoryField;
    @FXML private TextField searchField;
    @FXML private Label formErrorLabel;
    @FXML private Label recordCountLabel;
    
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> colPatientId;
    @FXML private TableColumn<Patient, String> colName;
    @FXML private TableColumn<Patient, String> colGender;
    @FXML private TableColumn<Patient, Integer> colAge;
    @FXML private TableColumn<Patient, String> colPhone;
    @FXML private TableColumn<Patient, String> colAddress;
    @FXML private TableColumn<Patient, String> colBlood;
    
    private final PatientDAO patientDAO = new PatientDAO();
    private Patient selectedPatient;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupTable();
        setupKeyboardShortcuts();
        loadPatients();
        generateNewId();
    }
    
    private void setupComboBoxes() {
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        bloodGroupCombo.setItems(FXCollections.observableArrayList(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));
    }
    
    private void setupTable() {
        colPatientId.setCellValueFactory(data -> data.getValue().patientIdProperty());
        colName.setCellValueFactory(data -> data.getValue().fullNameProperty());
        colGender.setCellValueFactory(data -> data.getValue().genderProperty());
        colAge.setCellValueFactory(data -> data.getValue().ageProperty().asObject());
        colPhone.setCellValueFactory(data -> data.getValue().phoneProperty());
        colAddress.setCellValueFactory(data -> data.getValue().addressProperty());
        colBlood.setCellValueFactory(data -> data.getValue().bloodGroupProperty());
        
        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedPatient = selected;
                populateForm(selected);
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> {
            patientTable.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                this::handleClear
            );
            patientTable.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                this::handleAdd
            );
        });
    }
    
    private void loadPatients() {
        ObservableList<Patient> patients = FXCollections.observableArrayList(patientDAO.getAll());
        patientTable.setItems(patients);
        recordCountLabel.setText("Total: " + patients.size() + " records");
    }
    
    private void generateNewId() {
        patientIdField.setText(patientDAO.getNextPatientId());
    }
    
    private void populateForm(Patient patient) {
        patientIdField.setText(patient.getPatientId());
        fullNameField.setText(patient.getFullName());
        genderCombo.setValue(patient.getGender());
        ageField.setText(String.valueOf(patient.getAge()));
        phoneField.setText(patient.getPhone());
        addressField.setText(patient.getAddress());
        bloodGroupCombo.setValue(patient.getBloodGroup());
        medicalHistoryField.setText(patient.getMedicalHistory());
    }
    
    private void clearForm() {
        generateNewId();
        fullNameField.clear();
        genderCombo.setValue(null);
        ageField.clear();
        phoneField.clear();
        addressField.clear();
        bloodGroupCombo.setValue(null);
        medicalHistoryField.clear();
        formErrorLabel.setVisible(false);
        formErrorLabel.setManaged(false);
        selectedPatient = null;
        patientTable.getSelectionModel().clearSelection();
    }
    
    private boolean validateForm() {
        if (fullNameField.getText().trim().isEmpty()) {
            showError("Full name is required");
            return false;
        }
        if (genderCombo.getValue() == null) {
            showError("Gender is required");
            return false;
        }
        if (ageField.getText().trim().isEmpty()) {
            showError("Age is required");
            return false;
        }
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            if (age < 0 || age > 150) {
                showError("Age must be between 0 and 150");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Age must be a number");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showError("Phone number is required");
            return false;
        }
        if (addressField.getText().trim().isEmpty()) {
            showError("Address is required");
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
        
        Patient patient = new Patient();
        patient.setPatientId(patientIdField.getText());
        patient.setFullName(fullNameField.getText().trim());
        patient.setGender(genderCombo.getValue());
        patient.setAge(Integer.parseInt(ageField.getText().trim()));
        patient.setPhone(phoneField.getText().trim());
        patient.setAddress(addressField.getText().trim());
        patient.setBloodGroup(bloodGroupCombo.getValue());
        patient.setMedicalHistory(medicalHistoryField.getText());
        
        if (patientDAO.insert(patient)) {
            loadPatients();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Patient added successfully");
        } else {
            showError("Failed to add patient");
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedPatient == null) {
            showError("Please select a patient to update");
            return;
        }
        
        if (!validateForm()) return;
        
        selectedPatient.setFullName(fullNameField.getText().trim());
        selectedPatient.setGender(genderCombo.getValue());
        selectedPatient.setAge(Integer.parseInt(ageField.getText().trim()));
        selectedPatient.setPhone(phoneField.getText().trim());
        selectedPatient.setAddress(addressField.getText().trim());
        selectedPatient.setBloodGroup(bloodGroupCombo.getValue());
        selectedPatient.setMedicalHistory(medicalHistoryField.getText());
        
        if (patientDAO.update(selectedPatient)) {
            loadPatients();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Patient updated successfully");
        } else {
            showError("Failed to update patient");
        }
    }
    
    @FXML
    private void handleDelete() {
        if (selectedPatient == null) {
            showError("Please select a patient to delete");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Patient");
        alert.setContentText("Are you sure you want to delete " + selectedPatient.getFullName() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (patientDAO.delete(selectedPatient.getId())) {
                    loadPatients();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Patient deleted successfully");
                } else {
                    showError("Failed to delete patient");
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
            loadPatients();
        } else {
            ObservableList<Patient> patients = FXCollections.observableArrayList(
                    patientDAO.searchByName(searchTerm)
            );
            patientTable.setItems(patients);
            recordCountLabel.setText("Found: " + patients.size() + " records");
        }
    }
    
    @FXML
    private void exportToCSV() {
        CSVExporter.exportPatients(patientTable.getItems());
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
