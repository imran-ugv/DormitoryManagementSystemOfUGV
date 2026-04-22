package com.dorm.view;

import com.dorm.controller.RoomController; // Import Controller
import com.dorm.model.Room;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * View class for adding new rooms.
 * Strictly follows MVC by delegating logic to RoomController.
 */
public class RoomView extends JPanel {

    private JTextField txtRoomNumber, txtSearch;
    private JComboBox<String> cmbActive;
    private JButton btnSave;
    private JTable roomTable;
    private DefaultTableModel tableModel;

    // Instantiate Controller
    private RoomController controller = new RoomController();

    public RoomView() {
        // Setup Main Background
        setBackground(new Color(245, 245, 245));
        setLayout(new GridBagLayout());

        // --- MAIN WHITE CARD ---
        JPanel mainCard = new JPanel(new GridBagLayout());
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Header Title
        JLabel lblHeader = new JLabel("ADD NEW ROOM");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        mainCard.add(lblHeader, gbc);

        // 2. Input Fields Row
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        gbc.gridx = 0;
        JLabel lblNo = new JLabel("<html>Room Number <font color='red'>*</font></html>");
        lblNo.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainCard.add(lblNo, gbc);

        gbc.gridx = 1;
        txtRoomNumber = new JTextField();
        txtRoomNumber.setPreferredSize(new Dimension(200, 35));
        txtRoomNumber.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mainCard.add(txtRoomNumber, gbc);

        gbc.gridx = 2;
        JLabel lblActive = new JLabel("<html>Active <font color='red'>*</font></html>");
        lblActive.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainCard.add(lblActive, gbc);

        gbc.gridx = 3;
        cmbActive = new JComboBox<>(new String[]{"YES", "NO"});
        cmbActive.setPreferredSize(new Dimension(100, 35));
        mainCard.add(cmbActive, gbc);

        // 3. Save Button
        gbc.gridx = 4;
        btnSave = new JButton("Save Room");
        btnSave.setPreferredSize(new Dimension(130, 35));
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainCard.add(btnSave, gbc);

        // 4. Separator Title for Table
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 5;
        JLabel lblTableTitle = new JLabel("ALL ROOMS LIST", SwingConstants.CENTER);
        lblTableTitle.setFont(new Font("Serif", Font.BOLD, 18));
        lblTableTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        mainCard.add(lblTableTitle, gbc);

        // 5. Search Bar
        gbc.gridy = 3; gbc.gridwidth = 5;
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("Search Room: ");
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(150, 30));
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        mainCard.add(searchPanel, gbc);

        // 6. Room Table
        gbc.gridy = 4; gbc.gridwidth = 5;
        String[] columns = {"Room Number", "Active", "Room Status"};
        tableModel = new DefaultTableModel(columns, 0);
        roomTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        roomTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setPreferredSize(new Dimension(700, 250));
        mainCard.add(scrollPane, gbc);

        add(mainCard);

        // --- BUTTON ACTIONS: Delegating to Controller ---

        btnSave.addActionListener(e -> {
            String roomNo = txtRoomNumber.getText().trim();
            String isActive = (String) cmbActive.getSelectedItem();

            // Delegate logic to controller
            if (controller.handleSaveRoom(roomNo, isActive, this)) {
                txtRoomNumber.setText(""); // Reset UI
                refreshTable(controller.getAllRooms()); // Refresh list from controller
            }
        });

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // Delegate search to controller
                refreshTable(controller.searchRooms(txtSearch.getText().trim()));
            }
        });

        // Initial Load using controller
        refreshTable(controller.getAllRooms());
    }

    private void refreshTable(List<Room> list) {
        tableModel.setRowCount(0);
        for (Room r : list) {
            tableModel.addRow(new Object[]{r.getRoomNumber(), r.getIsActive(), r.getStatus()});
        }
    }
}