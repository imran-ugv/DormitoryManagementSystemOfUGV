package com.dorm.view;

import com.dorm.controller.FeesController; // Import Controller
import com.dorm.model.Fee;
import com.dorm.model.Student;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class DormitoryFeesView extends JPanel {
    private JTextField txtSearch, txtName, txtPhone, txtRoom, txtAmountPaid, txtTotalFee, txtDue;
    private JComboBox<String> cmbMonth, cmbYear;
    private JButton btnSearch, btnPay, btnClear;
    private JTable feeTable;
    private DefaultTableModel model;

    // Using FeesController for logic handling
    private FeesController controller = new FeesController();

    public DormitoryFeesView() {
        // Layout and Spacing Setup
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1), new EmptyBorder(40, 50, 40, 50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 15, 12, 15);

        // Header
        JLabel lblHeader = new JLabel("DORMITORY FEES MANAGEMENT");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 26)); lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0); formContainer.add(lblHeader, gbc);

        // Search Bar
        gbc.gridwidth = 1; gbc.insets = new Insets(10, 10, 25, 10);
        addLabel(formContainer, "Student ID:", 0, 1);
        txtSearch = createField();
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        });
        formContainer.add(txtSearch, getGbc(1, 1));
        btnSearch = new JButton("🔍 Search Student");
        btnSearch.setPreferredSize(new Dimension(180, 35)); btnSearch.setBackground(new Color(153, 153, 255)); btnSearch.setForeground(Color.WHITE);
        formContainer.add(btnSearch, getGbc(2, 1));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.insets = new Insets(10, 0, 30, 0);
        formContainer.add(new JSeparator(), gbc);

        // Input Rows
        gbc.gridwidth = 1; gbc.insets = new Insets(12, 10, 12, 10);
        int row = 3;
        addLabel(formContainer, "Student Name:", 0, row); txtName = createField(); txtName.setEditable(false); formContainer.add(txtName, getGbc(1, row++));
        addLabel(formContainer, "Phone Number:", 0, row); txtPhone = createField(); txtPhone.setEditable(false); formContainer.add(txtPhone, getGbc(1, row++));
        addLabel(formContainer, "Room Number:", 0, row); txtRoom = createField(); txtRoom.setEditable(false); formContainer.add(txtRoom, getGbc(1, row++));

        int rRow = 3;
        addLabel(formContainer, "Payment Month:", 2, rRow);
        JPanel datePnl = new JPanel(new GridLayout(1, 2, 10, 0)); datePnl.setOpaque(false);
        cmbMonth = new JComboBox<>(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        cmbYear = new JComboBox<>(new String[]{"2024", "2025", "2026"});
        datePnl.add(cmbMonth); datePnl.add(cmbYear); formContainer.add(datePnl, getGbc(3, rRow++));
        addLabel(formContainer, "Monthly Fee:", 2, rRow); txtTotalFee = createField(); txtTotalFee.setText("2500"); txtTotalFee.setEditable(false); formContainer.add(txtTotalFee, getGbc(3, rRow++));
        addLabel(formContainer, "Amount to Pay:", 2, rRow); txtAmountPaid = createField();
        txtAmountPaid.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { char c = e.getKeyChar(); if (!Character.isDigit(c) && c != '.') e.consume(); }});
        formContainer.add(txtAmountPaid, getGbc(3, rRow++));
        addLabel(formContainer, "Current Due:", 2, rRow); txtDue = createField(); txtDue.setEditable(false); formContainer.add(txtDue, getGbc(3, rRow++));

        // History Table
        model = new DefaultTableModel(new String[]{"Month", "Total Fee", "Paid", "Due", "Date"}, 0);
        feeTable = new JTable(model) { public boolean isCellEditable(int r, int c) { return false; } };
        feeTable.setRowHeight(25); JScrollPane tableScroll = new JScrollPane(feeTable);
        tableScroll.setPreferredSize(new Dimension(800, 150));
        gbc.gridx = 0; gbc.gridy = Math.max(row, rRow) + 1; gbc.gridwidth = 4; gbc.insets = new Insets(30, 0, 10, 0);
        formContainer.add(tableScroll, gbc);

        // Buttons
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); btnPnl.setOpaque(false);
        btnPay = new JButton("Confirm Payment"); btnPay.setBackground(new Color(39, 174, 96)); btnPay.setForeground(Color.WHITE); btnPay.setPreferredSize(new Dimension(180, 45)); btnPay.setEnabled(false);
        btnClear = new JButton("Clear Form"); btnClear.setPreferredSize(new Dimension(140, 45));
        btnPnl.add(btnPay); btnPnl.add(btnClear);
        gbc.gridy = Math.max(row, rRow) + 2; gbc.insets = new Insets(30, 0, 0, 0);
        formContainer.add(btnPnl, gbc);

        wrapper.add(formContainer);
        JScrollPane mainScroll = new JScrollPane(wrapper); mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        // --- ACTIONS: Delegating to Controller ---

        btnSearch.addActionListener(e -> {
            Student s = controller.handleSearch(txtSearch.getText().trim(), this);
            if (s != null) {
                txtName.setText(s.name); txtPhone.setText(s.phone); txtRoom.setText(s.roomNo);
                refreshDueAndTable();
                btnPay.setEnabled(true);
            } else clearForm();
        });

        btnPay.addActionListener(e -> {
            String id = txtSearch.getText().trim();
            String amountStr = txtAmountPaid.getText().trim();
            if(amountStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter an amount!", "Input Error", JOptionPane.WARNING_MESSAGE); return; }

            double payAmount = Double.parseDouble(amountStr);
            double currentDue = Double.parseDouble(txtDue.getText());
            String monthStr = cmbMonth.getSelectedItem() + "-" + cmbYear.getSelectedItem();

            Fee fee = new Fee(id, monthStr, 2500.0, payAmount, currentDue - payAmount, "");

            if (controller.handlePayment(fee, currentDue, this)) {
                txtAmountPaid.setText("");
                refreshDueAndTable();
            }
        });

        cmbMonth.addActionListener(e -> refreshDueAndTable());
        cmbYear.addActionListener(e -> refreshDueAndTable());
        btnClear.addActionListener(e -> clearForm());
    }

    private void refreshDueAndTable() {
        String id = txtSearch.getText().trim();
        if(id.isEmpty()) return;

        String monthStr = cmbMonth.getSelectedItem() + "-" + cmbYear.getSelectedItem();
        double due = controller.getDueAmount(id, monthStr);
        txtDue.setText(String.valueOf(due));

        if (due <= 0) { btnPay.setEnabled(false); btnPay.setText("Paid for " + cmbMonth.getSelectedItem()); }
        else { btnPay.setEnabled(true); btnPay.setText("Confirm Payment"); }

        model.setRowCount(0);
        for(Fee f : controller.getPaymentHistory(id)) model.addRow(new Object[]{f.month, f.total, f.paid, f.due, f.date});
    }

    private JTextField createField() { JTextField f = new JTextField(); f.setPreferredSize(new Dimension(250, 35)); f.setFont(new Font("SansSerif", Font.PLAIN, 14)); return f; }
    private void addLabel(JPanel p, String t, int x, int y) { JLabel l = new JLabel(t); l.setFont(new Font("SansSerif", Font.BOLD, 14)); GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(12, 10, 12, 5); p.add(l, g); }
    private GridBagConstraints getGbc(int x, int y) { GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0; g.insets = new Insets(12, 5, 12, 10); return g; }
    private void clearForm() { txtSearch.setText(""); txtName.setText(""); txtPhone.setText(""); txtRoom.setText(""); txtAmountPaid.setText(""); txtDue.setText(""); model.setRowCount(0); btnPay.setEnabled(false); btnPay.setText("Confirm Payment"); }
}