package com.hospital.dao;

import com.hospital.model.Bill;
import com.hospital.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Bill entity.
 */
public class BillDAO {
    private static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());
    private final DatabaseManager dbManager;
    
    public BillDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Get bill by ID
     */
    public Bill getById(int id) {
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting bill by ID", e);
        }
        return null;
    }
    
    /**
     * Get bill by bill number
     */
    public Bill getByBillNumber(String billNumber) {
        String sql = "SELECT * FROM bills WHERE bill_number = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, billNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting bill by bill number", e);
        }
        return null;
    }
    
    /**
     * Get all bills
     */
    public List<Bill> getAll() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all bills", e);
        }
        return bills;
    }
    
    /**
     * Search bills
     */
    public List<Bill> search(String searchTerm) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE patient_name LIKE ? OR bill_number LIKE ? ORDER BY bill_date DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapResultSetToBill(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching bills", e);
        }
        return bills;
    }
    
    /**
     * Insert new bill
     */
    public boolean insert(Bill bill) {
        String sql = "INSERT INTO bills (bill_number, patient_name, consultation_fee, medicine_fee, " +
                     "service_charge, tax_amount, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, bill.getBillNumber());
            stmt.setString(2, bill.getPatientName());
            stmt.setBigDecimal(3, bill.getConsultationFee());
            stmt.setBigDecimal(4, bill.getMedicineFee());
            stmt.setBigDecimal(5, bill.getServiceCharge());
            stmt.setBigDecimal(6, bill.getTaxAmount());
            stmt.setBigDecimal(7, bill.getTotalAmount());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bill.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting bill", e);
        }
        return false;
    }
    
    /**
     * Update bill
     */
    public boolean update(Bill bill) {
        String sql = "UPDATE bills SET patient_name = ?, consultation_fee = ?, medicine_fee = ?, " +
                     "service_charge = ?, tax_amount = ?, total_amount = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bill.getPatientName());
            stmt.setBigDecimal(2, bill.getConsultationFee());
            stmt.setBigDecimal(3, bill.getMedicineFee());
            stmt.setBigDecimal(4, bill.getServiceCharge());
            stmt.setBigDecimal(5, bill.getTaxAmount());
            stmt.setBigDecimal(6, bill.getTotalAmount());
            stmt.setInt(7, bill.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating bill", e);
        }
        return false;
    }
    
    /**
     * Delete bill
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting bill", e);
        }
        return false;
    }
    
    /**
     * Get total revenue
     */
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM bills";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total revenue", e);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get next bill number
     */
    public String getNextBillNumber() {
        String sql = "SELECT MAX(CAST(SUBSTRING(bill_number, 10) AS INT)) as max_id FROM bills";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int nextId = 1;
            if (rs.next() && rs.getObject("max_id") != null) {
                nextId = rs.getInt("max_id") + 1;
            }
            return String.format("BILL-%d-%03d", java.time.LocalDate.now().getYear(), nextId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next bill number", e);
        }
        return String.format("BILL-%d-001", java.time.LocalDate.now().getYear());
    }
    
    /**
     * Get monthly revenue for charts
     */
    public List<MonthlyRevenue> getMonthlyRevenue(int months) {
        List<MonthlyRevenue> revenues = new ArrayList<>();
        String sql = "SELECT FORMATDATETIME(bill_date, 'yyyy-MM') as month, COALESCE(SUM(total_amount), 0) as revenue " +
                     "FROM bills WHERE bill_date >= ? " +
                     "GROUP BY FORMATDATETIME(bill_date, 'yyyy-MM') " +
                     "ORDER BY month";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now().minusMonths(months)));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    revenues.add(new MonthlyRevenue(
                        rs.getString("month"), 
                        rs.getBigDecimal("revenue")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting monthly revenue", e);
        }
        return revenues;
    }
    
    /**
     * Map ResultSet to Bill object
     */
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setPatientName(rs.getString("patient_name"));
        bill.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        bill.setMedicineFee(rs.getBigDecimal("medicine_fee"));
        bill.setServiceCharge(rs.getBigDecimal("service_charge"));
        bill.setTaxAmount(rs.getBigDecimal("tax_amount"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        Timestamp billDate = rs.getTimestamp("bill_date");
        if (billDate != null) {
            bill.setBillDate(billDate.toLocalDateTime());
        }
        return bill;
    }
    
    /**
     * Inner class for monthly revenue data
     */
    public static class MonthlyRevenue {
        private final String month;
        private final BigDecimal revenue;
        
        public MonthlyRevenue(String month, BigDecimal revenue) {
            this.month = month;
            this.revenue = revenue;
        }
        
        public String getMonth() { return month; }
        public BigDecimal getRevenue() { return revenue; }
    }
}
