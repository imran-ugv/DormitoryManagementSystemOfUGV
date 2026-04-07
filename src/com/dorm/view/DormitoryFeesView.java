package com.dorm.view;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Fee;
import com.dorm.model.Student;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Professional Dormitory Fees Management View.
 * Balanced spacing, numeric-only validation for ID and Amount, and responsive layout.
 */
public class DormitoryFeesView extends JPanel {
    private JTextField txtSearch, txtName, txtPhone, txtRoom, txtAmountPaid, txtTotalFee, txtDue;
    private JComboBox<String> cmbMonth, cmbYear;
    private JButton btnSearch, btnPay, btnClear;
    private JTable feeTable;
    private DefaultTableModel model;
    private DormitoryDAO dao = new DormitoryDAO();

    public DormitoryFeesView() {
        // Background and Main Layout
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // Centering Wrapper
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        // --- MAIN WHITE CARD ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 15, 12, 15);

        // --- 1. HEADER SECTION ---
        JLabel lblHeader = new JLabel("DORMITORY FEES MANAGEMENT");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);
        formContainer.add(lblHeader, gbc);

        // --- 2. SEARCH BAR SECTION ---
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 25, 10);
        addLabel(formContainer, "Student ID:", 0, 1);

        txtSearch = createField();
        // Validation: Only allow digits in Search field
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        });
        formContainer.add(txtSearch, getGbc(1, 1));

        btnSearch = new JButton("🔍 Search Student");
        btnSearch.setPreferredSize(new Dimension(180, 35));
        btnSearch.setBackground(new Color(153, 153, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 13));
        formContainer.add(btnSearch, getGbc(2, 1));

        // Separator
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 30, 0);
        formContainer.add(new JSeparator(), gbc);

        // --- 3. INPUT FIELDS (Two Columns) ---
        gbc.gridwidth = 1;
        gbc.insets = new Insets(12, 10, 12, 10);
        int r = 3;

        // Left Column
        addLabel(formContainer, "Student Name:", 0, r);
        txtName = createField(); txtName.setEditable(false);
        txtName.setBackground(new Color(245, 245, 245));
        formContainer.add(txtName, getGbc(1, r++));

        addLabel(formContainer, "Phone Number:", 0, r);
        txtPhone = createField(); txtPhone.setEditable(false);
        txtPhone.setBackground(new Color(245, 245, 245));
        formContainer.add(txtPhone, getGbc(1, r++));

        addLabel(formContainer, "Room Number:", 0, r);
        txtRoom = createField(); txtRoom.setEditable(false);
        txtRoom.setBackground(new Color(245, 245, 245));
        formContainer.add(txtRoom, getGbc(1, r++));

        // Right Column (Reset row to 3)
        int rightR = 3;
        addLabel(formContainer, "Payment Month:", 2, rightR);
        JPanel datePnl = new JPanel(new GridLayout(1, 2, 10, 0));
        datePnl.setOpaque(false);
        cmbMonth = new JComboBox<>(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        cmbYear = new JComboBox<>(new String[]{"2024", "2025", "2026"});
        datePnl.add(cmbMonth); datePnl.add(cmbYear);
        formContainer.add(datePnl, getGbc(3, rightR++));

        addLabel(formContainer, "Monthly Fee:", 2, rightR);
        txtTotalFee = createField(); txtTotalFee.setText("2500"); txtTotalFee.setEditable(false);
        txtTotalFee.setBackground(new Color(245, 245, 245));
        formContainer.add(txtTotalFee, getGbc(3, rightR++));

        addLabel(formContainer, "Amount to Pay:", 2, rightR);
        txtAmountPaid = createField();
        // Validation: Only digits and one decimal point allowed
        txtAmountPaid.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.') e.consume();
            }
        });
        formContainer.add(txtAmountPaid, getGbc(3, rightR++));

        addLabel(formContainer, "Current Due:", 2, rightR);
        txtDue = createField(); txtDue.setEditable(false);
        txtDue.setBackground(new Color(245, 245, 245));
        formContainer.add(txtDue, getGbc(3, rightR++));

        // --- 4. PAYMENT HISTORY TABLE ---
        gbc.gridx = 0; gbc.gridy = Math.max(r, rightR); gbc.gridwidth = 4;
        gbc.insets = new Insets(30, 0, 10, 0);
        JLabel lblTable = new JLabel("PAYMENT HISTORY", SwingConstants.CENTER);
        lblTable.setFont(new Font("SansSerif", Font.BOLD, 16));
        formContainer.add(lblTable, gbc);

        model = new DefaultTableModel(new String[]{"Month-Year", "Total Fee", "Paid", "Due", "Date"}, 0);
        feeTable = new JTable(model);
        feeTable.setRowHeight(25);
        JScrollPane tableScroll = new JScrollPane(feeTable);
        tableScroll.setPreferredSize(new Dimension(800, 150));
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        formContainer.add(tableScroll, gbc);

        // --- 5. ACTION BUTTONS ---
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPnl.setOpaque(false);

        btnPay = new JButton("Confirm Payment");
        btnPay.setBackground(new Color(39, 174, 96));
        btnPay.setForeground(Color.WHITE);
        btnPay.setPreferredSize(new Dimension(180, 45));
        btnPay.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnPay.setEnabled(false);

        btnClear = new JButton("Clear Form");
        btnClear.setPreferredSize(new Dimension(140, 45));
        btnClear.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnPnl.add(btnPay); btnPnl.add(btnClear);
        gbc.gridy++; gbc.insets = new Insets(30, 0, 0, 0);
        formContainer.add(btnPnl, gbc);

        wrapper.add(formContainer);

        // Final Scroll Pane for responsiveness
        JScrollPane mainScroll = new JScrollPane(wrapper);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        // --- BUTTON ACTIONS ---

        btnSearch.addActionListener(e -> {
            String id = txtSearch.getText().trim();
            if(id.isEmpty()) return;
            Student s = dao.getStudentByID(id);
            if (s != null) {
                txtName.setText(s.name); txtPhone.setText(s.phone); txtRoom.setText(s.roomNo);
                refreshDueAndTable();
                btnPay.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found!");
                clearForm();
            }
        });

        btnPay.addActionListener(e -> {
            String amountStr = txtAmountPaid.getText().trim();
            if(amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount to pay!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double payAmount = Double.parseDouble(amountStr);
                double currentDue = Double.parseDouble(txtDue.getText());

                if(payAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero!");
                    return;
                }

                if(payAmount > currentDue) {
                    JOptionPane.showMessageDialog(this, "Paying more than the current due is not allowed!");
                    return;
                }

                String monthStr = cmbMonth.getSelectedItem() + "-" + cmbYear.getSelectedItem();
                Fee f = new Fee(txtSearch.getText().trim(), monthStr, 2500.0, payAmount, currentDue - payAmount, "");

                if(dao.saveFee(f)) {
                    JOptionPane.showMessageDialog(this, "Payment Recorded Successfully!");
                    txtAmountPaid.setText("");
                    refreshDueAndTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid numeric amount!");
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
        double due = dao.getMonthDue(id, monthStr);
        txtDue.setText(String.valueOf(due));

        if (due <= 0) {
            btnPay.setEnabled(false);
            btnPay.setText("Paid for " + cmbMonth.getSelectedItem());
        } else {
            btnPay.setEnabled(true);
            btnPay.setText("Confirm Payment");
        }

        // Update Table
        model.setRowCount(0);
        List<Fee> history = dao.getPaymentHistory(id);
        for(Fee f : history) {
            model.addRow(new Object[]{f.month, f.total, f.paid, f.due, f.date});
        }
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(250, 35));
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return f;
    }

    private void addLabel(JPanel p, String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(12, 10, 12, 5);
        p.add(l, g);
    }

    private GridBagConstraints getGbc(int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.insets = new Insets(12, 5, 12, 10);
        return g;
    }

    private void clearForm() {
        txtSearch.setText(""); txtName.setText(""); txtPhone.setText(""); txtRoom.setText("");
        txtAmountPaid.setText(""); txtDue.setText(""); model.setRowCount(0);
        btnPay.setEnabled(false); btnPay.setText("Confirm Payment");
    }
}