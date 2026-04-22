package com.dorm.view;

import com.dorm.controller.StudentController; // Import Controller
import com.dorm.model.Student;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;

public class ManageStudentView extends JPanel {
    private JTextField txtSearchID, txtName, txtFather, txtMother, txtPhone, txtEmail;
    private JFormattedTextField txtDOB;
    private JRadioButton rbMale, rbFemale;
    private JComboBox<String> cmbDegree, cmbRoom, cmbStatus;
    private JTextArea txtAddress;
    private JButton btnSearch, btnUpdate, btnDelete, btnClear;

    // Using StudentController for all logic
    private StudentController controller = new StudentController();
    private String currentRoomInDB = "", currentStatusInDB = "";

    public ManageStudentView() {
        // Layout and Background Setup
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
        JLabel lblHeader = new JLabel("MANAGE STUDENT RECORDS");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 35, 0);
        formContainer.add(lblHeader, gbc);

        // Search Bar
        gbc.gridwidth = 1; gbc.insets = new Insets(10, 10, 25, 10);
        addLabel(formContainer, "University ID to Manage:", 0, 1);
        txtSearchID = createTextField();
        txtSearchID.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        });
        formContainer.add(txtSearchID, getGbc(1, 1));

        btnSearch = new JButton("🔍 Search Student");
        btnSearch.setPreferredSize(new Dimension(180, 35));
        btnSearch.setBackground(new Color(153, 153, 255)); btnSearch.setForeground(Color.WHITE);
        formContainer.add(btnSearch, getGbc(2, 1));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 30, 0);
        formContainer.add(new JSeparator(), gbc);

        // Form Fields (Left Column)
        int row = 3; gbc.gridwidth = 1; gbc.insets = new Insets(12, 10, 12, 10);
        addLabel(formContainer, "Full Name:", 0, row);
        txtName = createTextField(); formContainer.add(txtName, getGbc(1, row++));
        addLabel(formContainer, "Father Name:", 0, row);
        txtFather = createTextField(); formContainer.add(txtFather, getGbc(1, row++));
        addLabel(formContainer, "Gender:", 0, row);
        JPanel genPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); genPnl.setOpaque(false);
        rbMale = new JRadioButton("Male"); rbFemale = new JRadioButton("Female");
        rbMale.setOpaque(false); rbFemale.setOpaque(false);
        ButtonGroup bg = new ButtonGroup(); bg.add(rbMale); bg.add(rbFemale);
        genPnl.add(rbMale); genPnl.add(rbFemale); formContainer.add(genPnl, getGbc(1, row++));
        addLabel(formContainer, "Phone:", 0, row);
        txtPhone = createTextField();
        txtPhone.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }});
        formContainer.add(txtPhone, getGbc(1, row++));
        addLabel(formContainer, "Room Number:", 0, row);
        cmbRoom = new JComboBox<>(); cmbRoom.setPreferredSize(new Dimension(250, 35));
        formContainer.add(cmbRoom, getGbc(1, row++));

        // Right Column
        int rightRow = 3;
        addLabel(formContainer, "Date of Birth:", 2, rightRow);
        txtDOB = createDateField(); formContainer.add(txtDOB, getGbc(3, rightRow++));
        addLabel(formContainer, "Mother Name:", 2, rightRow);
        txtMother = createTextField(); formContainer.add(txtMother, getGbc(3, rightRow++));
        addLabel(formContainer, "Email Address:", 2, rightRow);
        txtEmail = createTextField(); formContainer.add(txtEmail, getGbc(3, rightRow++));
        addLabel(formContainer, "Degree Program:", 2, rightRow);
        cmbDegree = new JComboBox<>(new String[]{"Bachelor Programs", "Master Programs", "Diploma"});
        cmbDegree.setPreferredSize(new Dimension(250, 35)); formContainer.add(cmbDegree, getGbc(3, rightRow++));
        addLabel(formContainer, "Living Status:", 2, rightRow);
        cmbStatus = new JComboBox<>(new String[]{"Living", "Leaved"});
        cmbStatus.setPreferredSize(new Dimension(250, 35)); formContainer.add(cmbStatus, getGbc(3, rightRow++));
        addLabel(formContainer, "Home Address:", 2, rightRow);
        txtAddress = new JTextArea(3, 20); txtAddress.setBorder(new LineBorder(Color.LIGHT_GRAY));
        JScrollPane scroll = new JScrollPane(txtAddress);
        GridBagConstraints addrGbc = getGbc(3, rightRow); addrGbc.gridheight = 2;
        formContainer.add(scroll, addrGbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); btnPanel.setOpaque(false);
        btnUpdate = new JButton("Update Record"); btnUpdate.setBackground(new Color(39, 174, 96)); btnUpdate.setForeground(Color.WHITE); btnUpdate.setPreferredSize(new Dimension(170, 45));
        btnDelete = new JButton("Delete Student"); btnDelete.setBackground(new Color(255, 102, 102)); btnDelete.setForeground(Color.WHITE); btnDelete.setPreferredSize(new Dimension(170, 45));
        btnClear = new JButton("Clear Form"); btnClear.setPreferredSize(new Dimension(140, 45));
        btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        gbc.gridx = 0; gbc.gridy = Math.max(row, rightRow) + 2; gbc.gridwidth = 4; gbc.insets = new Insets(50, 0, 0, 0);
        formContainer.add(btnPanel, gbc);

        wrapper.add(formContainer);
        JScrollPane mainScroll = new JScrollPane(wrapper); mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        // --- BUTTON ACTIONS: Delegating to Controller ---

        btnSearch.addActionListener(e -> {
            String id = txtSearchID.getText().trim();
            Student s = controller.handleSearch(id, this);
            if (s != null) {
                txtName.setText(s.name); txtFather.setText(s.fatherName); txtMother.setText(s.motherName);
                txtPhone.setText(s.phone); txtEmail.setText(s.email); txtDOB.setText(s.dob);
                txtAddress.setText(s.address); cmbDegree.setSelectedItem(s.degree);
                cmbStatus.setSelectedItem(s.status);
                currentRoomInDB = s.roomNo; currentStatusInDB = s.status;

                // Refresh room dropdown
                cmbRoom.removeAllItems(); cmbRoom.addItem(s.roomNo);
                for (String r : controller.getAvailableRooms()) cmbRoom.addItem(r);
                if (s.gender.equalsIgnoreCase("Male")) rbMale.setSelected(true); else rbFemale.setSelected(true);
            } else clearForm();
        });

        btnUpdate.addActionListener(e -> {
            if (txtSearchID.getText().trim().isEmpty()) return;
            Student s = new Student(txtName.getText(), txtFather.getText(), txtMother.getText(), rbMale.isSelected()?"Male":"Female",
                    txtDOB.getText().trim(), txtPhone.getText(), txtEmail.getText(), txtSearchID.getText().trim(),
                    cmbDegree.getSelectedItem().toString(), cmbRoom.getSelectedItem().toString(),
                    txtAddress.getText(), cmbStatus.getSelectedItem().toString());

            if (controller.handleUpdate(s, currentRoomInDB, currentStatusInDB, txtDOB.getText().trim(), this)) {
                clearForm();
            }
        });

        btnDelete.addActionListener(e -> {
            if (controller.handleDelete(txtSearchID.getText().trim(), currentRoomInDB, this)) {
                clearForm();
            }
        });

        btnClear.addActionListener(e -> clearForm());
    }

    private JTextField createTextField() { JTextField tf = new JTextField(); tf.setPreferredSize(new Dimension(250, 35)); tf.setFont(new Font("SansSerif", Font.PLAIN, 14)); return tf; }
    private JFormattedTextField createDateField() { try { MaskFormatter mf = new MaskFormatter("####-##-##"); mf.setPlaceholderCharacter('_'); JFormattedTextField ftf = new JFormattedTextField(mf); ftf.setPreferredSize(new Dimension(250, 35)); ftf.setFont(new Font("SansSerif", Font.PLAIN, 14)); return ftf; } catch (Exception e) { return new JFormattedTextField(); } }
    private void addLabel(JPanel p, String text, int x, int y) { JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.BOLD, 14)); GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(12, 10, 12, 5); p.add(l, g); }
    private GridBagConstraints getGbc(int x, int y) { GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0; g.insets = new Insets(12, 5, 12, 10); return g; }
    private void clearForm() { txtSearchID.setText(""); txtName.setText(""); txtFather.setText(""); txtMother.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtDOB.setText(""); txtAddress.setText(""); if (cmbRoom != null) cmbRoom.removeAllItems(); rbMale.setSelected(true); }
}