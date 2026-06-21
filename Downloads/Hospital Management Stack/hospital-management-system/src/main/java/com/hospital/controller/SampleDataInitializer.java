package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes sample data on first launch.
 */
public class SampleDataInitializer {
    private static final Logger LOGGER = Logger.getLogger(SampleDataInitializer.class.getName());
    private static boolean initialized = false;
    
    /**
     * Initialize sample data if database is empty
     */
    public static synchronized void initialize() {
        if (initialized) return;
        
        try {
            UserDAO userDAO = new UserDAO();
            PatientDAO patientDAO = new PatientDAO();
            DoctorDAO doctorDAO = new DoctorDAO();
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            BillDAO billDAO = new BillDAO();
            
            // Check if data already exists
            if (userDAO.getCount() > 0) {
                LOGGER.info("Sample data already exists, skipping initialization");
                initialized = true;
                return;
            }
            
            LOGGER.info("Initializing sample data...");
            
            // Create admin user
            User admin = new User("admin", "admin123", "admin@medicare.com", "System Administrator");
            userDAO.insert(admin);
            LOGGER.info("Admin user created");
            
            // Create sample patients
            Patient[] patients = {
                new Patient("PT-001", "John Smith", "Male", 45, "+1-555-0101", "123 Main St, New York, NY", "O+", "Hypertension, Diabetes Type 2"),
                new Patient("PT-002", "Sarah Johnson", "Female", 32, "+1-555-0102", "456 Oak Ave, Los Angeles, CA", "A+", "Asthma"),
                new Patient("PT-003", "Michael Brown", "Male", 28, "+1-555-0103", "789 Pine Rd, Chicago, IL", "B+", "No significant history"),
                new Patient("PT-004", "Emily Davis", "Female", 55, "+1-555-0104", "321 Elm St, Houston, TX", "AB-", "Arthritis"),
                new Patient("PT-005", "Robert Wilson", "Male", 62, "+1-555-0105", "654 Maple Dr, Phoenix, AZ", "O-", "Heart Disease"),
                new Patient("PT-006", "Lisa Anderson", "Female", 38, "+1-555-0106", "987 Cedar Ln, Philadelphia, PA", "A-", "Migraine"),
                new Patient("PT-007", "David Martinez", "Male", 50, "+1-555-0107", "147 Birch St, San Antonio, TX", "B-", "High Cholesterol"),
                new Patient("PT-008", "Jennifer Taylor", "Female", 29, "+1-555-0108", "258 Spruce Ave, San Diego, CA", "O+", "No significant history"),
                new Patient("PT-009", "James Thomas", "Male", 41, "+1-555-0109", "369 Willow Rd, Dallas, TX", "A+", "Back Pain"),
                new Patient("PT-010", "Maria Garcia", "Female", 35, "+1-555-0110", "159 Aspen Ln, San Jose, CA", "AB+", "Anxiety Disorder")
            };
            
            for (Patient p : patients) {
                patientDAO.insert(p);
            }
            LOGGER.info("10 sample patients created");
            
            // Create sample doctors
            Doctor[] doctors = {
                new Doctor("DR-001", "Dr. William Harper", "Cardiology", "+1-555-0201", "w.harper@medicare.com", "Cardiology"),
                new Doctor("DR-002", "Dr. Amanda Chen", "Neurology", "+1-555-0202", "a.chen@medicare.com", "Neurology"),
                new Doctor("DR-003", "Dr. James Rodriguez", "Pediatrics", "+1-555-0203", "j.rodriguez@medicare.com", "Pediatrics"),
                new Doctor("DR-004", "Dr. Susan Lee", "Orthopedics", "+1-555-0204", "s.lee@medicare.com", "Orthopedics"),
                new Doctor("DR-005", "Dr. Michael Chang", "Radiology", "+1-555-0205", "m.chang@medicare.com", "Radiology"),
                new Doctor("DR-006", "Dr. Patricia White", "General Medicine", "+1-555-0206", "p.white@medicare.com", "General Medicine"),
                new Doctor("DR-007", "Dr. Robert Kim", "Surgery", "+1-555-0207", "r.kim@medicare.com", "Surgery"),
                new Doctor("DR-008", "Dr. Linda Scott", "Dermatology", "+1-555-0208", "l.scott@medicare.com", "Dermatology")
            };
            
            for (Doctor d : doctors) {
                doctorDAO.insert(d);
            }
            LOGGER.info("8 sample doctors created");
            
            // Create sample appointments
            Appointment[] appointments = {
                new Appointment("John Smith", "Dr. William Harper", LocalDate.now(), "09:00", "Scheduled"),
                new Appointment("Sarah Johnson", "Dr. Amanda Chen", LocalDate.now(), "10:30", "Scheduled"),
                new Appointment("Michael Brown", "Dr. James Rodriguez", LocalDate.now(), "14:00", "Completed"),
                new Appointment("Emily Davis", "Dr. Susan Lee", LocalDate.now().plusDays(1), "11:00", "Scheduled"),
                new Appointment("Robert Wilson", "Dr. William Harper", LocalDate.now().plusDays(2), "09:30", "Scheduled"),
                new Appointment("Lisa Anderson", "Dr. Amanda Chen", LocalDate.now().plusDays(3), "15:00", "Scheduled"),
                new Appointment("David Martinez", "Dr. Michael Chang", LocalDate.now().plusDays(5), "10:00", "Scheduled"),
                new Appointment("Jennifer Taylor", "Dr. Patricia White", LocalDate.now().plusDays(7), "13:30", "Scheduled"),
                new Appointment("James Thomas", "Dr. Susan Lee", LocalDate.now().minusDays(2), "11:00", "Completed"),
                new Appointment("Maria Garcia", "Dr. Patricia White", LocalDate.now().minusDays(3), "14:00", "Completed"),
                new Appointment("John Smith", "Dr. Robert Kim", LocalDate.now().minusDays(5), "08:00", "Cancelled"),
                new Appointment("Sarah Johnson", "Dr. Linda Scott", LocalDate.now().minusDays(1), "16:00", "No-Show")
            };
            
            for (Appointment a : appointments) {
                appointmentDAO.insert(a);
            }
            LOGGER.info("12 sample appointments created");
            
            // Create sample bills
            Bill[] bills = {
                createBill("BILL-2025-001", "John Smith", 150.00, 85.50, 45.00),
                createBill("BILL-2025-002", "Sarah Johnson", 200.00, 120.00, 60.00),
                createBill("BILL-2025-003", "Michael Brown", 100.00, 45.00, 30.00),
                createBill("BILL-2025-004", "Emily Davis", 175.00, 95.00, 50.00),
                createBill("BILL-2025-005", "Robert Wilson", 250.00, 150.00, 75.00),
                createBill("BILL-2025-006", "Lisa Anderson", 125.00, 65.00, 35.00),
                createBill("BILL-2025-007", "David Martinez", 180.00, 110.00, 55.00),
                createBill("BILL-2025-008", "Jennifer Taylor", 90.00, 35.00, 25.00)
            };
            
            for (Bill b : bills) {
                billDAO.insert(b);
            }
            LOGGER.info("8 sample bills created");
            
            initialized = true;
            LOGGER.info("Sample data initialization complete!");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing sample data", e);
        }
    }
    
    private static Bill createBill(String billNumber, String patientName, 
                                    double consultation, double medicine, double service) {
        BigDecimal consultationFee = BigDecimal.valueOf(consultation);
        BigDecimal medicineFee = BigDecimal.valueOf(medicine);
        BigDecimal serviceCharge = BigDecimal.valueOf(service);
        
        BigDecimal subtotal = consultationFee.add(medicineFee).add(serviceCharge);
        BigDecimal tax = subtotal.multiply(Bill.TAX_RATE);
        BigDecimal total = subtotal.add(tax);
        
        return new Bill(billNumber, patientName, consultationFee, medicineFee, serviceCharge, tax, total);
    }
}
