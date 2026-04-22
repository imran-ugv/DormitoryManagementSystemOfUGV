package com.dorm.model;

import com.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Final DAO Implementation.
 * Optimized to prevent double-booking and synced with MVC Controllers.
 */
public class DormitoryDAO {

    // --- 1. AUTHENTICATION ---
    public boolean checkLogin(String username, String password) {
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- 2. DYNAMIC DASHBOARD STATISTICS ---
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Room Counts
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rooms WHERE room_status = 'Not Booked' AND is_active = 'YES'");
            if (rs1.next()) stats.put("Available Rooms", rs1.getInt(1));
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rooms WHERE room_status = 'Booked'");
            if (rs2.next()) stats.put("Booked Rooms", rs2.getInt(1));

            // Student Counts
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM students WHERE status = 'Living'");
            if (rs3.next()) stats.put("Living Students", rs3.getInt(1));
            ResultSet rs4 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM students WHERE status = 'Leaved'");
            if (rs4.next()) stats.put("Leaved Students", rs4.getInt(1));

            // Employee Counts
            ResultSet rs5 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM employees WHERE status = 'Working'");
            if (rs5.next()) stats.put("Working Employees", rs5.getInt(1));
            ResultSet rs6 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM employees WHERE status = 'Leaved'");
            if (rs6.next()) stats.put("Not Working Employees", rs6.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    // --- 3. ROOM MANAGEMENT ---
    public boolean isRoomExists(String roomNumber) {
        String query = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, roomNumber);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public void saveRoom(Room room) {
        String query = "INSERT INTO rooms (room_number, is_active, room_status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getIsActive());
            ps.setString(3, room.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateRoom(String roomNo, String active) {
        String query = "UPDATE rooms SET is_active = ? WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, active);
            ps.setString(2, roomNo);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteRoom(String roomNo) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM rooms WHERE room_number = ?")) {
            ps.setString(1, roomNo);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM rooms")) {
            while (rs.next()) list.add(new Room(rs.getString(1), rs.getString(2), rs.getString(3)));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Room> searchRooms(String roomNo) {
        List<Room> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM rooms WHERE room_number LIKE ?")) {
            ps.setString(1, "%" + roomNo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Room(rs.getString(1), rs.getString(2), rs.getString(3)));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 4. STUDENT MANAGEMENT (Logic Updated for Duplication Fix) ---

    // Fetches only rooms that are both Active (YES) and NOT Booked.
    public List<String> getAvailableRooms() {
        List<String> rooms = new ArrayList<>();
        String query = "SELECT room_number FROM rooms WHERE is_active = 'YES' AND room_status = 'Not Booked'";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) rooms.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return rooms;
    }

    // Final validation before saving to prevent double-booking.
    public boolean isRoomStillAvailable(String roomNo) {
        String query = "SELECT room_status FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, roomNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("room_status").equals("Not Booked");
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean saveStudent(Student s) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction
            try {
                String q = "INSERT INTO students (name, father_name, mother_name, gender, dob, phone, email, university_id, degree_program, room_number, address, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(q);
                ps.setString(1, s.name); ps.setString(2, s.fatherName); ps.setString(3, s.motherName);
                ps.setString(4, s.gender); ps.setString(5, s.dob); ps.setString(6, s.phone);
                ps.setString(7, s.email); ps.setString(8, s.uniID); ps.setString(9, s.degree);
                ps.setString(10, s.roomNo); ps.setString(11, s.address); ps.setString(12, s.status);
                ps.executeUpdate();
                // Update Room status to Booked
                conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Booked' WHERE room_number = '" + s.roomNo + "'");
                conn.commit(); return true;
            } catch (SQLException ex) { conn.rollback(); ex.printStackTrace(); return false; }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Student getStudentByID(String uniID) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE university_id = ?")) {
            ps.setString(1, uniID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Student(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateStudentComplex(Student s, String oldRoom, String oldStatus) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String q = "UPDATE students SET name=?, father_name=?, mother_name=?, gender=?, dob=?, phone=?, email=?, degree_program=?, room_number=?, address=?, status=? WHERE university_id=?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, s.name); ps.setString(2, s.fatherName); ps.setString(3, s.motherName);
            ps.setString(4, s.gender); ps.setString(5, s.dob); ps.setString(6, s.phone);
            ps.setString(7, s.email); ps.setString(8, s.degree); ps.setString(9, s.roomNo);
            ps.setString(10, s.address); ps.setString(11, s.status); ps.setString(12, s.uniID);
            ps.executeUpdate();
            if (!s.roomNo.equals(oldRoom)) {
                conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Not Booked' WHERE room_number = '" + oldRoom + "'");
                conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Booked' WHERE room_number = '" + s.roomNo + "'");
            }
            if (s.status.equals("Leaved")) conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Not Booked' WHERE room_number = '" + s.roomNo + "'");
            else if (s.status.equals("Living")) conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Booked' WHERE room_number = '" + s.roomNo + "'");
            conn.commit(); return true;
        } catch (SQLException e) { return false; }
    }

    public boolean deleteStudent(String id, String room) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            conn.createStatement().executeUpdate("DELETE FROM students WHERE university_id = '" + id + "'");
            conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Not Booked' WHERE room_number = '" + room + "'");
            conn.commit(); return true;
        } catch (SQLException e) { return false; }
    }

    public List<Student> getAllLivingStudents() { return getStudentListByStatus("Living", null); }
    public List<Student> searchLivingStudents(String id) { return getStudentListByStatus("Living", id); }
    public List<Student> getAllLeavedStudents() { return getStudentListByStatus("Leaved", null); }
    public List<Student> searchLeavedStudents(String id) { return getStudentListByStatus("Leaved", id); }

    private List<Student> getStudentListByStatus(String status, String id) {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students WHERE status = '" + status + "'";
        if (id != null) query += " AND university_id LIKE '%" + id + "%'";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) list.add(new Student(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14)));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 5. FEES MANAGEMENT ---
    public double getMonthDue(String id, String month) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT SUM(amount_paid) FROM fees WHERE university_id = ? AND payment_month = ?")) {
            ps.setString(1, id); ps.setString(2, month);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? 2500 - rs.getDouble(1) : 2500;
        } catch (SQLException e) { return 2500; }
    }

    public boolean saveFee(Fee f) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO fees (university_id, payment_month, total_amount, amount_paid, due_amount) VALUES (?,?,?,?,?)")) {
            ps.setString(1, f.uniID); ps.setString(2, f.month); ps.setDouble(3, f.total); ps.setDouble(4, f.paid); ps.setDouble(5, f.due);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Fee> getPaymentHistory(String id) {
        List<Fee> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM fees WHERE university_id = ? ORDER BY payment_date DESC")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Fee(rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6), rs.getString(7)));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 6. EMPLOYEE MANAGEMENT ---
    public boolean saveEmployee(Employee e) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO employees (unique_id, name, father_name, mother_name, gender, dob, phone, email, designation, present_address, permanent_address, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)")) {
            ps.setString(1, e.employeeID); ps.setString(2, e.name); ps.setString(3, e.fatherName); ps.setString(4, e.motherName); ps.setString(5, e.gender); ps.setString(6, e.dob); ps.setString(7, e.phone); ps.setString(8, e.email); ps.setString(9, e.designation); ps.setString(10, e.presentAddr); ps.setString(11, e.permanentAddr); ps.setString(12, "Working");
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { return false; }
    }

    public Employee getEmployeeByID(String id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees WHERE unique_id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Employee(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateEmployee(Employee e) {
        String q = "UPDATE employees SET name=?, father_name=?, mother_name=?, gender=?, dob=?, phone=?, email=?, designation=?, present_address=?, permanent_address=?, status=? WHERE unique_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, e.name); ps.setString(2, e.fatherName); ps.setString(3, e.motherName); ps.setString(4, e.gender); ps.setString(5, e.dob); ps.setString(6, e.phone); ps.setString(7, e.email); ps.setString(8, e.designation); ps.setString(9, e.presentAddr); ps.setString(10, e.permanentAddr); ps.setString(11, e.status); ps.setString(12, e.employeeID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { return false; }
    }

    public boolean deleteEmployee(String id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE unique_id = ?")) {
            ps.setString(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException ex) { return false; }
    }

    public List<Employee> getAllWorkingEmployees() { return getEmployeeListByStatus("Working", null); }
    public List<Employee> searchWorkingEmployees(String id) { return getEmployeeListByStatus("Working", id); }
    public List<Employee> getAllLeavedEmployees() { return getEmployeeListByStatus("Leaved", null); }
    public List<Employee> searchLeavedEmployees(String id) { return getEmployeeListByStatus("Leaved", id); }

    private List<Employee> getEmployeeListByStatus(String status, String id) {
        List<Employee> list = new ArrayList<>();
        String query = "SELECT * FROM employees WHERE status = '" + status + "'";
        if (id != null) query += " AND unique_id LIKE '%" + id + "%'";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) list.add(new Employee(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13)));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 7. SALARY OPERATIONS ---
    public boolean saveSalary(String id, String month, double amount) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO salaries (unique_id, payment_month, amount) VALUES (?,?,?)")) {
            ps.setString(1, id); ps.setString(2, month); ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean isSalaryPaid(String id, String month) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM salaries WHERE unique_id = ? AND payment_month = ?")) {
            ps.setString(1, id); ps.setString(2, month);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public List<Object[]> getSalaryHistory(String id) {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT payment_month, amount, payment_date FROM salaries WHERE unique_id = ? ORDER BY payment_date DESC")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Object[]{rs.getString(1), rs.getDouble(2), rs.getString(3)});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}