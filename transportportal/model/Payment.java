package com.transportportal.model;

import java.sql.Timestamp;


public class Payment {

    private int id;
    private int bookingId;
    private double amount;
    private String method;   // e.g., "CASH", "MOBILE", "CARD"
    private String status;   // e.g., "PENDING", "COMPLETED", "FAILED"
    private Timestamp paidAt;

    public Payment() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getPaidAt() {
        return paidAt;
    }
    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public String toString() {
        return "Payment{id=" + id + ", bookingId=" + bookingId + ", amount=" + amount +
                ", method='" + method + "', status='" + status + "', paidAt=" + paidAt + "}";
    }
}
