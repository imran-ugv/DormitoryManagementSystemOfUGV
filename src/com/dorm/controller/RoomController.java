package com.dorm.controller;

import com.dorm.model.DormitoryDAO;
import com.dorm.model.Room;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller to handle ManageRoomsView logic.
 * Enforces business rules (e.g., cannot delete booked rooms).
 */
public class RoomController {
    private DormitoryDAO dao = new DormitoryDAO();

    // Logic to fetch all rooms from the database
    public List<Room> getAllRooms() {
        return dao.getAllRooms();
    }

    // Logic to filter rooms based on search query
    public List<Room> searchRooms(String query) {
        return dao.searchRooms(query);
    }

    // Business logic to update room status
    public boolean handleUpdate(String roomNo, String activeStatus, String currentRoomStatus, Component parent) {
        // Business Rule: Booked rooms cannot be modified
        if (currentRoomStatus.equals("Booked")) {
            JOptionPane.showMessageDialog(parent, "Booked rooms cannot be modified!", "Action Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        dao.updateRoom(roomNo, activeStatus);
        JOptionPane.showMessageDialog(parent, "Room Status Updated Successfully!");
        return true;
    }

    // Business logic to delete a room with confirmation
    public boolean handleDelete(String roomNo, String currentRoomStatus, Component parent) {
        // Business Rule: Booked rooms cannot be deleted
        if (currentRoomStatus.equals("Booked")) {
            JOptionPane.showMessageDialog(parent, "Booked rooms cannot be deleted!", "Action Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int confirm = JOptionPane.showConfirmDialog(parent, "Are you sure you want to delete room " + roomNo + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteRoom(roomNo);
            JOptionPane.showMessageDialog(parent, "Room Deleted!");
            return true;
        }
        return false;
    }

    // Logic to validate and save a new room
    public boolean handleSaveRoom(String roomNo, String isActive, Component parent) {
        // 1. Validation: Check if empty
        if (roomNo.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Please enter a room number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 2. Validation: Check if room already exists in DB
        if (dao.isRoomExists(roomNo)) {
            JOptionPane.showMessageDialog(parent, "Room " + roomNo + " already exists in the system!", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. Save via DAO
        try {
            com.dorm.model.Room newRoom = new com.dorm.model.Room(roomNo, isActive, "Not Booked");
            dao.saveRoom(newRoom);
            JOptionPane.showMessageDialog(parent, "Room " + roomNo + " Saved Successfully!");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error saving room: " + e.getMessage());
            return false;
        }
    }
}