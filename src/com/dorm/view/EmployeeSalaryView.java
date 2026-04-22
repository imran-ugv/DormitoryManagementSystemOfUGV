package com.dorm.view;

import com.dorm.controller.SalaryController; // Import Controller
import com.dorm.model.Employee;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class EmployeeSalaryView extends JPanel {
    private JTextField txtSearch, txtName, txtDesignation, txtAmount;
    private JComboBox<String> cmbMonth, cmbYear;
    private JButton btnSearch, btnPay, btnClear;
    private JTable salaryTable;
    private DefaultTableModel model;

    // Using Controller instead of direct DAO
    private SalaryController controller = new SalaryController();

    public EmployeeSalaryView() {
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1), new EmptyBorder(40, 50, 40, 50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(12, 15, 12, 15);

        // Header
        JLabel lblHeader = new JLabel("EMPLOYEE SALARY MANAGEMENT");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 26)); lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0); formContainer.add(lblHeader, gbc);

        // Search Bar
        gbc.gridwidth = 1; gbc.insets = new Insets(10, 10, 25, 10);
        addLabel(formContainer, "Employee ID:", 0, 1);
        txtSearch = createField();
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        });
        formContainer.add(txtSearch, getGbc(1, 1));
        btnSearch = new JButton("🔍 Search Employee");
        btnSearch.setPreferredSize(new Dimension(180, 35)); btnSearch.setBackground(new Color(153, 153, 255)); btnSearch.setForeground(Color.WHITE);
        formContainer.add(btnSearch, getGbc(2, 1));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.insets = new Insets(10, 0, 30, 0);
        formContainer.add(new JSeparator(), gbc);

        // Input Fields (Design kept same)
        gbc.gridwidth = 1; gbc.insets = new Insets(12, 10, 12, 10);
        int r = 3;
        addLabel(formContainer, "Employee Name:", 0, r); txtName = createField(); txtName.setEditable(false); txtName.setBackground(new Color(245, 245, 245)); formContainer.add(txtName, getGbc(1, r++));
        addLabel(formContainer, "Designation:", 0, r); txtDesignation = createField(); txtDesignation.setEditable(false); txtDesignation.setBackground(new Color(245, 245, 245)); formContainer.add(txtDesignation, getGbc(1, r++));

        int rightR = 3;
        addLabel(formContainer, "Salary Month:", 2, rightR);
        JPanel datePnl = new JPanel(new GridLayout(1, 2, 10, 0)); datePnl.setOpaque(false);
        cmbMonth = new JComboBox<>(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        cmbYear = new JComboBox<>(new String[]{"2024", "2025", "2026"});
        datePnl.add(cmbMonth); datePnl.add(cmbYear); formContainer.add(datePnl, getGbc(3, rightR++));
        addLabel(formContainer, "Amount to Pay:", 2, rightR); txtAmount = createField();
        txtAmount.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { char c = e.getKeyChar(); if (!Character.isDigit(c) && c != '.') e.consume(); }});
        formContainer.add(txtAmount, getGbc(3, rightR++));

        // History Table
        gbc.gridx = 0; gbc.gridy = Math.max(r, rightR); gbc.gridwidth = 4; gbc.insets = new Insets(30, 0, 10, 0);
        JLabel lblTable = new JLabel("PAYMENT HISTORY", SwingConstants.CENTER);
        lblTable.setFont(new Font("SansSerif", Font.BOLD, 16)); formContainer.add(lblTable, gbc);

        model = new DefaultTableModel(new String[]{"Month-Year", "Amount Paid", "Payment Date"}, 0);
        salaryTable = new JTable(model) { public boolean isCellEditable(int r, int c) { return false; } };
        salaryTable.setRowHeight(25); JScrollPane tableScroll = new JScrollPane(salaryTable);
        tableScroll.setPreferredSize(new Dimension(800, 150));
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0); formContainer.add(tableScroll, gbc);

        // Buttons
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); btnPnl.setOpaque(false);
        btnPay = new JButton("Confirm Payment"); btnPay.setBackground(new Color(39, 174, 96)); btnPay.setForeground(Color.WHITE); btnPay.setPreferredSize(new Dimension(180, 45)); btnPay.setEnabled(false);
        btnClear = new JButton("Clear Form"); btnClear.setPreferredSize(new Dimension(140, 45));
        btnPnl.add(btnPay); btnPnl.add(btnClear);
        gbc.gridy++; gbc.insets = new Insets(30, 0, 0, 0); formContainer.add(btnPnl, gbc);

        wrapper.add(formContainer);
        JScrollPane mainScroll = new JScrollPane(wrapper); mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        // --- ACTIONS: Delegating to Controller ---

        btnSearch.addActionListener(e -> {
            Employee emp = controller.handleSearch(txtSearch.getText().trim(), this);
            if (emp != null) {
                txtName.setText(emp.name); txtDesignation.setText(emp.designation);
                refreshTable(); btnPay.setEnabled(true);
            } else clearForm();
        });

        btnPay.addActionListener(e -> {
            String id = txtSearch.getText().trim();
            String amountStr = txtAmount.getText().trim();
            String monthStr = cmbMonth.getSelectedItem() + "-" + cmbYear.getSelectedItem();

            if (controller.handleSalaryPayment(id, monthStr, amountStr, this)) {
                txtAmount.setText("");
                refreshTable();
            }
        });

        btnClear.addActionListener(e -> clearForm());
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Object[]> history = controller.getSalaryHistory(txtSearch.getText().trim());
        for(Object[] row : history) model.addRow(row);
    }

    private JTextField createField() { JTextField f = new JTextField(); f.setPreferredSize(new Dimension(250, 35)); f.setFont(new Font("SansSerif", Font.PLAIN, 14)); return f; }
    private void addLabel(JPanel p, String t, int x, int y) { JLabel l = new JLabel(t); l.setFont(new Font("SansSerif", Font.BOLD, 14)); GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(12, 10, 12, 5); p.add(l, g); }
    private GridBagConstraints getGbc(int x, int y) { GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0; g.insets = new Insets(12, 5, 12, 10); return g; }
    private void clearForm() { txtSearch.setText(""); txtName.setText(""); txtDesignation.setText(""); txtAmount.setText(""); model.setRowCount(0); btnPay.setEnabled(false); }
}