package com.dorm.view;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Room;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageRoomsView extends JPanel {

    private JTextField txtRoomNumber, txtSearch;
    private JComboBox<String> cmbActive;
    private JButton btnUpdate, btnDelete, btnClear;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private DormitoryDAO dao = new DormitoryDAO();

    public ManageRoomsView() {
        setBackground(new Color(245, 245, 245));
        setLayout(new GridBagLayout());

        JPanel mainCard = new JPanel(new GridBagLayout());
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Header
        JLabel lblHeader = new JLabel("MANAGE ROOM RECORDS");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(new Color(153, 153, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        mainCard.add(lblHeader, gbc);

        // 2. Input Row
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        gbc.gridx = 0;
        mainCard.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1;
        txtRoomNumber = new JTextField();
        txtRoomNumber.setPreferredSize(new Dimension(150, 35));
        txtRoomNumber.setEditable(false); // Should not change room number
        mainCard.add(txtRoomNumber, gbc);

        gbc.gridx = 2;
        mainCard.add(new JLabel("Room Active:"), gbc);
        gbc.gridx = 3;
        cmbActive = new JComboBox<>(new String[]{"YES", "NO"});
        cmbActive.setPreferredSize(new Dimension(100, 35));
        mainCard.add(cmbActive, gbc);

        // 3. Action Buttons Row
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);

        btnUpdate = new JButton("Update Status");
        btnUpdate.setBackground(new Color(102, 255, 102));
        btnUpdate.setPreferredSize(new Dimension(130, 35));

        btnDelete = new JButton("Delete Room");
        btnDelete.setBackground(new Color(255, 102, 102));
        btnDelete.setPreferredSize(new Dimension(130, 35));

        btnClear = new JButton("Clear Selection");
        btnClear.setPreferredSize(new Dimension(130, 35));

        btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        mainCard.add(btnPanel, gbc);

        // 4. Search and Table Title
        gbc.gridy = 3; gbc.gridwidth = 4;
        JLabel lblTable = new JLabel("SELECT A ROOM TO MANAGE", SwingConstants.CENTER);
        lblTable.setFont(new Font("Serif", Font.BOLD, 18));
        lblTable.setBorder(new EmptyBorder(20, 0, 10, 0));
        mainCard.add(lblTable, gbc);

        gbc.gridy = 4;
        JPanel searchPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPnl.setOpaque(false);
        txtSearch = new JTextField(15);
        searchPnl.add(new JLabel("Search Room: "));
        searchPnl.add(txtSearch);
        mainCard.add(searchPnl, gbc);

        // 5. Table
        gbc.gridy = 5;
        tableModel = new DefaultTableModel(new String[]{"Room Number", "Active", "Room Status"}, 0);
        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(roomTable);
        scroll.setPreferredSize(new Dimension(700, 200));
        mainCard.add(scroll, gbc);

        add(mainCard);

        // --- LOGIC ---

        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = roomTable.getSelectedRow();
                txtRoomNumber.setText(tableModel.getValueAt(row, 0).toString());
                cmbActive.setSelectedItem(tableModel.getValueAt(row, 1).toString());
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = roomTable.getSelectedRow();
            if (row != -1) {
                String status = tableModel.getValueAt(row, 2).toString();
                if (status.equals("Booked")) {
                    JOptionPane.showMessageDialog(this, "Booked rooms cannot be modified!");
                } else {
                    dao.updateRoom(txtRoomNumber.getText(), cmbActive.getSelectedItem().toString());
                    JOptionPane.showMessageDialog(this, "Status Updated!");
                    refreshTable(dao.getAllRooms());
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = roomTable.getSelectedRow();
            if (row != -1) {
                String status = tableModel.getValueAt(row, 2).toString();
                if (status.equals("Booked")) {
                    JOptionPane.showMessageDialog(this, "Booked rooms cannot be deleted!");
                } else {
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete this room?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dao.deleteRoom(txtRoomNumber.getText());
                        refreshTable(dao.getAllRooms());
                    }
                }
            }
        });

        btnClear.addActionListener(e -> {
            txtRoomNumber.setText("");
            cmbActive.setSelectedIndex(0);
            roomTable.clearSelection();
        });

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refreshTable(dao.searchRooms(txtSearch.getText().trim()));
            }
        });

        refreshTable(dao.getAllRooms());
    }

    private void refreshTable(List<Room> list) {
        tableModel.setRowCount(0);
        for (Room r : list) {
            tableModel.addRow(new Object[]{r.getRoomNumber(), r.getIsActive(), r.getStatus()});
        }
    }
}