package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Doctor entity.
 */
public class DoctorDAO {
    private static final Logger LOGGER = Logger.getLogger(DoctorDAO.class.getName());
    private final DatabaseManager dbManager;
    
    public DoctorDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Get doctor by ID
     */
    public Doctor getById(int id) {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting doctor by ID", e);
        }
        return null;
    }
    
    /**
     * Get doctor by doctor ID
     */
    public Doctor getByDoctorId(String doctorId) {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting doctor by doctor ID", e);
        }
        return null;
    }
    
    /**
     * Get all doctors
     */
    public List<Doctor> getAll() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all doctors", e);
        }
        return doctors;
    }
    
    /**
     * Search doctors by name or specialization
     */
    public List<Doctor> searchByNameOrSpecialization(String searchTerm) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE full_name LIKE ? OR specialization LIKE ? OR doctor_id LIKE ? ORDER BY full_name";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching doctors", e);
        }
        return doctors;
    }
    
    /**
     * Get doctors by department
     */
    public List<Doctor> getByDepartment(String department) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE department = ? ORDER BY full_name";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, department);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting doctors by department", e);
        }
        return doctors;
    }
    
    /**
     * Insert new doctor
     */
    public boolean insert(Doctor doctor) {
        String sql = "INSERT INTO doctors (doctor_id, full_name, specialization, phone, email, department) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, doctor.getDoctorId());
            stmt.setString(2, doctor.getFullName());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getPhone());
            stmt.setString(5, doctor.getEmail());
            stmt.setString(6, doctor.getDepartment());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        doctor.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting doctor", e);
        }
        return false;
    }
    
    /**
     * Update doctor
     */
    public boolean update(Doctor doctor) {
        String sql = "UPDATE doctors SET full_name = ?, specialization = ?, phone = ?, email = ?, department = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctor.getFullName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getPhone());
            stmt.setString(4, doctor.getEmail());
            stmt.setString(5, doctor.getDepartment());
            stmt.setInt(6, doctor.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating doctor", e);
        }
        return false;
    }
    
    /**
     * Delete doctor
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting doctor", e);
        }
        return false;
    }
    
    /**
     * Get total doctor count
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting doctor count", e);
        }
        return 0;
    }
    
    /**
     * Get next doctor ID
     */
    public String getNextDoctorId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(doctor_id, 4) AS INT)) as max_id FROM doctors";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int nextId = 1;
            if (rs.next() && rs.getObject("max_id") != null) {
                nextId = rs.getInt("max_id") + 1;
            }
            return String.format("DR-%03d", nextId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next doctor ID", e);
        }
        return "DR-001";
    }
    
    /**
     * Get all unique departments
     */
    public List<String> getAllDepartments() {
        List<String> departments = new ArrayList<>();
        String sql = "SELECT DISTINCT department FROM doctors ORDER BY department";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting departments", e);
        }
        return departments;
    }
    
    /**
     * Map ResultSet to Doctor object
     */
    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setDoctorId(rs.getString("doctor_id"));
        doctor.setFullName(rs.getString("full_name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setPhone(rs.getString("phone"));
        doctor.setEmail(rs.getString("email"));
        doctor.setDepartment(rs.getString("department"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            doctor.setCreatedAt(createdAt.toLocalDateTime());
        }
        return doctor;
    }
}
