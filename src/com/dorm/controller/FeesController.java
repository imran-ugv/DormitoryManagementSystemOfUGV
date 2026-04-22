package com.dorm.controller;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Fee;
import com.dorm.model.Student;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller for managing Dormitory Fees.
 * Handles search, due calculation, and payment processing logic.
 */
public class FeesController {
    private DormitoryDAO dao = new DormitoryDAO();

    // Logic to search for a student and provide feedback if not found
    public Student handleSearch(String id, Component parent) {
        if (id.isEmpty()) return null;

        Student s = dao.getStudentByID(id);
        if (s == null) {
            JOptionPane.showMessageDialog(parent, "Student ID not found!", "Search Error", JOptionPane.ERROR_MESSAGE);
        }
        return s;
    }

    // Fetch current due for a specific student and month
    public double getDueAmount(String id, String monthStr) {
        return dao.getMonthDue(id, monthStr);
    }

    // Fetch payment history for a specific student
    public List<Fee> getPaymentHistory(String id) {
        return dao.getPaymentHistory(id);
    }

    // Logic to validate and save a fee payment
    public boolean handlePayment(Fee fee, double currentDue, Component parent) {
        // 1. Logic: Amount must be greater than zero
        if (fee.paid <= 0) {
            JOptionPane.showMessageDialog(parent, "Amount must be greater than zero!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 2. Logic: Cannot pay more than what is due
        if (fee.paid > currentDue) {
            JOptionPane.showMessageDialog(parent, "Paying more than the current due is not allowed!", "Payment Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. Save to Database via DAO
        if (dao.saveFee(fee)) {
            JOptionPane.showMessageDialog(parent, "Payment of " + fee.paid + " successfully recorded for " + fee.month);
            return true;
        }
        return false;
    }
}