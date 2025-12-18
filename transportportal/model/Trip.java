package com.transportportal.model;

import java.sql.Timestamp;

public class Trip {
    private int id;
    private String routeName;
    private String vehiclePlate;
    private double price;
    private Timestamp departureTime;
    private int availableSeats;
    private String status;

    // Database fields
    private int routeId;
    private int driverId;
    private int vehicleId;
    private Timestamp arrivalTime;
    private Timestamp date;
    
    // Seat management fields
    private int totalSeats = 0;
    private int bookedSeats = 0;

    public Trip() {}

    public Trip(int id, String routeName, String vehiclePlate, double price, Timestamp departureTime, int availableSeats) {
        this.id = id;
        this.routeName = routeName;
        this.vehiclePlate = vehiclePlate;
        this.price = price;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.status = "SCHEDULED";
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getVehiclePlate() { return vehiclePlate; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Timestamp getDepartureTime() { return departureTime; }
    public void setDepartureTime(Timestamp departureTime) { this.departureTime = departureTime; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Completed missing methods
    public Timestamp getDate() {
        if (this.date != null) {
            return this.date;
        }
        // If date is not set, return departure time as fallback
        return this.departureTime;
    }
    
    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getRouteId() {
        return this.routeId;
    }

    public int getDriverId() {
        return this.driverId;
    }

    public int getVehicleId() {
        return this.vehicleId;
    }

    public Timestamp getArrivalTime() {
        return this.arrivalTime;
    }

    // Seat management methods
    public int getTotalSeats() {
        return this.totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
        // Recalculate available seats if needed
        if (this.totalSeats > 0 && this.bookedSeats >= 0) {
            this.availableSeats = this.totalSeats - this.bookedSeats;
        }
    }

    public int getBookedSeats() {
        return this.bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
        // Recalculate available seats
        if (this.totalSeats > 0 && this.bookedSeats >= 0) {
            this.availableSeats = this.totalSeats - this.bookedSeats;
        }
    }

    // Business logic methods
    public boolean hasAvailableSeats() {
        return this.availableSeats > 0;
    }

    public boolean bookSeat() {
        if (hasAvailableSeats()) {
            this.bookedSeats++;
            this.availableSeats--;
            return true;
        }
        return false;
    }

    public boolean cancelSeat() {
        if (this.bookedSeats > 0 && this.availableSeats < this.totalSeats) {
            this.bookedSeats--;
            this.availableSeats++;
            return true;
        }
        return false;
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(this.status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(this.status);
    }

    public boolean isScheduled() {
        return "SCHEDULED".equalsIgnoreCase(this.status);
    }

    public boolean isOngoing() {
        return "ONGOING".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", routeName='" + routeName + '\'' +
                ", vehiclePlate='" + vehiclePlate + '\'' +
                ", price=" + price +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", availableSeats=" + availableSeats +
                ", status='" + status + '\'' +
                ", routeId=" + routeId +
                ", driverId=" + driverId +
                ", vehicleId=" + vehicleId +
                ", totalSeats=" + totalSeats +
                ", bookedSeats=" + bookedSeats +
                '}';
    }

    // Utility method for display
    public String getDisplayInfo() {
        return routeName + " - " + vehiclePlate + " (" + departureTime + ") - " + price + " RWF";
    }

    // Validation method
    public boolean isValid() {
        return routeId > 0 && 
               driverId > 0 && 
               vehicleId > 0 && 
               departureTime != null && 
               price >= 0 && 
               totalSeats > 0;
    }
}