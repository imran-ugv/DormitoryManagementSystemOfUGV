package com.dorm.view;

import com.dorm.controller.EmployeeController; // Import Controller
import com.dorm.model.Employee;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;

public class ManageEmployeeView extends JPanel {
    private JTextField txtSearchID, txtName, txtFather, txtMother, txtPhone, txtEmail;
    private JFormattedTextField txtDOB;
    private JRadioButton rbMale, rbFemale;
    private JComboBox<String> cmbDesignation, cmbStatus;
    private JTextArea txtPresentAddr, txtPermanentAddr;
    private JButton btnSearch, btnUpdate, btnDelete, btnClear;

    // Using Controller instead of direct DAO
    private EmployeeController controller = new EmployeeController();

    public ManageEmployeeView() {
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1), new EmptyBorder(40, 50, 40, 50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(12, 15, 12, 15);

        JLabel lblHeader = new JLabel("MANAGE EMPLOYEE RECORDS");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 35, 0);
        formContainer.add(lblHeader, gbc);

        // Search Bar
        gbc.gridwidth = 1; gbc.insets = new Insets(10, 10, 25, 10);
        addLabel(formContainer, "Employee ID to Manage:", 0, 1);
        txtSearchID = createTextField();
        txtSearchID.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }});
        formContainer.add(txtSearchID, getGbc(1, 1));

        btnSearch = new JButton("🔍 Search Employee");
        btnSearch.setPreferredSize(new Dimension(180, 35));
        btnSearch.setBackground(new Color(153, 153, 255)); btnSearch.setForeground(Color.WHITE);
        formContainer.add(btnSearch, getGbc(2, 1));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.insets = new Insets(10, 0, 30, 0);
        formContainer.add(new JSeparator(), gbc);

        // Input Rows
        int row = 3; gbc.gridwidth = 1; gbc.insets = new Insets(12, 10, 12, 10);
        addLabel(formContainer, "Full Name:", 0, row);
        txtName = createTextField(); formContainer.add(txtName, getGbc(1, row++));
        addLabel(formContainer, "Father Name:", 0, row);
        txtFather = createTextField(); formContainer.add(txtFather, getGbc(1, row++));
        addLabel(formContainer, "Gender:", 0, row);
        JPanel genPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); genPnl.setOpaque(false);
        rbMale = new JRadioButton("Male"); rbFemale = new JRadioButton("Female");
        ButtonGroup bg = new ButtonGroup(); bg.add(rbMale); bg.add(rbFemale);
        genPnl.add(rbMale); genPnl.add(rbFemale); formContainer.add(genPnl, getGbc(1, row++));
        addLabel(formContainer, "Phone (Digits only):", 0, row);
        txtPhone = createTextField();
        txtPhone.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }});
        formContainer.add(txtPhone, getGbc(1, row++));
        addLabel(formContainer, "Present Address:", 0, row);
        txtPresentAddr = new JTextArea(3, 20); txtPresentAddr.setBorder(new LineBorder(Color.LIGHT_GRAY));
        formContainer.add(new JScrollPane(txtPresentAddr), getGbc(1, row));

        int rRow = 3;
        addLabel(formContainer, "Date of Birth:", 2, rRow);
        txtDOB = createDateField(); formContainer.add(txtDOB, getGbc(3, rRow++));
        addLabel(formContainer, "Mother Name:", 2, rRow);
        txtMother = createTextField(); formContainer.add(txtMother, getGbc(3, rRow++));
        addLabel(formContainer, "Email Address:", 2, rRow);
        txtEmail = createTextField(); formContainer.add(txtEmail, getGbc(3, rRow++));
        addLabel(formContainer, "Designation:", 2, rRow);
        cmbDesignation = new JComboBox<>(new String[]{"Accounting", "Chef", "Electrician", "Housekeeping", "Maintenance"});
        cmbDesignation.setPreferredSize(new Dimension(250, 35)); formContainer.add(cmbDesignation, getGbc(3, rRow++));
        addLabel(formContainer, "Working Status:", 2, rRow);
        cmbStatus = new JComboBox<>(new String[]{"Working", "Leaved"});
        cmbStatus.setPreferredSize(new Dimension(250, 35)); formContainer.add(cmbStatus, getGbc(3, rRow++));
        addLabel(formContainer, "Permanent Address:", 2, rRow);
        txtPermanentAddr = new JTextArea(3, 20); txtPermanentAddr.setBorder(new LineBorder(Color.LIGHT_GRAY));
        GridBagConstraints addrGbc = getGbc(3, rRow); addrGbc.gridheight = 2;
        formContainer.add(new JScrollPane(txtPermanentAddr), addrGbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); btnPanel.setOpaque(false);
        btnUpdate = new JButton("Update Record"); btnUpdate.setBackground(new Color(39, 174, 96)); btnUpdate.setForeground(Color.WHITE); btnUpdate.setPreferredSize(new Dimension(170, 45));
        btnDelete = new JButton("Delete Employee"); btnDelete.setBackground(new Color(255, 102, 102)); btnDelete.setForeground(Color.WHITE); btnDelete.setPreferredSize(new Dimension(170, 45));
        btnClear = new JButton("Clear Form"); btnClear.setPreferredSize(new Dimension(140, 45));
        btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        gbc.gridx = 0; gbc.gridy = Math.max(row, rRow) + 2; gbc.gridwidth = 4; gbc.insets = new Insets(50, 0, 0, 0);
        formContainer.add(btnPanel, gbc);

        wrapper.add(formContainer);
        JScrollPane mainScroll = new JScrollPane(wrapper); mainScroll.setBorder(null);
        add(mainScroll, BorderLayout.CENTER);

        // --- BUTTON ACTIONS: Delegating to Controller ---

        btnSearch.addActionListener(e -> {
            Employee emp = controller.handleSearch(txtSearchID.getText().trim(), this);
            if (emp != null) {
                txtName.setText(emp.name); txtFather.setText(emp.fatherName); txtMother.setText(emp.motherName);
                txtPhone.setText(emp.phone); txtEmail.setText(emp.email); txtDOB.setText(emp.dob);
                txtPresentAddr.setText(emp.presentAddr); txtPermanentAddr.setText(emp.permanentAddr);
                cmbDesignation.setSelectedItem(emp.designation); cmbStatus.setSelectedItem(emp.status);
                if (emp.gender.equalsIgnoreCase("Male")) rbMale.setSelected(true); else rbFemale.setSelected(true);
            } else clearForm();
        });

        btnUpdate.addActionListener(e -> {
            if (txtSearchID.getText().trim().isEmpty()) return;
            Employee updated = new Employee(txtSearchID.getText().trim(), txtName.getText(), txtFather.getText(), txtMother.getText(), rbMale.isSelected()?"Male":"Female", txtDOB.getText().trim(), txtPhone.getText(), txtEmail.getText().trim(), cmbDesignation.getSelectedItem().toString(), txtPresentAddr.getText(), txtPermanentAddr.getText(), cmbStatus.getSelectedItem().toString());

            if (controller.handleUpdate(updated, txtDOB.getText().trim(), this)) {
                clearForm();
            }
        });

        btnDelete.addActionListener(e -> {
            if (controller.handleDelete(txtSearchID.getText().trim(), this)) {
                clearForm();
            }
        });

        btnClear.addActionListener(e -> clearForm());
    }

    private JTextField createTextField() { JTextField tf = new JTextField(); tf.setPreferredSize(new Dimension(250, 35)); tf.setFont(new Font("SansSerif", Font.PLAIN, 14)); return tf; }
    private JFormattedTextField createDateField() { try { MaskFormatter mf = new MaskFormatter("####-##-##"); mf.setPlaceholderCharacter('_'); JFormattedTextField ftf = new JFormattedTextField(mf); ftf.setPreferredSize(new Dimension(250, 35)); ftf.setFont(new Font("SansSerif", Font.PLAIN, 14)); return ftf; } catch (Exception e) { return new JFormattedTextField(); } }
    private void addLabel(JPanel p, String text, int x, int y) { JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.BOLD, 14)); GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(12, 10, 12, 5); p.add(l, g); }
    private GridBagConstraints getGbc(int x, int y) { GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0; g.insets = new Insets(12, 5, 12, 10); return g; }
    private void clearForm() { txtSearchID.setText(""); txtName.setText(""); txtFather.setText(""); txtMother.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtDOB.setValue(null); txtPresentAddr.setText(""); txtPermanentAddr.setText(""); rbMale.setSelected(true); cmbStatus.setSelectedIndex(0); }
}