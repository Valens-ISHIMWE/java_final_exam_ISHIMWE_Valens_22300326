package com.transportportal.model;

import java.sql.Timestamp;
import java.util.Date;


public class TransportData {

    // =====================================================
    // USER
    // =====================================================
    public static class User {
        private int id;
        private String username;
        private String password;
        private String fullName;
        private String role;
        private String email;
        private String phone;

        public User() {}

        public User(int id, String username, String password, String fullName, String role) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        @Override
        public String toString() {
            String fn = (fullName == null || fullName.isEmpty())
                    ? username
                    : fullName + " (" + username + ")";
            return fn + " [" + role + "]";
        }
    }

    // =====================================================
    // DRIVER
    // =====================================================
    public static class Driver {
        private int driverID;
        private String name;
        private String contact;
        private String licenseNumber;
        private Date createdAt;
        
        private int userId;
        private int experienceYears;
        private String status;

        public Driver() {}

        public Driver(int driverID, String name, String contact) {
            this.driverID = driverID;
            this.name = name;
            this.contact = contact;
            this.createdAt = new Date();
            this.status = "ACTIVE";
        }

        public int getDriverID() { return driverID; }
        public void setDriverID(int driverID) { this.driverID = driverID; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }

        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

        public int getUserId() { return this.userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public int getExperienceYears() { return this.experienceYears; }
        public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

        public String getStatus() { return this.status != null ? this.status : "ACTIVE"; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public String toString() {
            return driverID + " - " + name + " (" + contact + ")";
        }
    }

    // =====================================================
    // VEHICLE (Updated with Model and Type)
    // =====================================================
    public static class Vehicle {
        private int vehicleID;
        private String name;
        private String identifier;
        private String status;
        private String location;
        private String contact;
        private Date assignedSince;
        private int capacity; 
        
        // --- NEW FIELDS ---
        private String model;
        private String type;

        public Vehicle() {
            this.capacity = 45; 
        }

        public Vehicle(int vehicleID, String name, String identifier) {
            this.vehicleID = vehicleID;
            this.name = name;
            this.identifier = identifier;
            this.status = "Available";
            this.assignedSince = new Date();
            this.capacity = 45;
        }

        public int getVehicleID() { return vehicleID; }
        public void setVehicleID(int vehicleID) { this.vehicleID = vehicleID; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }

        public Date getAssignedSince() { return assignedSince; }
        public void setAssignedSince(Date assignedSince) { this.assignedSince = assignedSince; }

        public int getCapacity() { return this.capacity > 0 ? this.capacity : 45; }
        public void setCapacity(int capacity) {
            this.capacity = (capacity > 0) ? capacity : 45;
        }

        // --- NEW METHODS FOR DASHBOARD COMPATIBILITY ---
        public String getModel() {
            return (model != null && !model.isEmpty()) ? model : "Standard Model";
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getType() {
            return (type != null && !type.isEmpty()) ? type : "Transport";
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return vehicleID + " - " + getName() + " [" + identifier + "] - " + getModel();
        }
    }

    // =====================================================
    // ROUTE
    // =====================================================
    public static class Route {
        private int routeID;
        private String origin;
        private String destination;
        private double distance;
        private Timestamp createdAt;
        private int id;
        private double distanceKm;

        public Route() {}

        public Route(int routeID, String origin, String destination, double distance) {
            this.routeID = routeID;
            this.origin = origin;
            this.destination = destination;
            this.distance = distance;
            this.distanceKm = distance;
            this.createdAt = new Timestamp(System.currentTimeMillis());
        }

        public int getRouteID() { return routeID; }
        public void setRouteID(int routeID) { this.routeID = routeID; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public double getDistance() { return distance; }
        public void setDistance(double distance) { 
            this.distance = distance;
            this.distanceKm = distance;
        }
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        public void setId(int id) { this.id = id; this.routeID = id; }
        public int getId() { return this.id > 0 ? this.id : this.routeID; }
        public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; this.distance = distanceKm; }
        public double getDistanceKm() { return this.distanceKm > 0 ? this.distanceKm : this.distance; }

        @Override
        public String toString() { return origin + " → " + destination + " (" + distance + " km)"; }
    }

    // =====================================================
    // TRIP
    // =====================================================
    public static class Trip {
        private int tripID;
        private int driverID;
        private int routeID;
        private int vehicleID;
        private Timestamp departure;
        private Timestamp arrival;
        private String status;
        private double totalAmount;
        private String paymentMethod;
        private int availableSeats;
        private int id;
        private String routeName;
        private String vehiclePlate;
        private double price;
        private Timestamp departureTime;
        private Timestamp arrivalTime;
        private int totalSeats;
        private int bookedSeats;

        public Trip() {}

        public int getTripID() { return tripID; }
        public void setTripID(int tripID) { this.tripID = tripID; this.id = tripID; }
        public int getDriverID() { return driverID; }
        public void setDriverID(int driverID) { this.driverID = driverID; }
        public int getRouteID() { return routeID; }
        public void setRouteID(int routeID) { this.routeID = routeID; }
        public int getVehicleID() { return vehicleID; }
        public void setVehicleID(int vehicleID) { this.vehicleID = vehicleID; }
        public Timestamp getDeparture() { return departure; }
        public void setDeparture(Timestamp departure) { this.departure = departure; this.departureTime = departure; }
        public Timestamp getArrival() { return arrival; }
        public void setArrival(Timestamp arrival) { this.arrival = arrival; this.arrivalTime = arrival; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; this.price = totalAmount; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
        public int getId() { return this.id > 0 ? this.id : this.tripID; }
        public String getRouteName() { return this.routeName != null ? this.routeName : "Route #" + routeID; }
        public Timestamp getDepartureTime() { return this.departureTime != null ? this.departureTime : this.departure; }
        public Timestamp getArrivalTime() { return this.arrivalTime != null ? this.arrivalTime : this.arrival; }
        public double getPrice() { return this.price > 0 ? this.price : this.totalAmount; }
        public void setId(int id) { this.id = id; this.tripID = id; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }
        public String getVehiclePlate() { return this.vehiclePlate != null ? this.vehiclePlate : "Vehicle #" + vehicleID; }
        public void setPrice(double price) { this.price = price; this.totalAmount = price; }
        public void setDepartureTime(Timestamp departureTime) { this.departureTime = departureTime; this.departure = departureTime; }
        public void setArrivalTime(Timestamp arrivalTime) { this.arrivalTime = arrivalTime; this.arrival = arrivalTime; }
        public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
        public int getTotalSeats() { return this.totalSeats; }
        public void setBookedSeats(int bookedSeats) {
            this.bookedSeats = bookedSeats;
            if (this.totalSeats > 0) this.availableSeats = this.totalSeats - bookedSeats;
        }
        public int getBookedSeats() { return this.bookedSeats; }

        @Override
        public String toString() { return "Trip #" + tripID + " | Status: " + status; }
    }

    // =====================================================
    // BOOKING
    // =====================================================
    public static class Booking {
        private int id;
        private int userId;
        private int tripId;
        private int seatCount;
        private String status;
        private Timestamp createdAt;
        private String routeName;
        private String vehiclePlate;
        private Timestamp departureTime;
        private double totalPrice;
        private String passengerName;
        private int seats;
        private Timestamp bookedAt;

        public Booking() {}

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public int getTripId() { return tripId; }
        public void setTripId(int tripId) { this.tripId = tripId; }
        public int getSeatCount() { return seatCount; }
        public void setSeatCount(int seatCount) { this.seatCount = seatCount; this.seats = seatCount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; if (this.bookedAt == null) this.bookedAt = createdAt; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public String getVehiclePlate() { return vehiclePlate; }
        public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }
        public Timestamp getDepartureTime() { return departureTime; }
        public void setDepartureTime(Timestamp departureTime) { this.departureTime = departureTime; }
        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
        public String getPassengerName() { return passengerName != null ? passengerName : "Passenger #" + userId; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public Date getBookedAt() { return bookedAt != null ? new Date(bookedAt.getTime()) : new Date(); }
        public void setBookedAt(Timestamp bookedAt) { this.bookedAt = bookedAt; }
        public Object getSeats() { return Math.max(this.seats, 1); }
        public void setSeats(int seats) { this.seats = seats; this.seatCount = seats; }
    }

    // =====================================================
    // MAINTENANCE
    // =====================================================
    public static class Maintenance {
        private int maintenanceID;
        private int vehicleID;
        private String description;
        private Timestamp date;
        private String status;
        private String remarks;

        public Maintenance() {}

        public int getMaintenanceID() { return maintenanceID; }
        public void setMaintenanceID(int maintenanceID) { this.maintenanceID = maintenanceID; }
        public int getVehicleID() { return vehicleID; }
        public void setVehicleID(int vehicleID) { this.vehicleID = vehicleID; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Timestamp getDate() { return date; }
        public void setDate(Timestamp date) { this.date = date; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }
}