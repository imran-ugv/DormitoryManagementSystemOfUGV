package com.dorm.main;

import com.dorm.view.LoginView;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Apply FlatLaf for a modern look
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Launch the Login Screen
        new LoginView();
    }
}