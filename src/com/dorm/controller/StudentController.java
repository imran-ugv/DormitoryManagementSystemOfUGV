package com.dorm.controller;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Student;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Refined Controller for Student management.
 * Strictly prevents double-booking rooms during Registration and Updates.
 */
public class StudentController {
    private DormitoryDAO dao = new DormitoryDAO();

    // Fetch available rooms list for the dropdown
    public List<String> getAvailableRooms() {
        return dao.getAvailableRooms();
    }

    // Logic to search a student by ID
    public Student handleSearch(String id, Component parent) {
        if (id.isEmpty()) return null;

        Student s = dao.getStudentByID(id);
        if (s == null) {
            JOptionPane.showMessageDialog(parent, "Record Not Found with ID: " + id, "Search Error", JOptionPane.ERROR_MESSAGE);
        }
        return s;
    }

    // Logic to validate and update student info including room/status changes
    public boolean handleUpdate(Student s, String oldRoom, String oldStatus, String dob, Component parent) {
        // 1. Basic Format Validations
        if (dob.contains("_")) {
            JOptionPane.showMessageDialog(parent, "Please enter complete Date of Birth!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            LocalDate.parse(dob);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(parent, "Invalid Date Format!", "Date Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", s.email)) {
            JOptionPane.showMessageDialog(parent, "Invalid Email Format!", "Email Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. CRITICAL: If Room is being changed, check if the NEW room is available
        if (!s.roomNo.equals(oldRoom)) {
            if (!dao.isRoomStillAvailable(s.roomNo)) {
                JOptionPane.showMessageDialog(parent, "The target Room " + s.roomNo + " is already booked! Please select another.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // 3. Database Update via DAO
        if (dao.updateStudentComplex(s, oldRoom, oldStatus)) {
            JOptionPane.showMessageDialog(parent, "Student Record Updated Successfully!");
            return true;
        } else {
            JOptionPane.showMessageDialog(parent, "Update Failed! Check Database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Logic to handle student deletion with confirmation
    public boolean handleDelete(String id, String roomNo, Component parent) {
        if (id.isEmpty()) return false;

        int confirm = JOptionPane.showConfirmDialog(parent, "Are you sure you want to delete this student and free the room?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteStudent(id, roomNo)) {
                JOptionPane.showMessageDialog(parent, "Student deleted and room " + roomNo + " is now available!");
                return true;
            }
        }
        return false;
    }

    // Logic to validate and save a new student record
    public boolean handleRegistration(Student s, String dob, Component parent) {
        // 1. Basic Field Validation
        if (s.name.isEmpty() || s.phone.isEmpty() || s.email.isEmpty() || s.uniID.isEmpty() || s.roomNo.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Please fill all required fields (*)", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 2. Date and Email Validation
        if (dob.contains("_")) {
            JOptionPane.showMessageDialog(parent, "Please enter complete Date of Birth!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            LocalDate.parse(dob);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(parent, "Invalid Date Format!", "Date Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", s.email)) {
            JOptionPane.showMessageDialog(parent, "Invalid Email Format!", "Email Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. CRITICAL: Final check before saving to prevent double-booking
        if (!dao.isRoomStillAvailable(s.roomNo)) {
            JOptionPane.showMessageDialog(parent, "Room " + s.roomNo + " was just booked! Please select another.", "Room Unavailable", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 4. Persistence via DAO
        if (dao.saveStudent(s)) {
            JOptionPane.showMessageDialog(parent, "Student Registered Successfully for UGV!");
            return true;
        } else {
            JOptionPane.showMessageDialog(parent, "Registration Failed! Phone or ID Card might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // List fetching methods
    public List<Student> getLeavedStudents() { return dao.getAllLeavedStudents(); }
    public List<Student> filterLeavedStudents(String query) { return dao.searchLeavedStudents(query); }
    public List<Student> getLivingStudents() { return dao.getAllLivingStudents(); }
    public List<Student> filterLivingStudents(String query) { return dao.searchLivingStudents(query); }
}