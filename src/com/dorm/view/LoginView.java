package com.dorm.view;

import com.dorm.controller.AuthController; // Added Controller import
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin, btnCancel;

    // Instantiate Controller
    private AuthController controller = new AuthController();

    public LoginView() {
        // Basic window setup
        setTitle("University Dormitory Management System - Login");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2));

        // --- LEFT PANEL: Branding ---
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(153, 153, 255));
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 10, 10);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/com/dorm/resources/UGV-Logo-02.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(150, 220, Image.SCALE_SMOOTH);
            leftPanel.add(new JLabel(new ImageIcon(scaledImage)), gbc);
        } catch (Exception e) {
            leftPanel.add(new JLabel("🏛"), gbc);
        }

        gbc.gridy = 1;
        JLabel lblBrandingText = new JLabel("<html><div style='text-align: center; color: white;'>"
                + "<h1 style='font-size: 24px;'>UGV<br>Dormitory<br>Management</h1>"
                + "</div></html>");
        leftPanel.add(lblBrandingText, gbc);

        // --- RIGHT PANEL: Login Form ---
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(64, 224, 208));
        rightPanel.setLayout(null);

        JLabel lblTitle = new JLabel("Admin Login");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 36));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(120, 40, 250, 50);
        rightPanel.add(lblTitle);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(40, 130, 80, 25);
        rightPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(130, 130, 250, 30);
        rightPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(40, 180, 80, 25);
        rightPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 180, 250, 30);
        rightPanel.add(txtPassword);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setBounds(260, 215, 130, 20);
        chkShowPassword.setBackground(new Color(64, 224, 208));
        chkShowPassword.setForeground(Color.WHITE);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) txtPassword.setEchoChar((char) 0);
            else txtPassword.setEchoChar('•');
        });
        rightPanel.add(chkShowPassword);

        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(130, 300, 110, 40);
        btnCancel.setBackground(Color.RED);
        btnCancel.setForeground(Color.WHITE);
        rightPanel.add(btnCancel);

        btnLogin = new JButton("Log In");
        btnLogin.setBounds(270, 300, 110, 40);
        btnLogin.setBackground(new Color(39, 174, 96));
        btnLogin.setForeground(Color.WHITE);
        rightPanel.add(btnLogin);

        add(leftPanel); add(rightPanel);

        // --- BUTTON ACTIONS: Delegating to Controller ---
        btnCancel.addActionListener(e -> System.exit(0));

        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();

            // Controller handles the logic now
            controller.handleLogin(this, user, pass);
        });

        setVisible(true);
    }
}