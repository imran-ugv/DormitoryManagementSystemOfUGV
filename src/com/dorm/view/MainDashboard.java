package com.dorm.view;

import com.dorm.controller.DashboardController; // Import Controller
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class MainDashboard extends JFrame {

    private JPanel sideBar, header, mainContent;
    private DashboardController controller = new DashboardController(); // Using Controller

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
        } catch (Exception e) { leftHeader.add(new JLabel("🏛")); }

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
                controller.handleLogout(MainDashboard.this); // Delegate to controller
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
                else {
                    JPanel panel = controller.getTargetPanel(item[0]); // Get panel from controller
                    if (panel != null) showPanel(panel);
                }
            });
            menuContainer.add(btn);
        }
        sideBar.add(menuContainer, BorderLayout.NORTH);
        JPanel filler = new JPanel(); filler.setBackground(Color.WHITE);
        sideBar.add(filler, BorderLayout.CENTER);
    }

    private void showDashboardHome() {
        Map<String, Integer> statsData = controller.getStats(); // Fetch from controller
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

        chartSection.add(createChartWrapper("Records Analysis", controller.createBarChart("Students", statsData), "Bar", true));
        chartSection.add(createChartWrapper("Occupancy Trend", controller.createLineChart(statsData), "Line", false));
        chartSection.add(createChartWrapper("Room Distribution", controller.createPieChart(statsData), "Pie", false));

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
            btnS.addActionListener(e -> chartPanel.setChart(controller.createBarChart("Students", controller.getStats())));
            btnE.addActionListener(e -> chartPanel.setChart(controller.createBarChart("Employees", controller.getStats())));
            btnPanel.add(btnS); btnPanel.add(btnE);
            pnl.add(btnPanel, BorderLayout.SOUTH);
        }
        return pnl;
    }
}