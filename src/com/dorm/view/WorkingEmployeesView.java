package com.dorm.view;

import com.dorm.controller.EmployeeController; // Import Controller
import com.dorm.model.Employee;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * View class to display records of currently working employees.
 * Strictly follows MVC by delegating data fetching to EmployeeController.
 */
public class WorkingEmployeesView extends JPanel {
    private JTextField txtSearch;
    private JTable employeeTable;
    private DefaultTableModel model;

    // Using EmployeeController instead of direct DAO access
    private EmployeeController controller = new EmployeeController();

    public WorkingEmployeesView() {
        // Main Background Setup
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

        JLabel lblTitle = new JLabel("CURRENT WORKING EMPLOYEES");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(new Color(153, 153, 255));
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("Search by Employee ID: ");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 14));

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Validation: Allow only digits in the search field
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume(); // Discard non-digit characters
                }
            }
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        mainCard.add(topPanel, BorderLayout.NORTH);

        // --- CENTER SECTION: Employee Table ---
        String[] columns = {"ID", "Name", "Designation", "Phone", "Email", "Gender", "DOB"};
        model = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Records are read-only
        };
        employeeTable.setRowHeight(30);
        employeeTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        employeeTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane tableScroll = new JScrollPane(employeeTable);
        mainCard.add(tableScroll, BorderLayout.CENTER);

        wrapper.add(mainCard);

        // --- FINAL SCROLLPANE INTEGRATION (Responsive) ---
        JScrollPane mainScroll = new JScrollPane(wrapper);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        // --- LOGIC: Delegating to Controller ---

        // Initial load of all working employees via Controller
        refreshTable(controller.getWorkingEmployees());

        // Dynamic search logic: triggers as Admin types the ID
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                String query = txtSearch.getText().trim();
                // Fetch filtered data from Controller
                refreshTable(controller.filterWorkingEmployees(query));
            }
        });
    }

    /**
     * Helper to refresh the table with a list of employees.
     * @param list List of employee objects from Controller.
     */
    private void refreshTable(List<Employee> list) {
        model.setRowCount(0);
        if (list != null) {
            for (Employee e : list) {
                model.addRow(new Object[]{
                        e.employeeID, e.name, e.designation, e.phone, e.email, e.gender, e.dob
                });
            }
        }
    }
}