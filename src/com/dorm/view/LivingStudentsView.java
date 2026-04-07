package com.dorm.view;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Student;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * View class to display records of students currently living in the dormitory.
 * Includes digit-only validation for the search field.
 */
public class LivingStudentsView extends JPanel {
    private JTextField txtSearch;
    private JTable studentTable;
    private DefaultTableModel model;
    private DormitoryDAO dao = new DormitoryDAO();

    public LivingStudentsView() {
        // Main Setup
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // Centering Wrapper
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        // --- MAIN WHITE CARD ---
        JPanel mainCard = new JPanel(new BorderLayout());
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));
        mainCard.setPreferredSize(new Dimension(1000, 600));

        // --- TOP SECTION: Header & Search ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("LIVING STUDENTS INFORMATION");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(new Color(153, 153, 255));
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("Search by Student ID: ");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 14));

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // NEW: Validation to allow only digits in the search field
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume(); // Ignore non-digit characters
                }
            }
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        mainCard.add(topPanel, BorderLayout.NORTH);

        // --- CENTER SECTION: Students Table ---
        String[] columns = {"Name", "Father Name", "Phone", "Email", "Uni ID", "Room No", "Degree"};
        model = new DefaultTableModel(columns, 0);
        studentTable = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Read-only table
        };
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane tableScroll = new JScrollPane(studentTable);
        mainCard.add(tableScroll, BorderLayout.CENTER);

        wrapper.add(mainCard);

        // --- FINAL SCROLLPANE (For Responsive Behavior) ---
        JScrollPane mainScroll = new JScrollPane(wrapper);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        // --- LOGIC ---

        // Initial load of all living students
        refreshTable(dao.getAllLivingStudents());

        // Dynamic search logic: triggers as you type
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String query = txtSearch.getText().trim();
                if (query.isEmpty()) {
                    refreshTable(dao.getAllLivingStudents());
                } else {
                    refreshTable(dao.searchLivingStudents(query));
                }
            }
        });
    }

    /**
     * Helper to refresh table data.
     * @param list List of Student objects to display.
     */
    private void refreshTable(List<Student> list) {
        model.setRowCount(0);
        for (Student s : list) {
            model.addRow(new Object[]{
                    s.name, s.fatherName, s.phone, s.email, s.uniID, s.roomNo, s.degree
            });
        }
    }
}