package com.dorm.model;

public class Fee {
    public String uniID, month, date;
    public double total, paid, due;

    public Fee(String uniID, String month, double total, double paid, double due, String date) {
        this.uniID = uniID; this.month = month; this.total = total;
        this.paid = paid; this.due = due; this.date = date;
    }
}