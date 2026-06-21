package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Patient entity.
 */
public class PatientDAO {
    private static final Logger LOGGER = Logger.getLogger(PatientDAO.class.getName());
    private final DatabaseManager dbManager;
    
    public PatientDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Get patient by ID
     */
    public Patient getById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting patient by ID", e);
        }
        return null;
    }
    
    /**
     * Get patient by patient ID
     */
    public Patient getByPatientId(String patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting patient by patient ID", e);
        }
        return null;
    }
    
    /**
     * Get all patients
     */
    public List<Patient> getAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all patients", e);
        }
        return patients;
    }
    
    /**
     * Search patients by name
     */
    public List<Patient> searchByName(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE full_name LIKE ? OR patient_id LIKE ? ORDER BY full_name";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching patients", e);
        }
        return patients;
    }
    
    /**
     * Insert new patient
     */
    public boolean insert(Patient patient) {
        String sql = "INSERT INTO patients (patient_id, full_name, gender, age, phone, address, blood_group, medical_history) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, patient.getPatientId());
            stmt.setString(2, patient.getFullName());
            stmt.setString(3, patient.getGender());
            stmt.setInt(4, patient.getAge());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getAddress());
            stmt.setString(7, patient.getBloodGroup());
            stmt.setString(8, patient.getMedicalHistory());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        patient.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting patient", e);
        }
        return false;
    }
    
    /**
     * Update patient
     */
    public boolean update(Patient patient) {
        String sql = "UPDATE patients SET full_name = ?, gender = ?, age = ?, phone = ?, address = ?, " +
                     "blood_group = ?, medical_history = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFullName());
            stmt.setString(2, patient.getGender());
            stmt.setInt(3, patient.getAge());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getAddress());
            stmt.setString(6, patient.getBloodGroup());
            stmt.setString(7, patient.getMedicalHistory());
            stmt.setInt(8, patient.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating patient", e);
        }
        return false;
    }
    
    /**
     * Delete patient
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting patient", e);
        }
        return false;
    }
    
    /**
     * Get total patient count
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting patient count", e);
        }
        return 0;
    }
    
    /**
     * Get next patient ID
     */
    public String getNextPatientId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(patient_id, 4) AS INT)) as max_id FROM patients";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int nextId = 1;
            if (rs.next() && rs.getObject("max_id") != null) {
                nextId = rs.getInt("max_id") + 1;
            }
            return String.format("PT-%03d", nextId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next patient ID", e);
        }
        return "PT-001";
    }
    
    /**
     * Map ResultSet to Patient object
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setPatientId(rs.getString("patient_id"));
        patient.setFullName(rs.getString("full_name"));
        patient.setGender(rs.getString("gender"));
        patient.setAge(rs.getInt("age"));
        patient.setPhone(rs.getString("phone"));
        patient.setAddress(rs.getString("address"));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setMedicalHistory(rs.getString("medical_history"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            patient.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            patient.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return patient;
    }
}
