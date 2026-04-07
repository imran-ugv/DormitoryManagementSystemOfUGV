package com.dorm.view;

import com.dorm.model.DormitoryDAO; // Imported the DAO for DB access
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    // Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin, btnCancel;

    public LoginView() {
        // Basic window setup
        setTitle("University Dormitory Management System - Login");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
        setLayout(new GridLayout(1, 2)); // Two equal halves

        // --- LEFT PANEL: Branding ---
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(153, 153, 255));
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        try {
            // Load the Logo image from the resources package
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/com/dorm/resources/UGV-Logo-02.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(150, 220, Image.SCALE_SMOOTH);
            ImageIcon logoIcon = new ImageIcon(scaledImage);
            JLabel lblLogo = new JLabel(logoIcon);
            leftPanel.add(lblLogo, gbc);
        } catch (Exception e) {
            leftPanel.add(new JLabel("🏛"));
        }

        // Branding Text
        gbc.gridy = 1;
        JLabel lblBrandingText = new JLabel("<html><div style='text-align: center; color: white;'>"
                + "<h1 style='font-size: 24px;'>UGV<br>Dormitory<br>Management</h1>"
                + "</div></html>");
        leftPanel.add(lblBrandingText, gbc);

        // --- RIGHT PANEL: Login Form ---
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(64, 224, 208));
        rightPanel.setLayout(null);

        // Title
        JLabel lblTitle = new JLabel("Admin Login");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 36));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(120, 40, 250, 50);
        rightPanel.add(lblTitle);

        // Username Label and Field
        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(40, 130, 80, 25);
        rightPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(130, 130, 250, 30);
        rightPanel.add(txtUsername);

        // Password Label and Field
        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(40, 180, 80, 25);
        rightPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 180, 250, 30);
        rightPanel.add(txtPassword);

        // Show Password Checkbox
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setBounds(260, 215, 130, 20);
        chkShowPassword.setBackground(new Color(64, 224, 208));
        chkShowPassword.setForeground(Color.WHITE);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        rightPanel.add(chkShowPassword);

        // Buttons
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(130, 300, 110, 40);
        btnCancel.setBackground(Color.RED);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        rightPanel.add(btnCancel);

        btnLogin = new JButton("Log In");
        btnLogin.setBounds(270, 300, 110, 40);
        btnLogin.setBackground(new Color(39, 174, 96));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        rightPanel.add(btnLogin);

        // Add panels to frame
        add(leftPanel);
        add(rightPanel);

        // --- BUTTON ACTIONS ---

        // Cancel Action
        btnCancel.addActionListener(e -> System.exit(0));

        // Updated Login Button Action with Database Logic
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText().trim();
                String pass = new String(txtPassword.getPassword()).trim();

                // Validation: Check if fields are empty
                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both Username and Password!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    // --- DATABASE CHECK ---
                    DormitoryDAO dao = new DormitoryDAO();
                    if (dao.checkLogin(user, pass)) {
                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        new MainDashboard(); // Opens the dashboard
                        dispose(); // Closes the login window
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Username or Password from Database!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        setVisible(true);
    }
}