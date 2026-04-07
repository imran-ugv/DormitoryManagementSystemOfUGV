package com.dorm.view;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Student;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Professional New Student Registration View.
 * Balanced spacing, date masking, and strict input validation (Digits only for Phone/ID).
 */
public class NewStudentView extends JPanel {
    private JTextField txtName, txtFather, txtMother, txtPhone, txtEmail, txtUniID;
    private JFormattedTextField txtDOB; // Date field with masking
    private JRadioButton rbMale, rbFemale;
    private JComboBox<String> cmbDegree, cmbRoom;
    private JTextArea txtAddress;
    private JButton btnSave, btnClear;
    private DormitoryDAO dao = new DormitoryDAO();

    public NewStudentView() {
        // Main Background Setup
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // Centering Wrapper
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        // --- MAIN WHITE CARD CONTAINER ---
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
        JLabel lblHeader = new JLabel("NEW STUDENT REGISTRATION (UGV)");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 35, 0);
        formContainer.add(lblHeader, gbc);

        // --- 2. INPUT FORM FIELDS (Two Columns) ---
        gbc.gridwidth = 1;
        gbc.insets = new Insets(12, 10, 12, 10);
        int row = 1;

        // --- Left Column ---
        addLabel(formContainer, "<html>Full Name <font color='red'>*</font></html>", 0, row);
        txtName = createTextField();
        formContainer.add(txtName, getGbc(1, row++));

        addLabel(formContainer, "Father Name:", 0, row);
        txtFather = createTextField();
        formContainer.add(txtFather, getGbc(1, row++));

        addLabel(formContainer, "<html>Gender <font color='red'>*</font></html>", 0, row);
        JPanel genPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genPnl.setOpaque(false);
        rbMale = new JRadioButton("Male"); rbFemale = new JRadioButton("Female");
        rbMale.setOpaque(false); rbFemale.setOpaque(false); rbMale.setSelected(true);
        ButtonGroup bg = new ButtonGroup(); bg.add(rbMale); bg.add(rbFemale);
        genPnl.add(rbMale); genPnl.add(rbFemale);
        formContainer.add(genPnl, getGbc(1, row++));

        // Phone Validation: Only allow digits
        addLabel(formContainer, "<html>Phone Number <font color='red'>*</font></html>", 0, row);
        txtPhone = createTextField();
        txtPhone.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        });
        formContainer.add(txtPhone, getGbc(1, row++));

        addLabel(formContainer, "Degree Program:", 0, row);
        cmbDegree = new JComboBox<>(new String[]{"Bachelor Programs", "Master Programs", "Diploma"});
        cmbDegree.setPreferredSize(new Dimension(250, 35));
        formContainer.add(cmbDegree, getGbc(1, row++));

        addLabel(formContainer, "<html>Room No <font color='red'>*</font></html>", 0, row);
        cmbRoom = new JComboBox<>();
        cmbRoom.setPreferredSize(new Dimension(250, 35));
        formContainer.add(cmbRoom, getGbc(1, row++));

        // --- Right Column ---
        int rRow = 1;
        addLabel(formContainer, "<html>Birth Date <font color='red'>*</font> (YYYY-MM-DD)</html>", 2, rRow);
        txtDOB = createDateField(); // MASKED INPUT
        formContainer.add(txtDOB, getGbc(3, rRow++));

        addLabel(formContainer, "Mother Name:", 2, rRow);
        txtMother = createTextField();
        formContainer.add(txtMother, getGbc(3, rRow++));

        addLabel(formContainer, "<html>Email Address <font color='red'>*</font></html>", 2, rRow);
        txtEmail = createTextField();
        formContainer.add(txtEmail, getGbc(3, rRow++));

        // NEW: University ID Validation (Only digits allowed)
        addLabel(formContainer, "<html>University ID <font color='red'>*</font></html>", 2, rRow);
        txtUniID = createTextField();
        txtUniID.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        });
        formContainer.add(txtUniID, getGbc(3, rRow++));

        addLabel(formContainer, "Home Address:", 2, rRow);
        txtAddress = new JTextArea(3, 20);
        txtAddress.setBorder(new LineBorder(Color.LIGHT_GRAY));
        txtAddress.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(txtAddress);
        GridBagConstraints addrGbc = getGbc(3, rRow);
        addrGbc.gridheight = 2; // Address spans two rows
        formContainer.add(scroll, addrGbc);

        // --- 3. ACTION BUTTONS SECTION ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setOpaque(false);

        btnSave = new JButton("Save Record");
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(170, 45));
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnClear = new JButton("Clear Form");
        btnClear.setPreferredSize(new Dimension(140, 45));
        btnClear.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnPanel.add(btnSave); btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = Math.max(row, rRow) + 2;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(50, 0, 0, 0);
        formContainer.add(btnPanel, gbc);

        wrapper.add(formContainer);

        // --- FINAL SCROLLPANE INTEGRATION ---
        JScrollPane mainScroll = new JScrollPane(wrapper);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        // Load available rooms from database
        loadRooms();

        // --- SAVE LOGIC ---
        btnSave.addActionListener(e -> {
            String dob = txtDOB.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String uniID = txtUniID.getText().trim();

            if (dob.contains("_") || txtName.getText().isEmpty() || phone.isEmpty() || email.isEmpty() || uniID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields (*)", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try { LocalDate.parse(dob); } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Birth Date!", "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
                JOptionPane.showMessageDialog(this, "Invalid Email Format!", "Email Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student s = new Student(txtName.getText(), txtFather.getText(), txtMother.getText(),
                    rbMale.isSelected()?"Male":"Female", dob, phone, email,
                    uniID, cmbDegree.getSelectedItem().toString(),
                    cmbRoom.getSelectedItem().toString(), txtAddress.getText(), "Living");

            if (dao.saveStudent(s)) {
                JOptionPane.showMessageDialog(this, "Student Successfully Registered!");
                clearForm();
                loadRooms();
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed! Phone or ID Card might already exist.");
            }
        });

        btnClear.addActionListener(e -> clearForm());
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(250, 35));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return tf;
    }

    private JFormattedTextField createDateField() {
        try {
            MaskFormatter mf = new MaskFormatter("####-##-##");
            mf.setPlaceholderCharacter('_');
            JFormattedTextField ftf = new JFormattedTextField(mf);
            ftf.setPreferredSize(new Dimension(250, 35));
            ftf.setFont(new Font("SansSerif", Font.PLAIN, 14));
            return ftf;
        } catch (Exception e) { return new JFormattedTextField(); }
    }

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
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

    private void loadRooms() {
        cmbRoom.removeAllItems();
        List<String> list = dao.getAvailableRooms();
        for (String r : list) cmbRoom.addItem(r);
    }

    private void clearForm() {
        txtName.setText(""); txtFather.setText(""); txtMother.setText("");
        txtPhone.setText(""); txtEmail.setText(""); txtUniID.setText("");
        txtDOB.setValue(null); txtAddress.setText("");
        rbMale.setSelected(true);
    }
}