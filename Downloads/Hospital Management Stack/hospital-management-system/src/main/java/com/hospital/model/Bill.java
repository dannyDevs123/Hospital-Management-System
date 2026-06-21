package com.hospital.model;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bill model class for billing management.
 */
public class Bill {
    private final IntegerProperty id;
    private final StringProperty billNumber;
    private final StringProperty patientName;
    private final ObjectProperty<BigDecimal> consultationFee;
    private final ObjectProperty<BigDecimal> medicineFee;
    private final ObjectProperty<BigDecimal> serviceCharge;
    private final ObjectProperty<BigDecimal> taxAmount;
    private final ObjectProperty<BigDecimal> totalAmount;
    private final ObjectProperty<LocalDateTime> billDate;
    
    public static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax
    
    public Bill() {
        this.id = new SimpleIntegerProperty();
        this.billNumber = new SimpleStringProperty();
        this.patientName = new SimpleStringProperty();
        this.consultationFee = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.medicineFee = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.serviceCharge = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.taxAmount = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.totalAmount = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.billDate = new SimpleObjectProperty<>();
    }
    
    public Bill(String billNumber, String patientName, BigDecimal consultationFee,
                BigDecimal medicineFee, BigDecimal serviceCharge, BigDecimal taxAmount, 
                BigDecimal totalAmount) {
        this();
        setBillNumber(billNumber);
        setPatientName(patientName);
        setConsultationFee(consultationFee);
        setMedicineFee(medicineFee);
        setServiceCharge(serviceCharge);
        setTaxAmount(taxAmount);
        setTotalAmount(totalAmount);
    }
    
    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }
    
    // Bill Number
    public String getBillNumber() { return billNumber.get(); }
    public void setBillNumber(String billNumber) { this.billNumber.set(billNumber); }
    public StringProperty billNumberProperty() { return billNumber; }
    
    // Patient Name
    public String getPatientName() { return patientName.get(); }
    public void setPatientName(String patientName) { this.patientName.set(patientName); }
    public StringProperty patientNameProperty() { return patientName; }
    
    // Consultation Fee
    public BigDecimal getConsultationFee() { return consultationFee.get(); }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee.set(consultationFee); }
    public ObjectProperty<BigDecimal> consultationFeeProperty() { return consultationFee; }
    
    // Medicine Fee
    public BigDecimal getMedicineFee() { return medicineFee.get(); }
    public void setMedicineFee(BigDecimal medicineFee) { this.medicineFee.set(medicineFee); }
    public ObjectProperty<BigDecimal> medicineFeeProperty() { return medicineFee; }
    
    // Service Charge
    public BigDecimal getServiceCharge() { return serviceCharge.get(); }
    public void setServiceCharge(BigDecimal serviceCharge) { this.serviceCharge.set(serviceCharge); }
    public ObjectProperty<BigDecimal> serviceChargeProperty() { return serviceCharge; }
    
    // Tax Amount
    public BigDecimal getTaxAmount() { return taxAmount.get(); }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount.set(taxAmount); }
    public ObjectProperty<BigDecimal> taxAmountProperty() { return taxAmount; }
    
    // Total Amount
    public BigDecimal getTotalAmount() { return totalAmount.get(); }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount.set(totalAmount); }
    public ObjectProperty<BigDecimal> totalAmountProperty() { return totalAmount; }
    
    // Bill Date
    public LocalDateTime getBillDate() { return billDate.get(); }
    public void setBillDate(LocalDateTime billDate) { this.billDate.set(billDate); }
    public ObjectProperty<LocalDateTime> billDateProperty() { return billDate; }
    
    /**
     * Calculate total amount including tax
     */
    public void calculateTotal() {
        BigDecimal subtotal = getConsultationFee().add(getMedicineFee()).add(getServiceCharge());
        BigDecimal tax = subtotal.multiply(TAX_RATE);
        setTaxAmount(tax);
        setTotalAmount(subtotal.add(tax));
    }
    
    @Override
    public String toString() {
        return getBillNumber() + " - " + getPatientName();
    }
}
