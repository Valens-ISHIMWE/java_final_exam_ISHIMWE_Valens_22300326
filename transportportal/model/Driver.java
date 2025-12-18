package com.transportportal.model;

import java.sql.Timestamp;


public class Driver {

    private int id;
    private int userId; // reference to User table
    private String licenseNumber;
    private String status;  // e.g., "ACTIVE", "INACTIVE", "ON_TRIP"
    private String assignedVehicle;
    private Timestamp hiredAt;

    // Added fields that were referenced in other parts
    private String fullName;
    private String phone;

    public Driver() {}

    // id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // userId
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // licenseNumber
    public String getLicenseNumber() {
        return licenseNumber;
    }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    // status
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // assignedVehicle
    public String getAssignedVehicle() {
        return assignedVehicle;
    }
    public void setAssignedVehicle(String assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    // hiredAt
    public Timestamp getHiredAt() {
        return hiredAt;
    }
    public void setHiredAt(Timestamp hiredAt) {
        this.hiredAt = hiredAt;
    }

    // fullName (was previously empty)
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getFullName() {
        return this.fullName;
    }

    // phone (was previously empty)
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhone() {
        return this.phone;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", status='" + status + '\'' +
                ", assignedVehicle='" + assignedVehicle + '\'' +
                ", hiredAt=" + hiredAt +
                '}';
    }
}
