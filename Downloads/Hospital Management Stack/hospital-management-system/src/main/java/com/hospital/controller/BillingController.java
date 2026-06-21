package com.hospital.controller;

import com.hospital.dao.BillDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Bill;
import com.hospital.model.Patient;
import com.hospital.util.CSVExporter;
import com.hospital.util.PDFGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Billing & Invoicing screen.
 */
public class BillingController implements Initializable {
    
    @FXML private TextField billNumberField;
    @FXML private ComboBox<String> patientCombo;
    @FXML private TextField consultationFeeField;
    @FXML private TextField medicineFeeField;
    @FXML private TextField serviceChargeField;
    @FXML private TextField taxField;
    @FXML private TextField totalField;
    @FXML private Label formErrorLabel;
    @FXML private Label recordCountLabel;
    
    @FXML private TableView<Bill> billTable;
    @FXML private TableColumn<Bill, String> colBillNumber;
    @FXML private TableColumn<Bill, String> colBillPatient;
    @FXML private TableColumn<Bill, String> colConsultation;
    @FXML private TableColumn<Bill, String> colMedicine;
    @FXML private TableColumn<Bill, String> colService;
    @FXML private TableColumn<Bill, String> colTax;
    @FXML private TableColumn<Bill, String> colTotal;
    @FXML private TableColumn<Bill, String> colBillDate;
    
    private final BillDAO billDAO = new BillDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private Bill selectedBill;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadComboData();
        loadBills();
        generateNewBillNumber();
    }
    
    private void setupTable() {
        colBillNumber.setCellValueFactory(data -> data.getValue().billNumberProperty());
        colBillPatient.setCellValueFactory(data -> data.getValue().patientNameProperty());
        colConsultation.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", data.getValue().getConsultationFee())));
        colMedicine.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", data.getValue().getMedicineFee())));
        colService.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", data.getValue().getServiceCharge())));
        colTax.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", data.getValue().getTaxAmount())));
        colTotal.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", data.getValue().getTotalAmount())));
        colBillDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getBillDate() != null ? data.getValue().getBillDate().toLocalDate().toString() : ""));
        
        billTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedBill = selected;
                populateForm(selected);
            }
        });
    }
    
    private void loadComboData() {
        ObservableList<String> patientNames = FXCollections.observableArrayList();
        for (Patient p : patientDAO.getAll()) {
            patientNames.add(p.getFullName());
        }
        patientCombo.setItems(patientNames);
    }
    
    private void loadBills() {
        ObservableList<Bill> bills = FXCollections.observableArrayList(billDAO.getAll());
        billTable.setItems(bills);
        recordCountLabel.setText("Total: " + bills.size() + " records");
    }
    
    private void generateNewBillNumber() {
        billNumberField.setText(billDAO.getNextBillNumber());
    }
    
    private void populateForm(Bill bill) {
        billNumberField.setText(bill.getBillNumber());
        patientCombo.setValue(bill.getPatientName());
        consultationFeeField.setText(bill.getConsultationFee().toString());
        medicineFeeField.setText(bill.getMedicineFee().toString());
        serviceChargeField.setText(bill.getServiceCharge().toString());
        taxField.setText(bill.getTaxAmount().toString());
        totalField.setText(bill.getTotalAmount().toString());
    }
    
    private void clearForm() {
        generateNewBillNumber();
        patientCombo.setValue(null);
        consultationFeeField.setText("0");
        medicineFeeField.setText("0");
        serviceChargeField.setText("0");
        taxField.setText("0.00");
        totalField.setText("0.00");
        formErrorLabel.setVisible(false);
        formErrorLabel.setManaged(false);
        selectedBill = null;
        billTable.getSelectionModel().clearSelection();
    }
    
    private BigDecimal parseFee(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private boolean validateForm() {
        if (patientCombo.getValue() == null) {
            showError("Please select a patient");
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
    private void handleCalculate() {
        BigDecimal consultation = parseFee(consultationFeeField.getText());
        BigDecimal medicine = parseFee(medicineFeeField.getText());
        BigDecimal service = parseFee(serviceChargeField.getText());
        
        BigDecimal subtotal = consultation.add(medicine).add(service);
        BigDecimal tax = subtotal.multiply(Bill.TAX_RATE);
        BigDecimal total = subtotal.add(tax);
        
        consultationFeeField.setText(consultation.toString());
        medicineFeeField.setText(medicine.toString());
        serviceChargeField.setText(service.toString());
        taxField.setText(String.format("%.2f", tax));
        totalField.setText(String.format("%.2f", total));
    }
    
    @FXML
    private void handleGenerate() {
        if (!validateForm()) return;
        
        handleCalculate();
        
        Bill bill = new Bill();
        bill.setBillNumber(billNumberField.getText());
        bill.setPatientName(patientCombo.getValue());
        bill.setConsultationFee(parseFee(consultationFeeField.getText()));
        bill.setMedicineFee(parseFee(medicineFeeField.getText()));
        bill.setServiceCharge(parseFee(serviceChargeField.getText()));
        bill.setTaxAmount(parseFee(taxField.getText()));
        bill.setTotalAmount(parseFee(totalField.getText()));
        
        if (billDAO.insert(bill)) {
            loadBills();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Bill generated successfully");
            generateNewBillNumber();
        } else {
            showError("Failed to generate bill");
        }
    }
    
    @FXML
    private void handlePrint() {
        if (totalField.getText().equals("0.00") || totalField.getText().isEmpty()) {
            showError("Please calculate the bill first");
            return;
        }
        
        Bill bill = new Bill();
        bill.setBillNumber(billNumberField.getText());
        bill.setPatientName(patientCombo.getValue() != null ? patientCombo.getValue() : "N/A");
        bill.setConsultationFee(parseFee(consultationFeeField.getText()));
        bill.setMedicineFee(parseFee(medicineFeeField.getText()));
        bill.setServiceCharge(parseFee(serviceChargeField.getText()));
        bill.setTaxAmount(parseFee(taxField.getText()));
        bill.setTotalAmount(parseFee(totalField.getText()));
        
        PDFGenerator.generateBillReceipt(bill);
        showAlert(Alert.AlertType.INFORMATION, "Print", "Bill sent to printer/PDF");
    }
    
    @FXML
    private void handleClear() {
        clearForm();
    }
    
    @FXML
    private void exportToCSV() {
        CSVExporter.exportBills(billTable.getItems());
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
