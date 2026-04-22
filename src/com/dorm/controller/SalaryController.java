package com.dorm.controller;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Employee;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller to handle Employee Salary logic.
 * Manages search, duplicate payment checks, and data persistence.
 */
public class SalaryController {
    private DormitoryDAO dao = new DormitoryDAO();

    // Logic to search for an employee and handle feedback
    public Employee handleSearch(String id, Component parent) {
        if (id.isEmpty()) return null;

        Employee emp = dao.getEmployeeByID(id);
        if (emp == null) {
            JOptionPane.showMessageDialog(parent, "Employee ID Not Found!", "Search Error", JOptionPane.ERROR_MESSAGE);
        }
        return emp;
    }

    // Fetch salary history for a specific employee
    public List<Object[]> getSalaryHistory(String id) {
        return dao.getSalaryHistory(id);
    }

    // Logic to validate and save salary payment
    public boolean handleSalaryPayment(String id, String monthStr, String amountStr, Component parent) {
        // 1. Validation: Amount empty check
        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Please enter an amount!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            // 2. Business Logic: Check for duplicate payment in same month
            if (dao.isSalaryPaid(id, monthStr)) {
                JOptionPane.showMessageDialog(parent, "Salary already paid for " + monthStr, "Payment Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 3. Database Save
            if (dao.saveSalary(id, monthStr, amount)) {
                JOptionPane.showMessageDialog(parent, "Salary Payment of " + amount + " successfully recorded!");
                return true;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parent, "Invalid numeric amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}