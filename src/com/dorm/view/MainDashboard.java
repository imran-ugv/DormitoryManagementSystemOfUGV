package com.dorm.view;

import com.dorm.model.DormitoryDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Final Refined Main Dashboard.
 * Fixed Occupancy Trend logic to ensure lines are visible.
 * Synchronized all charts with real-time database counts.
 */
public class MainDashboard extends JFrame {

    private JPanel sideBar, header, mainContent;
    private DormitoryDAO dao = new DormitoryDAO();

    public MainDashboard() {
        setTitle("University Dormitory Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createSideBar();

        mainContent = new JPanel();
        mainContent.setBackground(new Color(245, 245, 245));
        mainContent.setLayout(new BorderLayout());

        showDashboardHome();

        add(header, BorderLayout.NORTH);
        add(sideBar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);

        setVisible(true);
    }

    private void createHeader() {
        header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 153, 255));
        header.setPreferredSize(new Dimension(100, 60));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftHeader.setOpaque(false);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/dorm/resources/UGV-Logo-02.png"));
            Image img = icon.getImage().getScaledInstance(30, 40, Image.SCALE_SMOOTH);
            leftHeader.add(new JLabel(new ImageIcon(img)));
        } catch (Exception e) {
            leftHeader.add(new JLabel("🏛"));
        }

        JLabel lblTitle = new JLabel("University Dormitory Management");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        leftHeader.add(lblTitle);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 15));
        rightHeader.setOpaque(false);
        JLabel lblAdmin = new JLabel("👤 WELCOME ADMIN   🚪 Logout");
        lblAdmin.setForeground(Color.WHITE);
        lblAdmin.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (JOptionPane.showConfirmDialog(null, "Logout?", "Confirm", 0) == 0) {
                    dispose(); new LoginView();
                }
            }
        });
        rightHeader.add(lblAdmin);
        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
    }

    private void createSideBar() {
        sideBar = new JPanel();
        sideBar.setLayout(new BorderLayout());
        sideBar.setBackground(Color.WHITE);
        sideBar.setPreferredSize(new Dimension(240, 0));
        sideBar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);

        String[][] menuItems = {
                {"Dashboard", "🏠"}, {"Add Room", "➕"}, {"Manage Rooms", "🛏"},
                {"New Student", "👤"}, {"Manage Students", "👨‍🎓"}, {"Dormitory Fees", "💵"},
                {"Living Students", "🏘"}, {"Leaved Students", "🚶"}, {"New Employee", "👷"},
                {"Manage Employees", "🛠"}, {"Employees Salary", "💰"}, {"Working Employees", "✅"},
                {"Leaved Employees", "❌"}
        };

        for (String[] item : menuItems) {
            JButton btn = new JButton(" " + item[1] + "   " + item[0]);
            btn.setMaximumSize(new Dimension(240, 45));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorder(new EmptyBorder(10, 20, 10, 10));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> {
                if (item[0].equals("Dashboard")) showDashboardHome();
                else if (item[0].equals("Add Room")) showPanel(new RoomView());
                else if (item[0].equals("Manage Rooms")) showPanel(new ManageRoomsView());
                else if (item[0].equals("New Student")) showPanel(new NewStudentView());
                else if (item[0].equals("Manage Students")) showPanel(new ManageStudentView());
                else if (item[0].equals("Dormitory Fees")) showPanel(new DormitoryFeesView());
                else if (item[0].equals("Living Students")) showPanel(new LivingStudentsView());
                else if (item[0].equals("Leaved Students")) showPanel(new LeavedStudentsView());
                else if (item[0].equals("New Employee")) showPanel(new NewEmployeeView());
                else if (item[0].equals("Manage Employees")) showPanel(new ManageEmployeeView());
                else if (item[0].equals("Employees Salary")) showPanel(new EmployeeSalaryView());
                else if (item[0].equals("Working Employees")) showPanel(new WorkingEmployeesView());
                else if (item[0].equals("Leaved Employees")) showPanel(new LeavedEmployeesView());
            });
            menuContainer.add(btn);
        }
        sideBar.add(menuContainer, BorderLayout.NORTH);
        JPanel filler = new JPanel(); filler.setBackground(Color.WHITE);
        sideBar.add(filler, BorderLayout.CENTER);
    }

    private void showDashboardHome() {
        Map<String, Integer> statsData = dao.getDashboardStats();
        JPanel homePanel = new JPanel(new GridBagLayout());
        homePanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.05;
        JLabel lblDash = new JLabel("  📊 DASHBOARD OVERVIEW");
        lblDash.setFont(new Font("SansSerif", Font.BOLD, 22));
        homePanel.add(lblDash, gbc);

        gbc.gridy = 1; gbc.weighty = 0.25;
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        String[] labels = {"Available Rooms", "Booked Rooms", "Living Students", "Leaved Students", "Working Employees", "Not Working Employees"};
        for (String label : labels) {
            gridPanel.add(createCard(label, String.valueOf(statsData.getOrDefault(label, 0))));
        }
        homePanel.add(gridPanel, gbc);

        gbc.gridy = 2; gbc.weighty = 0.70;
        JPanel chartSection = new JPanel(new GridLayout(1, 3, 25, 0));
        chartSection.setOpaque(false);
        chartSection.setBorder(new EmptyBorder(10, 20, 20, 20));

        chartSection.add(createChartWrapper("Records Analysis", createBarChart("Students", statsData), "Bar", true));
        chartSection.add(createChartWrapper("Occupancy Trend", createLineChart(statsData), "Line", false));
        chartSection.add(createChartWrapper("Room Distribution", createPieChart(statsData), "Pie", false));

        homePanel.add(chartSection, gbc);
        mainContent.removeAll();
        mainContent.add(homePanel, BorderLayout.CENTER);
        mainContent.revalidate(); mainContent.repaint();
    }

    private void showPanel(JPanel panel) {
        mainContent.removeAll(); mainContent.add(panel, BorderLayout.CENTER);
        mainContent.revalidate(); mainContent.repaint();
    }

    private JPanel createCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(255, 255, 204));
        card.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        JLabel lblTop = new JLabel(title, SwingConstants.CENTER);
        lblTop.setOpaque(true); lblTop.setBackground(new Color(102, 255, 102));
        lblTop.setPreferredSize(new Dimension(0, 30)); lblTop.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel lblVal = new JLabel(value, SwingConstants.CENTER);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 30));
        card.add(lblTop, BorderLayout.NORTH); card.add(lblVal, BorderLayout.CENTER);
        return card;
    }

    private JPanel createChartWrapper(String title, JFreeChart chart, String chartType, boolean showButtons) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setBorder(new EmptyBorder(8, 0, 8, 0));
        pnl.add(lbl, BorderLayout.NORTH);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        pnl.add(chartPanel, BorderLayout.CENTER);

        if (showButtons) {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            btnPanel.setOpaque(false);
            JButton btnS = new JButton("Student"); JButton btnE = new JButton("Employee");
            btnS.addActionListener(e -> chartPanel.setChart(createBarChart("Students", dao.getDashboardStats())));
            btnE.addActionListener(e -> chartPanel.setChart(createBarChart("Employees", dao.getDashboardStats())));
            btnPanel.add(btnS); btnPanel.add(btnE);
            pnl.add(btnPanel, BorderLayout.SOUTH);
        }
        return pnl;
    }

    private JFreeChart createBarChart(String category, Map<String, Integer> data) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        if(category.equals("Students")) {
            ds.addValue(data.getOrDefault("Living Students", 0), "Count", "Living");
            ds.addValue(data.getOrDefault("Leaved Students", 0), "Count", "Leaved");
        } else {
            ds.addValue(data.getOrDefault("Working Employees", 0), "Count", "Working");
            ds.addValue(data.getOrDefault("Not Working Employees", 0), "Count", "Leaved");
        }
        return ChartFactory.createBarChart(null, "Status", "People", ds, PlotOrientation.VERTICAL, false, true, false);
    }

    private JFreeChart createLineChart(Map<String, Integer> data) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        int booked = data.getOrDefault("Booked Rooms", 0);
        int available = data.getOrDefault("Available Rooms", 0);
        int total = booked + available;

        // Using two points on X-axis (Total vs Occupied) to ensure line is visible
        ds.addValue(total, "Rooms", "Total Capacity");
        ds.addValue(booked, "Rooms", "Current Booked");

        JFreeChart chart = ChartFactory.createLineChart(null, "Dormitory", "Rooms Count", ds, PlotOrientation.VERTICAL, false, true, false);

        // Customizing the renderer to show shapes/points on the line
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true); // Show dots on points
        plot.setRenderer(renderer);

        return chart;
    }

    private JFreeChart createPieChart(Map<String, Integer> data) {
        DefaultPieDataset ds = new DefaultPieDataset();
        ds.setValue("Available Rooms", data.getOrDefault("Available Rooms", 0));
        ds.setValue("Booked Rooms", data.getOrDefault("Booked Rooms", 0));
        return ChartFactory.createPieChart(null, ds, true, true, false);
    }
}