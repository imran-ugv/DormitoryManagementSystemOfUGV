package com.dorm.model;

public class Student {
    public String name, fatherName, motherName, gender, dob, phone, email, uniID, degree, roomNo, address, status;

    public Student(String name, String fatherName, String motherName, String gender, String dob,
                   String phone, String email, String uniID, String degree,
                   String roomNo, String address, String status) {
        this.name = name; this.fatherName = fatherName; this.motherName = motherName;
        this.gender = gender; this.dob = dob; this.phone = phone; this.email = email;
        this.uniID = uniID; this.degree = degree; this.roomNo = roomNo;
        this.address = address; this.status = status;
    }
}