package com.transportportal.model;

import java.sql.Timestamp;


public class Booking {

    private int id;
    private int tripId;
    private int userId;
    private int seats;
    private double totalPrice;
    private String status;
    private Timestamp bookedAt;
    private String paymentStatus;
    private int seatNumber;
    private String passengerName;
    
    // Additional display fields for UI
    private String routeName;
    private String vehiclePlate;
    private Timestamp departureTime;
    private String tripDetails;

    public Booking() {}

    // id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // tripId
    public int getTripId() {
        return tripId;
    }
    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    // userId
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // seats
    public int getSeats() {
        return seats;
    }
    public void setSeats(int seats) {
        this.seats = seats;
    }

    // ✅ added alias for DAO compatibility
    public int getSeatCount() {
        return seats;
    }
    public void setSeatCount(int seatCount) {
        this.seats = seatCount;
    }

    // totalPrice
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // status
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // bookedAt
    public Timestamp getBookedAt() {
        return bookedAt;
    }
    public void setBookedAt(Timestamp bookedAt) {
        this.bookedAt = bookedAt;
    }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    // Completed missing methods
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
    
    public String getRouteName() {
        return this.routeName;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }
    
    public String getVehiclePlate() {
        return this.vehiclePlate;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }
    
    public Timestamp getDepartureTime() {
        return this.departureTime;
    }

    // Additional utility methods for UI display
    public String getTripDetails() {
        if (this.tripDetails != null) {
            return this.tripDetails;
        }
        if (this.routeName != null && this.departureTime != null) {
            return this.routeName + " - " + this.departureTime;
        }
        return "Trip #" + this.tripId;
    }
    
    public void setTripDetails(String tripDetails) {
        this.tripDetails = tripDetails;
    }

    // Business logic methods
    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(this.status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(this.status);
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(this.status);
    }

    public boolean canBeCancelled() {
        return isConfirmed() || isPending();
    }

    public double calculateTotalPrice(double pricePerSeat) {
        if (this.seats > 0 && pricePerSeat > 0) {
            this.totalPrice = this.seats * pricePerSeat;
        }
        return this.totalPrice;
    }

    // Validation method
    public boolean isValid() {
        return this.tripId > 0 && 
               this.userId > 0 && 
               this.seats > 0 && 
               this.totalPrice >= 0 &&
               this.status != null &&
               !this.status.trim().isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking{")
          .append("id=").append(id)
          .append(", tripId=").append(tripId)
          .append(", userId=").append(userId)
          .append(", seats=").append(seats)
          .append(", totalPrice=").append(totalPrice)
          .append(", status='").append(status).append('\'')
          .append(", passengerName='").append(passengerName).append('\'')
          .append(", seatNumber=").append(seatNumber)
          .append(", paymentStatus='").append(paymentStatus).append('\'');
        
        if (routeName != null) {
            sb.append(", routeName='").append(routeName).append('\'');
        }
        if (vehiclePlate != null) {
            sb.append(", vehiclePlate='").append(vehiclePlate).append('\'');
        }
        if (departureTime != null) {
            sb.append(", departureTime=").append(departureTime);
        }
        
        sb.append(", bookedAt=").append(bookedAt)
          .append('}');
        
        return sb.toString();
    }

    // Static factory methods for convenience
    public static Booking createConfirmedBooking(int tripId, int userId, int seats, 
                                                String passengerName, double pricePerSeat) {
        Booking booking = new Booking();
        booking.setTripId(tripId);
        booking.setUserId(userId);
        booking.setSeats(seats);
        booking.setPassengerName(passengerName);
        booking.setStatus("CONFIRMED");
        booking.setPaymentStatus("PAID");
        booking.calculateTotalPrice(pricePerSeat);
        booking.setBookedAt(new Timestamp(System.currentTimeMillis()));
        return booking;
    }

    public static Booking createPendingBooking(int tripId, int userId, int seats, 
                                              String passengerName) {
        Booking booking = new Booking();
        booking.setTripId(tripId);
        booking.setUserId(userId);
        booking.setSeats(seats);
        booking.setPassengerName(passengerName);
        booking.setStatus("PENDING");
        booking.setPaymentStatus("PENDING");
        booking.setBookedAt(new Timestamp(System.currentTimeMillis()));
        return booking;
    }
}