package com.dorm.model;

public class Room {
    private String roomNumber;
    private String isActive;
    private String status;

    public Room(String roomNumber, String isActive, String status) {
        this.roomNumber = roomNumber;
        this.isActive = isActive;
        this.status = status;
    }

    // Getters
    public String getRoomNumber() { return roomNumber; }
    public String getIsActive() { return isActive; }
    public String getStatus() { return status; }
}