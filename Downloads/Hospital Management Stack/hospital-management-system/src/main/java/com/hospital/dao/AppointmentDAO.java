package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Appointment entity.
 */
public class AppointmentDAO {
    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());
    private final DatabaseManager dbManager;
    
    public AppointmentDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Get appointment by ID
     */
    public Appointment getById(int id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting appointment by ID", e);
        }
        return null;
    }
    
    /**
     * Get all appointments
     */
    public List<Appointment> getAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all appointments", e);
        }
        return appointments;
    }
    
    /**
     * Get appointments by date
     */
    public List<Appointment> getByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date = ? ORDER BY appointment_time";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting appointments by date", e);
        }
        return appointments;
    }
    
    /**
     * Get today's appointments
     */
    public List<Appointment> getTodayAppointments() {
        return getByDate(LocalDate.now());
    }
    
    /**
     * Get upcoming appointments
     */
    public List<Appointment> getUpcomingAppointments(int limit) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date >= ? AND status = 'Scheduled' " +
                     "ORDER BY appointment_date, appointment_time LIMIT ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting upcoming appointments", e);
        }
        return appointments;
    }
    
    /**
     * Search appointments
     */
    public List<Appointment> search(String searchTerm) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_name LIKE ? OR doctor_name LIKE ? ORDER BY appointment_date DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching appointments", e);
        }
        return appointments;
    }
    
    /**
     * Insert new appointment
     */
    public boolean insert(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_name, doctor_name, appointment_date, appointment_time, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, appointment.getPatientName());
            stmt.setString(2, appointment.getDoctorName());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setString(4, appointment.getAppointmentTime());
            stmt.setString(5, appointment.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        appointment.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting appointment", e);
        }
        return false;
    }
    
    /**
     * Update appointment
     */
    public boolean update(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_name = ?, doctor_name = ?, appointment_date = ?, " +
                     "appointment_time = ?, status = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, appointment.getPatientName());
            stmt.setString(2, appointment.getDoctorName());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setString(4, appointment.getAppointmentTime());
            stmt.setString(5, appointment.getStatus());
            stmt.setInt(6, appointment.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment", e);
        }
        return false;
    }
    
    /**
     * Update appointment status
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment status", e);
        }
        return false;
    }
    
    /**
     * Delete appointment
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting appointment", e);
        }
        return false;
    }
    
    /**
     * Get appointment count
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting appointment count", e);
        }
        return 0;
    }
    
    /**
     * Get today's appointment count
     */
    public int getTodayCount() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting today appointment count", e);
        }
        return 0;
    }
    
    /**
     * Get monthly appointment counts for charts
     */
    public List<MonthlyCount> getMonthlyCounts(int months) {
        List<MonthlyCount> counts = new ArrayList<>();
        String sql = "SELECT FORMATDATETIME(appointment_date, 'yyyy-MM') as month, COUNT(*) as count " +
                     "FROM appointments WHERE appointment_date >= ? " +
                     "GROUP BY FORMATDATETIME(appointment_date, 'yyyy-MM') " +
                     "ORDER BY month";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now().minusMonths(months)));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    counts.add(new MonthlyCount(rs.getString("month"), rs.getInt("count")));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting monthly appointment counts", e);
        }
        return counts;
    }
    
    /**
     * Map ResultSet to Appointment object
     */
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("id"));
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDoctorName(rs.getString("doctor_name"));
        Date date = rs.getDate("appointment_date");
        if (date != null) {
            appointment.setAppointmentDate(date.toLocalDate());
        }
        appointment.setAppointmentTime(rs.getString("appointment_time"));
        appointment.setStatus(rs.getString("status"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            appointment.setCreatedAt(createdAt.toLocalDateTime());
        }
        return appointment;
    }
    
    /**
     * Inner class for monthly count data
     */
    public static class MonthlyCount {
        private final String month;
        private final int count;
        
        public MonthlyCount(String month, int count) {
            this.month = month;
            this.count = count;
        }
        
        public String getMonth() { return month; }
        public int getCount() { return count; }
    }
}
