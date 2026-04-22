package com.dorm.controller;

import com.dorm.model.DormitoryDAO;
import com.dorm.view.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Controller for MainDashboard.
 * Handles navigation logic and chart data generation.
 */
public class DashboardController {
    private DormitoryDAO dao = new DormitoryDAO();

    // Fetch fresh stats from DAO
    public Map<String, Integer> getStats() {
        return dao.getDashboardStats();
    }

    // Navigation logic to decide which panel to show
    public JPanel getTargetPanel(String menuName) {
        switch (menuName) {
            case "Add Room": return new RoomView();
            case "Manage Rooms": return new ManageRoomsView();
            case "New Student": return new NewStudentView();
            case "Manage Students": return new ManageStudentView();
            case "Dormitory Fees": return new DormitoryFeesView();
            case "Living Students": return new LivingStudentsView();
            case "Leaved Students": return new LeavedStudentsView();
            case "New Employee": return new NewEmployeeView();
            case "Manage Employees": return new ManageEmployeeView();
            case "Employees Salary": return new EmployeeSalaryView();
            case "Working Employees": return new WorkingEmployeesView();
            case "Leaved Employees": return new LeavedEmployeesView();
            default: return null;
        }
    }

    // Logout logic with confirmation
    public void handleLogout(JFrame dashboardFrame) {
        int response = JOptionPane.showConfirmDialog(dashboardFrame, "Are you sure you want to Logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            dashboardFrame.dispose();
            new LoginView();
        }
    }

    // --- CHART LOGIC MOVED FROM VIEW TO CONTROLLER ---

    public JFreeChart createBarChart(String category, Map<String, Integer> data) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        if (category.equals("Students")) {
            ds.addValue(data.getOrDefault("Living Students", 0), "Count", "Living");
            ds.addValue(data.getOrDefault("Leaved Students", 0), "Count", "Leaved");
        } else {
            ds.addValue(data.getOrDefault("Working Employees", 0), "Count", "Working");
            ds.addValue(data.getOrDefault("Not Working Employees", 0), "Count", "Leaved");
        }
        return ChartFactory.createBarChart(null, "Status", "People", ds, PlotOrientation.VERTICAL, false, true, false);
    }

    public JFreeChart createLineChart(Map<String, Integer> data) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        int booked = data.getOrDefault("Booked Rooms", 0);
        int available = data.getOrDefault("Available Rooms", 0);
        ds.addValue(booked + available, "Rooms", "Total Capacity");
        ds.addValue(booked, "Rooms", "Current Booked");
        JFreeChart chart = ChartFactory.createLineChart(null, "Dormitory", "Rooms Count", ds, PlotOrientation.VERTICAL, false, true, false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);
        return chart;
    }

    public JFreeChart createPieChart(Map<String, Integer> data) {
        DefaultPieDataset ds = new DefaultPieDataset();
        ds.setValue("Available Rooms", data.getOrDefault("Available Rooms", 0));
        ds.setValue("Booked Rooms", data.getOrDefault("Booked Rooms", 0));
        return ChartFactory.createPieChart(null, ds, true, true, false);
    }
}