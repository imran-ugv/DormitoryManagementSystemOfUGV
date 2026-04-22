package com.dorm.controller;

import com.dorm.model.DormitoryDAO;
import com.dorm.view.MainDashboard;
import javax.swing.*;

/**
 * Controller to handle Authentication (Login/Logout) logic.
 * Mediates between LoginView and DormitoryDAO.
 */
public class AuthController {
    private DormitoryDAO dao = new DormitoryDAO();

    // Logic to validate credentials and switch to Dashboard
    public void handleLogin(JFrame currentFrame, String username, String password) {
        // 1. Validation Logic
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(currentFrame, "Please enter both Username and Password!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Database Logic through DAO
        if (dao.checkLogin(username, password)) {
            JOptionPane.showMessageDialog(currentFrame, "Login Successful!");
            new MainDashboard(); // Launch next view
            currentFrame.dispose(); // Close current view
        } else {
            JOptionPane.showMessageDialog(currentFrame, "Invalid Username or Password from Database!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}