package com.dorm.controller;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Employee;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Controller to handle ManageEmployeeView logic.
 * Separates data processing and validation from the UI.
 */
public class EmployeeController {
    private DormitoryDAO dao = new DormitoryDAO();

    // Logic to search an employee and return the object
    public Employee handleSearch(String id, Component parent) {
        if (id.isEmpty()) return null;

        Employee emp = dao.getEmployeeByID(id);
        if (emp == null) {
            JOptionPane.showMessageDialog(parent, "Employee ID Not Found!", "Search Error", JOptionPane.ERROR_MESSAGE);
        }
        return emp;
    }

    // Logic to validate and update employee data
    public boolean handleUpdate(Employee emp, String dob, Component parent) {
        // 1. Date Validation
        if (dob.contains("_")) {
            JOptionPane.showMessageDialog(parent, "Please enter complete Date of Birth!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            LocalDate.parse(dob);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(parent, "Invalid Date Calendar Format!", "Date Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Email Validation
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", emp.email)) {
            JOptionPane.showMessageDialog(parent, "Invalid Email Format!", "Email Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. Database Update
        if (dao.updateEmployee(emp)) {
            JOptionPane.showMessageDialog(parent, "Employee Record Updated Successfully!");
            return true;
        }
        return false;
    }

    // Logic to handle employee deletion
    public boolean handleDelete(String id, Component parent) {
        if (id.isEmpty()) return false;

        int confirm = JOptionPane.showConfirmDialog(parent, "Delete this employee permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteEmployee(id)) {
                JOptionPane.showMessageDialog(parent, "Employee Deleted Successfully!");
                return true;
            }
        }
        return false;
    }

    // Logic to validate and save a new employee
    public boolean handleRegistration(Employee emp, String dob, Component parent) {
        // 1. Basic Field Validation
        if (emp.employeeID.isEmpty() || dob.contains("_") || emp.name.isEmpty() || emp.phone.isEmpty() || emp.email.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Please fill all required fields (*)! ", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 2. Date Format Validation
        try {
            LocalDate.parse(dob);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(parent, "Invalid Date Calendar Format!", "Date Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. Email Format Validation
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", emp.email)) {
            JOptionPane.showMessageDialog(parent, "Invalid Email Format!", "Email Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 4. Save to Database via DAO
        if (dao.saveEmployee(emp)) {
            JOptionPane.showMessageDialog(parent, "Employee Registered Successfully!");
            return true;
        } else {
            JOptionPane.showMessageDialog(parent, "Registration Failed! Employee ID or Phone might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Logic to fetch all currently working employees
    public java.util.List<Employee> getWorkingEmployees() {
        return dao.getAllWorkingEmployees();
    }

    // Logic to filter working employees by ID
    public java.util.List<Employee> filterWorkingEmployees(String query) {
        return dao.searchWorkingEmployees(query);
    }

    // Logic to fetch all employees who have left
    public java.util.List<Employee> getLeavedEmployees() {
        return dao.getAllLeavedEmployees();
    }

    // Logic to filter leaved employees by ID
    public java.util.List<Employee> filterLeavedEmployees(String query) {
        return dao.searchLeavedEmployees(query);
    }
}