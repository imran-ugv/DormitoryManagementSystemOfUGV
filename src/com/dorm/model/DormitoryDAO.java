package com.dorm.model;

import com.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DormitoryDAO {

    // --- 1. ADMIN LOGIN LOGIC ---
    public boolean checkLogin(String username, String password) {
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 2. UPDATED DYNAMIC DASHBOARD STATISTICS (Real SQL Counts) ---
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Count Available Rooms
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rooms WHERE room_status = 'Not Booked' AND is_active = 'YES'");
            if (rs1.next()) stats.put("Available Rooms", rs1.getInt(1));

            // Count Booked Rooms
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rooms WHERE room_status = 'Booked'");
            if (rs2.next()) stats.put("Booked Rooms", rs2.getInt(1));

            // Count Living Students
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM students WHERE status = 'Living'");
            if (rs3.next()) stats.put("Living Students", rs3.getInt(1));

            // Count Leaved Students
            ResultSet rs4 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM students WHERE status = 'Leaved'");
            if (rs4.next()) stats.put("Leaved Students", rs4.getInt(1));

            // Count Working Employees
            ResultSet rs5 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM employees WHERE status = 'Working'");
            if (rs5.next()) stats.put("Working Employees", rs5.getInt(1));

            // Count Not Working Employees (Leaved)
            ResultSet rs6 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM employees WHERE status = 'Leaved'");
            if (rs6.next()) stats.put("Not Working Employees", rs6.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // --- 3. ROOM MANAGEMENT LOGIC ---

    public boolean isRoomExists(String roomNumber) {
        String query = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, roomNumber);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void saveRoom(Room room) {
        String query = "INSERT INTO rooms (room_number, is_active, room_status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getIsActive());
            ps.setString(3, room.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRoom(String roomNo, String active) {
        String query = "UPDATE rooms SET is_active = ? WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, active);
            ps.setString(2, roomNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRoom(String roomNo) {
        String query = "DELETE FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, roomNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Room(rs.getString("room_number"), rs.getString("is_active"), rs.getString("room_status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> searchRooms(String roomNo) {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM rooms WHERE room_number LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + roomNo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Room(rs.getString("room_number"), rs.getString("is_active"), rs.getString("room_status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- 4. NEW STUDENT REGISTRATION LOGIC ---

    public List<String> getAvailableRooms() {
        List<String> rooms = new ArrayList<>();
        String query = "SELECT room_number FROM rooms WHERE is_active = 'YES' AND room_status = 'Not Booked'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) rooms.add(rs.getString("room_number"));
        } catch (SQLException e) { e.printStackTrace(); }
        return rooms;
    }

    public boolean saveStudent(Student s) {
        String insertQuery = "INSERT INTO students (name, father_name, mother_name, gender, dob, phone, email, university_name, university_id, degree_program, room_number, address, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String updateQuery = "UPDATE rooms SET room_status = 'Booked' WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(insertQuery);
                 PreparedStatement ps2 = conn.prepareStatement(updateQuery)) {

                ps1.setString(1, s.name); ps1.setString(2, s.fatherName); ps1.setString(3, s.motherName);
                ps1.setString(4, s.gender); ps1.setString(5, s.dob); ps1.setString(6, s.phone);
                ps1.setString(7, s.email); ps1.setString(8, "UGV");
                ps1.setString(9, s.uniID); ps1.setString(10, s.degree); ps1.setString(11, s.roomNo);
                ps1.setString(12, s.address); ps1.setString(13, "Living");
                ps1.executeUpdate();

                ps2.setString(1, s.roomNo);
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- 5. STUDENT MANAGEMENT LOGIC (ADVANCED) ---

    public Student getStudentByID(String uniID) {
        String query = "SELECT * FROM students WHERE university_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uniID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getString("name"), rs.getString("father_name"), rs.getString("mother_name"),
                        rs.getString("gender"), rs.getString("dob"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("university_id"), rs.getString("degree_program"),
                        rs.getString("room_number"), rs.getString("address"), rs.getString("status")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Advanced update logic handles both student data and room/status logic.
     * Use oldRoom and oldStatus parameters to check for changes and update room table accordingly.
     */
    public boolean updateStudentComplex(Student s, String oldRoom, String oldStatus) {
        String updateStudent = "UPDATE students SET name=?, father_name=?, mother_name=?, gender=?, dob=?, phone=?, email=?, degree_program=?, room_number=?, address=?, status=? WHERE university_id=?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(updateStudent)) {
                ps.setString(1, s.name); ps.setString(2, s.fatherName); ps.setString(3, s.motherName);
                ps.setString(4, s.gender); ps.setString(5, s.dob); ps.setString(6, s.phone);
                ps.setString(7, s.email); ps.setString(8, s.degree); ps.setString(9, s.roomNo);
                ps.setString(10, s.address); ps.setString(11, s.status); ps.setString(12, s.uniID);
                ps.executeUpdate();

                // Scenario A: Student changed their Room
                if (!s.roomNo.equals(oldRoom)) {
                    // Release the old room
                    conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Not Booked' WHERE room_number = '" + oldRoom + "'");
                    // Book the new room
                    conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Booked' WHERE room_number = '" + s.roomNo + "'");
                }

                // Scenario B: Living Status changed
                if (s.status.equals("Leaved")) {
                    // If student leaves, room becomes free
                    conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Not Booked' WHERE room_number = '" + s.roomNo + "'");
                } else if (s.status.equals("Living") && oldStatus.equals("Leaved")) {
                    // If student returns from 'Leaved' to 'Living', re-book the room
                    conn.createStatement().executeUpdate("UPDATE rooms SET room_status = 'Booked' WHERE room_number = '" + s.roomNo + "'");
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(String uniID, String roomNo) {
        String deleteQuery = "DELETE FROM students WHERE university_id = ?";
        String updateRoomQuery = "UPDATE rooms SET room_status = 'Not Booked' WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(deleteQuery);
                 PreparedStatement ps2 = conn.prepareStatement(updateRoomQuery)) {

                ps1.setString(1, uniID);
                ps1.executeUpdate();

                ps2.setString(1, roomNo);
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) { return false; }
    }

    // --- 1. Get Payment History for Table ---
    public List<Fee> getPaymentHistory(String uniID) {
        List<Fee> list = new ArrayList<>();
        String query = "SELECT * FROM fees WHERE university_id = ? ORDER BY payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uniID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Fee(rs.getString("university_id"), rs.getString("payment_month"),
                        rs.getDouble("total_amount"), rs.getDouble("amount_paid"),
                        rs.getDouble("due_amount"), rs.getString("payment_date")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 2. Check current Due for a Month ---
    public double getMonthDue(String uniID, String month) {
        String query = "SELECT SUM(amount_paid) FROM fees WHERE university_id = ? AND payment_month = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uniID); ps.setString(2, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double alreadyPaid = rs.getDouble(1);
                return 2500 - alreadyPaid; // Assuming 2500 is fixed fee
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 2500;
    }

    // --- 3. Save Fee Record ---
    public boolean saveFee(Fee f) {
        String query = "INSERT INTO fees (university_id, payment_month, total_amount, amount_paid, due_amount) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, f.uniID); ps.setString(2, f.month);
            ps.setDouble(3, f.total); ps.setDouble(4, f.paid); ps.setDouble(5, f.due);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- 1. Get All Students currently 'Living' ---
    public List<Student> getAllLivingStudents() {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students WHERE status = 'Living'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("name"), rs.getString("father_name"), rs.getString("mother_name"),
                        rs.getString("gender"), rs.getString("dob"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("university_id"), rs.getString("degree_program"),
                        rs.getString("room_number"), rs.getString("address"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 2. Search Living Students by University ID ---
    public List<Student> searchLivingStudents(String uniID) {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students WHERE status = 'Living' AND university_id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + uniID + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("name"), rs.getString("father_name"), rs.getString("mother_name"),
                        rs.getString("gender"), rs.getString("dob"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("university_id"), rs.getString("degree_program"),
                        rs.getString("room_number"), rs.getString("address"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 1. Get All Students who have 'Leaved' ---
    public List<Student> getAllLeavedStudents() {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students WHERE status = 'Leaved'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("name"), rs.getString("father_name"), rs.getString("mother_name"),
                        rs.getString("gender"), rs.getString("dob"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("university_id"), rs.getString("degree_program"),
                        rs.getString("room_number"), rs.getString("address"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 2. Search Leaved Students by University ID ---
    public List<Student> searchLeavedStudents(String uniID) {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students WHERE status = 'Leaved' AND university_id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + uniID + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("name"), rs.getString("father_name"), rs.getString("mother_name"),
                        rs.getString("gender"), rs.getString("dob"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("university_id"), rs.getString("degree_program"),
                        rs.getString("room_number"), rs.getString("address"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 1. Save New Employee ---
    public boolean saveEmployee(Employee e) {
        String query = "INSERT INTO employees (unique_id, name, father_name, mother_name, gender, dob, phone, email, designation, present_address, permanent_address, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, e.employeeID);
            ps.setString(2, e.name);
            ps.setString(3, e.fatherName);
            ps.setString(4, e.motherName);
            ps.setString(5, e.gender);
            ps.setString(6, e.dob);
            ps.setString(7, e.phone);
            ps.setString(8, e.email);
            ps.setString(9, e.designation);
            ps.setString(10, e.presentAddr);
            ps.setString(11, e.permanentAddr);
            ps.setString(12, "Working");
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    // --- 1. Search Employee by Unique ID ---
    public Employee getEmployeeByID(String empID) {
        String query = "SELECT * FROM employees WHERE unique_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, empID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Employee(
                        rs.getString("unique_id"), rs.getString("name"), rs.getString("father_name"),
                        rs.getString("mother_name"), rs.getString("gender"), rs.getString("dob"),
                        rs.getString("phone"), rs.getString("email"), rs.getString("designation"),
                        rs.getString("present_address"), rs.getString("permanent_address"), rs.getString("status")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- 2. Update Employee Information ---
    public boolean updateEmployee(Employee e) {
        String query = "UPDATE employees SET name=?, father_name=?, mother_name=?, gender=?, dob=?, phone=?, email=?, designation=?, present_address=?, permanent_address=?, status=? WHERE unique_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, e.name); ps.setString(2, e.fatherName); ps.setString(3, e.motherName);
            ps.setString(4, e.gender); ps.setString(5, e.dob); ps.setString(6, e.phone);
            ps.setString(7, e.email); ps.setString(8, e.designation);
            ps.setString(9, e.presentAddr); ps.setString(10, e.permanentAddr);
            ps.setString(11, e.status); ps.setString(12, e.employeeID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    // --- 3. Delete Employee ---
    public boolean deleteEmployee(String empID) {
        String query = "DELETE FROM employees WHERE unique_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, empID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    // --- 1. Save Salary Record ---
    public boolean saveSalary(String empID, String month, double amount) {
        String query = "INSERT INTO salaries (unique_id, payment_month, amount) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, empID);
            ps.setString(2, month);
            ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- 2. Check if Salary is already paid for a month ---
    public boolean isSalaryPaid(String empID, String month) {
        String query = "SELECT * FROM salaries WHERE unique_id = ? AND payment_month = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, empID);
            ps.setString(2, month);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    // --- 3. Get Salary History for Table ---
    public List<Object[]> getSalaryHistory(String empID) {
        List<Object[]> history = new ArrayList<>();
        String query = "SELECT payment_month, amount, payment_date FROM salaries WHERE unique_id = ? ORDER BY payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, empID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(new Object[]{rs.getString(1), rs.getDouble(2), rs.getString(3)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return history;
    }

    // --- 1. Get All Currently 'Working' Employees ---
    public List<Employee> getAllWorkingEmployees() {
        List<Employee> list = new ArrayList<>();
        String query = "SELECT * FROM employees WHERE status = 'Working'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("unique_id"), rs.getString("name"), rs.getString("father_name"),
                        rs.getString("mother_name"), rs.getString("gender"), rs.getString("dob"),
                        rs.getString("phone"), rs.getString("email"), rs.getString("designation"),
                        rs.getString("present_address"), rs.getString("permanent_address"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 2. Search Working Employees by ID ---
    public List<Employee> searchWorkingEmployees(String empID) {
        List<Employee> list = new ArrayList<>();
        String query = "SELECT * FROM employees WHERE status = 'Working' AND unique_id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + empID + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("unique_id"), rs.getString("name"), rs.getString("father_name"),
                        rs.getString("mother_name"), rs.getString("gender"), rs.getString("dob"),
                        rs.getString("phone"), rs.getString("email"), rs.getString("designation"),
                        rs.getString("present_address"), rs.getString("permanent_address"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 3. Get All 'Leaved' Employees ---
    public List<Employee> getAllLeavedEmployees() {
        List<Employee> list = new ArrayList<>();
        String query = "SELECT * FROM employees WHERE status = 'Leaved'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("unique_id"), rs.getString("name"), rs.getString("father_name"),
                        rs.getString("mother_name"), rs.getString("gender"), rs.getString("dob"),
                        rs.getString("phone"), rs.getString("email"), rs.getString("designation"),
                        rs.getString("present_address"), rs.getString("permanent_address"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Employee> searchLeavedEmployees(String empID) {
        List<Employee> list = new ArrayList<>();
        String query = "SELECT * FROM employees WHERE status = 'Leaved' AND unique_id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + empID + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("unique_id"), rs.getString("name"), rs.getString("father_name"),
                        rs.getString("mother_name"), rs.getString("gender"), rs.getString("dob"),
                        rs.getString("phone"), rs.getString("email"), rs.getString("designation"),
                        rs.getString("present_address"), rs.getString("permanent_address"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}