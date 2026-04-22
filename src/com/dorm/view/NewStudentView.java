package com.dorm.view;

import com.dorm.controller.StudentController; // Import Controller
import com.dorm.model.Student;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;

public class NewStudentView extends JPanel {
    private JTextField txtName, txtFather, txtMother, txtPhone, txtEmail, txtUniID;
    private JFormattedTextField txtDOB;
    private JRadioButton rbMale, rbFemale;
    private JComboBox<String> cmbDegree, cmbRoom;
    private JTextArea txtAddress;
    private JButton btnSave, btnClear;

    // Using StudentController instead of direct DAO
    private StudentController controller = new StudentController();

    public NewStudentView() {
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
        JLabel lblHeader = new JLabel("NEW STUDENT REGISTRATION (UGV)");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 35, 0);
        formContainer.add(lblHeader, gbc);

        gbc.gridwidth = 1; int row = 1;
        gbc.insets = new Insets(12, 10, 12, 10);

        // --- LEFT COLUMN ---
        addLabel(formContainer, "<html>Full Name <font color='red'>*</font></html>", 0, row);
        txtName = createTextField(); formContainer.add(txtName, getGbc(1, row++));

        addLabel(formContainer, "Father Name:", 0, row);
        txtFather = createTextField(); formContainer.add(txtFather, getGbc(1, row++));

        addLabel(formContainer, "<html>Gender <font color='red'>*</font></html>", 0, row);
        JPanel genPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); genPnl.setOpaque(false);
        rbMale = new JRadioButton("Male"); rbFemale = new JRadioButton("Female"); rbMale.setSelected(true);
        ButtonGroup bg = new ButtonGroup(); bg.add(rbMale); bg.add(rbFemale);
        genPnl.add(rbMale); genPnl.add(rbFemale); formContainer.add(genPnl, getGbc(1, row++));

        addLabel(formContainer, "<html>Phone Number <font color='red'>*</font></html>", 0, row);
        txtPhone = createTextField();
        txtPhone.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }});
        formContainer.add(txtPhone, getGbc(1, row++));

        addLabel(formContainer, "Degree Program:", 0, row);
        cmbDegree = new JComboBox<>(new String[]{"Bachelor Programs", "Master Programs", "Diploma"});
        cmbDegree.setPreferredSize(new Dimension(250, 35)); formContainer.add(cmbDegree, getGbc(1, row++));

        addLabel(formContainer, "<html>Room No <font color='red'>*</font></html>", 0, row);
        cmbRoom = new JComboBox<>(); cmbRoom.setPreferredSize(new Dimension(250, 35));
        formContainer.add(cmbRoom, getGbc(1, row++));

        // --- RIGHT COLUMN ---
        int rRow = 1;
        addLabel(formContainer, "<html>Birth Date <font color='red'>*</font> (YYYY-MM-DD)</html>", 2, rRow);
        txtDOB = createDateField(); formContainer.add(txtDOB, getGbc(3, rRow++));

        addLabel(formContainer, "Mother Name:", 2, rRow);
        txtMother = createTextField(); formContainer.add(txtMother, getGbc(3, rRow++));

        addLabel(formContainer, "<html>Email Address <font color='red'>*</font></html>", 2, rRow);
        txtEmail = createTextField(); formContainer.add(txtEmail, getGbc(3, rRow++));

        addLabel(formContainer, "<html>University ID <font color='red'>*</font></html>", 2, rRow);
        txtUniID = createTextField();
        txtUniID.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }});
        formContainer.add(txtUniID, getGbc(3, rRow++));

        addLabel(formContainer, "Home Address:", 2, rRow);
        txtAddress = new JTextArea(3, 20); txtAddress.setBorder(new LineBorder(Color.LIGHT_GRAY));
        JScrollPane scroll = new JScrollPane(txtAddress);
        GridBagConstraints addrGbc = getGbc(3, rRow); addrGbc.gridheight = 2;
        formContainer.add(scroll, addrGbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); btnPanel.setOpaque(false);
        btnSave = new JButton("Save Record"); btnSave.setBackground(new Color(39, 174, 96)); btnSave.setForeground(Color.WHITE); btnSave.setPreferredSize(new Dimension(170, 45)); btnSave.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnClear = new JButton("Clear Form"); btnClear.setPreferredSize(new Dimension(140, 45)); btnClear.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnPanel.add(btnSave); btnPanel.add(btnClear);
        gbc.gridx = 0; gbc.gridy = Math.max(row, rRow) + 2; gbc.gridwidth = 4; gbc.insets = new Insets(50, 0, 0, 0);
        formContainer.add(btnPanel, gbc);

        wrapper.add(formContainer);
        JScrollPane mainScroll = new JScrollPane(wrapper); mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        loadRooms();

        // --- SAVE LOGIC: Delegating to Controller ---
        btnSave.addActionListener(e -> {
            Student s = new Student(
                    txtName.getText().trim(), txtFather.getText().trim(), txtMother.getText().trim(),
                    rbMale.isSelected()?"Male":"Female", txtDOB.getText().trim(), txtPhone.getText().trim(),
                    txtEmail.getText().trim(), txtUniID.getText().trim(), cmbDegree.getSelectedItem().toString(),
                    cmbRoom.getSelectedItem() == null ? "" : cmbRoom.getSelectedItem().toString(),
                    txtAddress.getText(), "Living"
            );

            if (controller.handleRegistration(s, txtDOB.getText().trim(), this)) {
                clearForm();
                loadRooms();
            }
        });

        btnClear.addActionListener(e -> clearForm());
    }

    private JTextField createTextField() { JTextField tf = new JTextField(); tf.setPreferredSize(new Dimension(250, 35)); tf.setFont(new Font("SansSerif", Font.PLAIN, 14)); return tf; }
    private JFormattedTextField createDateField() { try { MaskFormatter mf = new MaskFormatter("####-##-##"); mf.setPlaceholderCharacter('_'); JFormattedTextField ftf = new JFormattedTextField(mf); ftf.setPreferredSize(new Dimension(250, 35)); ftf.setFont(new Font("SansSerif", Font.PLAIN, 14)); return ftf; } catch (Exception e) { return new JFormattedTextField(); } }
    private void addLabel(JPanel p, String text, int x, int y) { JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.BOLD, 14)); GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(12, 10, 12, 5); p.add(l, g); }
    private GridBagConstraints getGbc(int x, int y) { GridBagConstraints g = new GridBagConstraints(); g.gridx = x; g.gridy = y; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0; g.insets = new Insets(12, 5, 12, 10); return g; }
    private void loadRooms() { cmbRoom.removeAllItems(); for (String r : controller.getAvailableRooms()) cmbRoom.addItem(r); }
    private void clearForm() { txtName.setText(""); txtFather.setText(""); txtMother.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtUniID.setText(""); txtDOB.setValue(null); txtAddress.setText(""); rbMale.setSelected(true); }
}