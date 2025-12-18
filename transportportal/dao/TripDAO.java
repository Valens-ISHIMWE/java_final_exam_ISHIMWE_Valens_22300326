package com.transportportal.dao;

import com.transportportal.config.DBConnection;
import com.transportportal.model.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripDAO {

    public static List<Trip> findByDriver(int driverId) {
        List<Trip> trips = new ArrayList<Trip>();
        
        // SIMPLE VERSION - No routes table join
        String sql = "SELECT t.*, v.plate_number, " +
                    "v.capacity as total_seats, " +
                    "(SELECT COUNT(*) FROM bookings b WHERE b.trip_id = t.id AND b.status != 'CANCELLED') as booked_seats " +
                    "FROM trips t " +
                    "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                    "WHERE t.driver_id = ? AND t.status != 'CANCELLED' " +
                    "ORDER BY t.departure_time DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, driverId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setRouteId(rs.getInt("route_id"));
                trip.setDriverId(rs.getInt("driver_id"));
                trip.setVehicleId(rs.getInt("vehicle_id"));
                trip.setDepartureTime(rs.getTimestamp("departure_time"));
                trip.setArrivalTime(rs.getTimestamp("arrival_time"));
                trip.setStatus(rs.getString("status"));
                trip.setPrice(rs.getDouble("price"));
                trip.setVehiclePlate(rs.getString("plate_number"));
                trip.setTotalSeats(rs.getInt("total_seats"));
                trip.setBookedSeats(rs.getInt("booked_seats"));
                
                // Create a simple route name
                trip.setRouteName("Route #" + rs.getInt("route_id"));
                
                trips.add(trip);
            }
        } catch (SQLException e) {
            System.err.println("Error loading trips for driver " + driverId + ": " + e.getMessage());
            e.printStackTrace();
            
            // If the complex query fails, try ultra-simple version
            return findByDriverSimple(driverId);
        } finally {
            closeResources(rs, ps, conn);
        }
        return trips;
    }

    // Ultra-simple fallback method
    private static List<Trip> findByDriverSimple(int driverId) {
        List<Trip> trips = new ArrayList<Trip>();
        
        // ULTRA SIMPLE - Only trips table
        String sql = "SELECT * FROM trips WHERE driver_id = ? AND status != 'CANCELLED' ORDER BY departure_time DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, driverId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setRouteId(rs.getInt("route_id"));
                trip.setDriverId(rs.getInt("driver_id"));
                trip.setVehicleId(rs.getInt("vehicle_id"));
                trip.setDepartureTime(rs.getTimestamp("departure_time"));
                trip.setArrivalTime(rs.getTimestamp("arrival_time"));
                trip.setStatus(rs.getString("status"));
                trip.setPrice(rs.getDouble("price"));
                
                // Set default values for missing fields
                trip.setRouteName("Route #" + rs.getInt("route_id"));
                trip.setVehiclePlate("Vehicle #" + rs.getInt("vehicle_id"));
                trip.setTotalSeats(50); // Default capacity
                trip.setBookedSeats(0); // Default booked seats
                
                trips.add(trip);
            }
        } catch (SQLException e) {
            System.err.println("Even simple query failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return trips;
    }

    public static void updateStatus(int tripId, String newStatus) {
        String sql = "UPDATE trips SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, tripId);
            int rowsUpdated = ps.executeUpdate();
            System.out.println("Updated " + rowsUpdated + " trip(s) to status: " + newStatus);
        } catch (SQLException e) {
            System.err.println("Error updating trip status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public static List<Trip> findHistoryByDriver(int driverId) {
        List<Trip> trips = new ArrayList<Trip>();
        
        // Simple version for history too
        String sql = "SELECT t.*, v.plate_number, " +
                    "v.capacity as total_seats, " +
                    "(SELECT COUNT(*) FROM bookings b WHERE b.trip_id = t.id AND b.status != 'CANCELLED') as booked_seats " +
                    "FROM trips t " +
                    "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                    "WHERE t.driver_id = ? AND t.status IN ('COMPLETED', 'CANCELLED') " +
                    "ORDER BY t.departure_time DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, driverId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setRouteId(rs.getInt("route_id"));
                trip.setDriverId(rs.getInt("driver_id"));
                trip.setVehicleId(rs.getInt("vehicle_id"));
                trip.setDepartureTime(rs.getTimestamp("departure_time"));
                trip.setArrivalTime(rs.getTimestamp("arrival_time"));
                trip.setStatus(rs.getString("status"));
                trip.setPrice(rs.getDouble("price"));
                trip.setVehiclePlate(rs.getString("plate_number"));
                trip.setTotalSeats(rs.getInt("total_seats"));
                trip.setBookedSeats(rs.getInt("booked_seats"));
                
                // Create a simple route name
                trip.setRouteName("Route #" + rs.getInt("route_id"));
                
                trips.add(trip);
            }
        } catch (SQLException e) {
            System.err.println("Error loading trip history: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to simple query
            String simpleSql = "SELECT * FROM trips WHERE driver_id = ? AND status IN ('COMPLETED', 'CANCELLED') ORDER BY departure_time DESC";
            try {
                conn = DBConnection.getConnection();
                ps = conn.prepareStatement(simpleSql);
                ps.setInt(1, driverId);
                rs = ps.executeQuery();
                
                while (rs.next()) {
                    Trip trip = new Trip();
                    trip.setId(rs.getInt("id"));
                    trip.setRouteId(rs.getInt("route_id"));
                    trip.setDriverId(rs.getInt("driver_id"));
                    trip.setVehicleId(rs.getInt("vehicle_id"));
                    trip.setDepartureTime(rs.getTimestamp("departure_time"));
                    trip.setArrivalTime(rs.getTimestamp("arrival_time"));
                    trip.setStatus(rs.getString("status"));
                    trip.setPrice(rs.getDouble("price"));
                    trip.setRouteName("Route #" + rs.getInt("route_id"));
                    trip.setVehiclePlate("Vehicle #" + rs.getInt("vehicle_id"));
                    trip.setTotalSeats(50);
                    trip.setBookedSeats(0);
                    
                    trips.add(trip);
                }
            } catch (SQLException ex) {
                System.err.println("Even history fallback failed: " + ex.getMessage());
            }
        } finally {
            closeResources(rs, ps, conn);
        }
        return trips;
    }

    public static Trip findById(int tripId) throws SQLException {
        String sql = "SELECT t.*, r.origin, r.destination, v.plate_number, v.capacity as total_seats, " +
                    "(SELECT COUNT(*) FROM bookings b WHERE b.trip_id = t.id AND b.status != 'CANCELLED') as booked_seats " +
                    "FROM trips t " +
                    "LEFT JOIN routes r ON t.route_id = r.id " +
                    "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                    "WHERE t.id = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setRouteId(rs.getInt("route_id"));
                trip.setDriverId(rs.getInt("driver_id"));
                trip.setVehicleId(rs.getInt("vehicle_id"));
                trip.setDepartureTime(rs.getTimestamp("departure_time"));
                trip.setArrivalTime(rs.getTimestamp("arrival_time"));
                trip.setStatus(rs.getString("status"));
                trip.setPrice(rs.getDouble("price"));
                trip.setVehiclePlate(rs.getString("plate_number"));
                trip.setTotalSeats(rs.getInt("total_seats"));
                trip.setBookedSeats(rs.getInt("booked_seats"));
                trip.setRouteName(rs.getString("origin") + " - " + rs.getString("destination"));
                return trip;
            }
            return null;
            
        } finally {
            closeResources(rs, ps, conn);
        }
    }
    
    // Helper method to create sample data for testing
    public static void createSampleTripsForDriver(int driverId) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Check if driver already has trips
            String checkSql = "SELECT COUNT(*) FROM trips WHERE driver_id = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, driverId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int existingTrips = rs.getInt(1);
            rs.close();
            
            if (existingTrips > 0) {
                System.out.println("Driver already has " + existingTrips + " trips");
                return;
            }
            
            // Create sample trips
            String insertSql = "INSERT INTO trips (route_id, driver_id, vehicle_id, departure_time, price, status) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(insertSql);
            
            // Trip 1
            ps.setInt(1, 1);
            ps.setInt(2, driverId);
            ps.setInt(3, 1);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis() + 3600000)); // 1 hour from now
            ps.setDouble(5, 5000.0);
            ps.setString(6, "SCHEDULED");
            ps.addBatch();
            
            // Trip 2
            ps.setInt(1, 2);
            ps.setInt(2, driverId);
            ps.setInt(3, 2);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis() + 7200000)); // 2 hours from now
            ps.setDouble(5, 7000.0);
            ps.setString(6, "SCHEDULED");
            ps.addBatch();
            
            // Trip 3
            ps.setInt(1, 3);
            ps.setInt(2, driverId);
            ps.setInt(3, 3);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis() + 10800000)); // 3 hours from now
            ps.setDouble(5, 8000.0);
            ps.setString(6, "SCHEDULED");
            ps.addBatch();
            
            int[] results = ps.executeBatch();
            System.out.println("Created " + results.length + " sample trips for driver " + driverId);
            
        } catch (SQLException e) {
            System.err.println("Error creating sample trips: " + e.getMessage());
        } finally {
            closeResources(null, ps, conn);
        }
    }

    private static void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}