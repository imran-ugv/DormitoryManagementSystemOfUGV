package com.dorm.model;

public class Employee {
    public String employeeID, name, fatherName, motherName, gender, dob, phone, email, designation, presentAddr, permanentAddr, status;

    public Employee(String employeeID, String name, String fatherName, String motherName, String gender, String dob,
                    String phone, String email, String designation, String presentAddr, String permanentAddr, String status) {
        this.employeeID = employeeID;
        this.name = name; this.fatherName = fatherName; this.motherName = motherName;
        this.gender = gender; this.dob = dob; this.phone = phone; this.email = email;
        this.designation = designation; this.presentAddr = presentAddr;
        this.permanentAddr = permanentAddr; this.status = status;
    }
}