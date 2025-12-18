package com.transportportal.service;

import com.transportportal.dao.BookingDAO;
import com.transportportal.dao.TripDAO;
import com.transportportal.model.TransportData.User;
import com.transportportal.model.Driver;
import com.transportportal.model.Vehicle;
import com.transportportal.model.Route;
import com.transportportal.model.Trip;
import com.transportportal.model.Booking;
import com.transportportal.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TransportService {

    // --- DB Configuration ---
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/transportportal?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    // --------------------------

    public TransportService() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("⚠ MySQL JDBC driver not found.");
            }
        }
    }

    // ============================================================
    // USER CRUD (same as before - KEEP EXISTING)
    // ============================================================

    public List<User> loadUsersFromDatabase() throws SQLException {
        List<User> list = new ArrayList<User>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT id, username, password, full_name, role FROM users ORDER BY id";
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        } finally {
            closeQuietly(rs); closeQuietly(ps); closeQuietly(conn);
        }
        return list;
    }

    public int register(User user) throws SQLException {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psDriver = null;
        ResultSet keys = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Insert into users table
            String userSql = "INSERT INTO users (username, password, full_name, role, contact, email) VALUES (?, ?, ?, ?, ?, ?)";
            psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, user.getUsername());
            psUser.setString(2, user.getPassword());
            psUser.setString(3, user.getFullName());
            psUser.setString(4, user.getRole());
            psUser.setString(5, user.getPhone());
            psUser.setString(6, user.getEmail());
            psUser.executeUpdate();
            
            int userId = 0;
            keys = psUser.getGeneratedKeys();
            if (keys.next()) {
                userId = keys.getInt(1);
            }
            keys.close();
            
            // 2. If user is a DRIVER, also insert into drivers table
            if ("DRIVER".equalsIgnoreCase(user.getRole())) {
                String driverSql = "INSERT INTO drivers (user_id, license_number, experience_years, status) VALUES (?, ?, ?, ?)";
                psDriver = conn.prepareStatement(driverSql, Statement.RETURN_GENERATED_KEYS);
                psDriver.setInt(1, userId);
                psDriver.setString(2, "PENDING"); // Default license number
                psDriver.setInt(3, 0); // Default experience
                psDriver.setString(4, "PENDING"); // Default status
                psDriver.executeUpdate();
                
                keys = psDriver.getGeneratedKeys();
                if (keys.next()) {
                    userId = keys.getInt(1); // Return driver ID
                }
                keys.close();
            }
            
            conn.commit(); // Commit transaction
            return userId;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback on error
            }
            throw e;
        } finally {
            closeQuietly(keys);
            closeQuietly(psDriver);
            closeQuietly(psUser);
            closeQuietly(conn);
        }
    }

    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = null; PreparedStatement ps = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            closeQuietly(ps); closeQuietly(conn);
        }
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username=?, password=?, full_name=?, role=? WHERE id=?";
        Connection conn = null; PreparedStatement ps = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            ps.setInt(5, user.getId());
            return ps.executeUpdate() > 0;
        } finally {
            closeQuietly(ps); closeQuietly(conn);
        }
    }

    public User authenticate(String username, String password, String requiredRole) throws SQLException {
        String sql = "SELECT id, username, password, full_name, role, contact, email FROM users WHERE username=? LIMIT 1";
        Connection conn = null; 
        PreparedStatement ps = null; 
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String storedRole = rs.getString("role");
                
                // DEBUG: Print what we found in database
                System.out.println("=== DEBUG LOGIN ATTEMPT ===");
                System.out.println("Username: " + username);
                System.out.println("Input password: " + password);
                System.out.println("Stored password: " + storedPassword);
                System.out.println("Stored role: " + storedRole);
                System.out.println("Required role: " + requiredRole);
                System.out.println("Password match: " + storedPassword.equals(password));
                System.out.println("Role match: " + (requiredRole == null || storedRole.equalsIgnoreCase(requiredRole)));
                System.out.println("===========================");
                
                // Check password match
                if (storedPassword != null && storedPassword.equals(password)) {
                    // Check role match (case-insensitive)
                    if (requiredRole == null || storedRole.equalsIgnoreCase(requiredRole)) {
                        User u = new User();
                        u.setId(rs.getInt("id"));
                        u.setUsername(rs.getString("username"));
                        u.setPassword(storedPassword);
                        u.setFullName(rs.getString("full_name"));
                        u.setRole(storedRole);
                        u.setPhone(rs.getString("contact"));
                        u.setEmail(rs.getString("email"));
                        System.out.println("LOGIN SUCCESS for user: " + u.getUsername());
                        return u;
                    } else {
                        System.out.println("ROLE MISMATCH: Stored='" + storedRole + "', Required='" + requiredRole + "'");
                    }
                } else {
                    System.out.println("PASSWORD MISMATCH for user: " + username);
                }
            } else {
                System.out.println("USER NOT FOUND: " + username);
            }
        } finally {
            closeQuietly(rs); 
            closeQuietly(ps); 
            closeQuietly(conn);
        }
        return null;
    }

    // ============================================================
    // ROUTE CRUD (KEEP EXISTING)
    // ============================================================

    public int addRoute(Route r) throws SQLException {
        String sql = "INSERT INTO routes (origin, destination, distance_km) VALUES (?, ?, ?)";
        Connection conn = null; PreparedStatement ps = null; ResultSet keys = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, r.getOrigin());
            ps.setString(2, r.getDestination());
            ps.setDouble(3, r.getDistanceKm());
            ps.executeUpdate();
            keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } finally {
            closeQuietly(keys); closeQuietly(ps); closeQuietly(conn);
        }
        return 0;
    }

    public List<Route> findAllRoutes() throws SQLException {
        List<Route> routes = new ArrayList<Route>();
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        String sql = "SELECT id, origin, destination, distance_km FROM routes";
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Route r = new Route();
                r.setId(rs.getInt("id"));
                r.setOrigin(rs.getString("origin"));
                r.setDestination(rs.getString("destination"));
                r.setDistanceKm(rs.getDouble("distance_km"));
                routes.add(r);
            }
        } finally {
            closeQuietly(rs); closeQuietly(ps); closeQuietly(conn);
        }
        return routes;
    }

    // ============================================================
    // VEHICLE CRUD (KEEP EXISTING)
    // ============================================================

    public int addVehicle(com.transportportal.model.TransportData.Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (plate_number, model, capacity, status) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet keys = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, vehicle.getIdentifier());
            ps.setString(2, vehicle.getName());
            ps.setInt(3, vehicle.getCapacity());
            ps.setString(4, vehicle.getStatus());
            ps.executeUpdate();
            
            keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            return 0;
        } finally {
            closeQuietly(keys);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    public List<Vehicle> findAllVehicles() throws SQLException {
        List<Vehicle> list = new ArrayList<Vehicle>();
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        String sql = "SELECT id, plate_number, model, capacity FROM vehicles";
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setModel(rs.getString("model"));
                v.setCapacity(rs.getInt("capacity"));
                list.add(v);
            }
        } finally {
            closeQuietly(rs); closeQuietly(ps); closeQuietly(conn);
        }
        return list;
    }

    // ============================================================
    // TRIP CRUD (KEEP EXISTING)
    // ============================================================

   public int addTrip(Trip t) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet keys = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Debug: Check what we're trying to insert
            System.out.println("=== DEBUG TRIP DATA ===");
            System.out.println("Route ID: " + t.getRouteId());
            System.out.println("Driver ID: " + t.getDriverId());
            System.out.println("Vehicle ID: " + t.getVehicleId());
            System.out.println("Departure Time: " + t.getDepartureTime());
            System.out.println("Arrival Time: " + t.getArrivalTime());
            System.out.println("Price: " + t.getPrice());
            System.out.println("Available Seats: " + t.getAvailableSeats());
            System.out.println("Status: " + t.getStatus());
            System.out.println("======================");
            
            // Validate required fields
            if (t.getRouteId() <= 0) {
                throw new SQLException("Route ID is required and must be greater than 0");
            }
            
            if (t.getDepartureTime() == null) {
                throw new SQLException("Departure time is required");
            }
            
            if (t.getPrice() <= 0) {
                throw new SQLException("Price must be greater than 0");
            }
            
            // Use driverId = 0 if not assigned, vehicleId = 0 if not assigned
            int driverId = t.getDriverId() > 0 ? t.getDriverId() : 0;
            int vehicleId = t.getVehicleId() > 0 ? t.getVehicleId() : 0;
            
            // Use the CORRECT SQL that matches your actual database schema
            // Based on your error, the trips table only has: route_id, driver_id, vehicle_id, departure_time, arrival_time, price
            String sql = "INSERT INTO trips (route_id, driver_id, vehicle_id, departure_time, arrival_time, price) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, t.getRouteId());
            
            // Handle driver_id - use NULL in database if 0
            if (driverId > 0) {
                ps.setInt(2, driverId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            
            // Handle vehicle_id - use NULL in database if 0
            if (vehicleId > 0) {
                ps.setInt(3, vehicleId);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            ps.setTimestamp(4, new Timestamp(t.getDepartureTime().getTime()));
            
            // Handle arrival_time - can be null
            if (t.getArrivalTime() != null) {
                ps.setTimestamp(5, new Timestamp(t.getArrivalTime().getTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            
            ps.setDouble(6, t.getPrice());
            
            ps.executeUpdate();
            keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int tripId = keys.getInt(1);
                System.out.println("Successfully created trip with ID: " + tripId);
                return tripId;
            }
        } catch (SQLException e) {
            System.err.println("Error adding trip: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            closeQuietly(keys); 
            closeQuietly(ps); 
            closeQuietly(conn);
        }
        return 0;
    }

    public List<Trip> findTripsByRoute(int routeId) throws SQLException {
        List<Trip> trips = new ArrayList<Trip>();
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        String sql = "SELECT id, route_id, driver_id, vehicle_id, departure_time, arrival_time, price FROM trips WHERE route_id=?";
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, routeId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Trip t = new Trip();
                t.setId(rs.getInt("id"));
                t.setRouteId(rs.getInt("route_id"));
                t.setDriverId(rs.getInt("driver_id"));
                t.setVehicleId(rs.getInt("vehicle_id"));
                t.setDepartureTime(rs.getTimestamp("departure_time"));
                t.setArrivalTime(rs.getTimestamp("arrival_time"));
                t.setPrice(rs.getDouble("price"));
                trips.add(t);
            }
        } finally {
            closeQuietly(rs); closeQuietly(ps); closeQuietly(conn);
        }
        return trips;
    }

    // ============================================================
    // BOOKING CRUD (KEEP EXISTING)
    // ============================================================

    public int addBooking(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings (user_id, trip_id, seat_count, status) VALUES (?, ?, ?, ?)";
        Connection conn = null; PreparedStatement ps = null; ResultSet keys = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, b.getUserId());
            ps.setInt(2, b.getTripId());
            ps.setInt(3, b.getSeatCount());
            ps.setString(4, b.getStatus());
            ps.executeUpdate();
            keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } finally {
            closeQuietly(keys); closeQuietly(ps); closeQuietly(conn);
        }
        return 0;
    }

    public List<Booking> getBookingsForPassenger(int userId) throws SQLException {
        List<Booking> list = new ArrayList<Booking>();
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        String sql = "SELECT * FROM bookings WHERE user_id=?";
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setTripId(rs.getInt("trip_id"));
                b.setSeatCount(rs.getInt("seat_count"));
                b.setStatus(rs.getString("status"));
                list.add(b);
            }
        } finally {
            closeQuietly(rs); closeQuietly(ps); closeQuietly(conn);
        }
        return list;
    }

    // ============================================================
    // PAYMENT CRUD (KEEP EXISTING)
    // ============================================================

    public int addPayment(Payment p) throws SQLException {
        String sql = "INSERT INTO payments (booking_id, amount, method, status) VALUES (?, ?, ?, ?)";
        Connection conn = null; PreparedStatement ps = null; ResultSet keys = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, p.getBookingId());
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getMethod());
            ps.setString(4, p.getStatus());
            ps.executeUpdate();
            keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } finally {
            closeQuietly(keys); closeQuietly(ps); closeQuietly(conn);
        }
        return 0;
    }

    public boolean updatePaymentStatus(int id, String newStatus) throws SQLException {
        String sql = "UPDATE payments SET status=? WHERE id=?";
        Connection conn = null; PreparedStatement ps = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } finally {
            closeQuietly(ps); closeQuietly(conn);
        }
    }

    // ============================================================
    // COMPLETED MISSING METHODS
    // ============================================================

    public List<com.transportportal.model.TransportData.Trip> searchTrips(String origin, String dest, String date) {
        List<com.transportportal.model.TransportData.Trip> trips = new ArrayList<com.transportportal.model.TransportData.Trip>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT t.*, r.origin, r.destination, v.plate_number, " +
                    "CONCAT(r.origin, ' - ', r.destination) as route_name, " +
                    "v.capacity as total_seats, " +
                    "(SELECT COUNT(*) FROM bookings b WHERE b.trip_id = t.id AND b.status != 'CANCELLED') as booked_seats " +
                    "FROM trips t " +
                    "LEFT JOIN routes r ON t.route_id = r.id " +
                    "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                    "WHERE r.origin LIKE ? AND r.destination LIKE ? " +
                    "AND DATE(t.departure_time) = ? " +
                    "AND t.status = 'SCHEDULED' " +
                    "ORDER BY t.departure_time";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + origin + "%");
            ps.setString(2, "%" + dest + "%");
            ps.setString(3, date);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                com.transportportal.model.TransportData.Trip trip = new com.transportportal.model.TransportData.Trip();
                trip.setId(rs.getInt("id"));
                trip.setRouteName(rs.getString("route_name"));
                trip.setVehiclePlate(rs.getString("plate_number")); // FIXED: Now accepts String
                trip.setPrice(rs.getDouble("price"));
                trip.setDepartureTime(rs.getTimestamp("departure_time"));
                trip.setTotalSeats(rs.getInt("total_seats"));
                trip.setBookedSeats(rs.getInt("booked_seats"));
                trips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return trips;
    }

    public void createBooking(int userId, int tripId, int seats, String paymentMethod) {
        Connection conn = null;
        PreparedStatement psBooking = null;
        PreparedStatement psPayment = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Create booking
            String bookingSql = "INSERT INTO bookings (user_id, trip_id, seat_count, status) VALUES (?, ?, ?, 'CONFIRMED')";
            psBooking = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            psBooking.setInt(1, userId);
            psBooking.setInt(2, tripId);
            psBooking.setInt(3, seats);
            psBooking.executeUpdate();
            
            ResultSet keys = psBooking.getGeneratedKeys();
            int bookingId = 0;
            if (keys.next()) {
                bookingId = keys.getInt(1);
            }
            
            // 2. Get trip price
            double amount = 0;
            String priceSql = "SELECT price FROM trips WHERE id = ?";
            PreparedStatement psPrice = conn.prepareStatement(priceSql);
            psPrice.setInt(1, tripId);
            ResultSet rsPrice = psPrice.executeQuery();
            if (rsPrice.next()) {
                amount = rsPrice.getDouble("price") * seats;
            }
            rsPrice.close();
            psPrice.close();
            
            // 3. Create payment
            String paymentSql = "INSERT INTO payments (booking_id, amount, method, status) VALUES (?, ?, ?, 'COMPLETED')";
            psPayment = conn.prepareStatement(paymentSql);
            psPayment.setInt(1, bookingId);
            psPayment.setDouble(2, amount);
            psPayment.setString(3, paymentMethod);
            psPayment.executeUpdate();
            
            conn.commit(); // Commit transaction
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            closeQuietly(psPayment);
            closeQuietly(psBooking);
            closeQuietly(conn);
        }
    }

    public void cancelBooking(int bookingId) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);
            
            // Update booking status
            String bookingSql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
            ps = conn.prepareStatement(bookingSql);
            ps.setInt(1, bookingId);
            ps.executeUpdate();
            ps.close();
            
            // Update payment status if exists
            String paymentSql = "UPDATE payments SET status = 'REFUNDED' WHERE booking_id = ?";
            ps = conn.prepareStatement(paymentSql);
            ps.setInt(1, bookingId);
            ps.executeUpdate();
            
            conn.commit();
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    public List<Booking> findBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<Booking>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT b.*, t.departure_time, t.price, " +
                    "CONCAT(r.origin, ' - ', r.destination) as route_name, " +
                    "v.plate_number as vehicle_plate " +
                    "FROM bookings b " +
                    "LEFT JOIN trips t ON b.trip_id = t.id " +
                    "LEFT JOIN routes r ON t.route_id = r.id " +
                    "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                    "WHERE b.user_id = ? " +
                    "ORDER BY b.created_at DESC";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setTripId(rs.getInt("trip_id"));
                booking.setSeatCount(rs.getInt("seat_count"));
                booking.setStatus(rs.getString("status"));
                
                // Set additional display info
                if (booking instanceof com.transportportal.model.Booking) {
                    ((com.transportportal.model.Booking) booking).setRouteName(rs.getString("route_name"));
                    ((com.transportportal.model.Booking) booking).setVehiclePlate(rs.getString("vehicle_plate"));
                    ((com.transportportal.model.Booking) booking).setDepartureTime(rs.getTimestamp("departure_time"));
                    ((com.transportportal.model.Booking) booking).setTotalPrice(rs.getDouble("price") * booking.getSeatCount());
                }
                
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return bookings;
    }

    public List<com.transportportal.model.TransportData.Route> searchRoutes(String from, String to) {
        List<com.transportportal.model.TransportData.Route> routes = new ArrayList<com.transportportal.model.TransportData.Route>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT * FROM routes WHERE origin LIKE ? AND destination LIKE ?";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + from + "%");
            ps.setString(2, "%" + to + "%");
            rs = ps.executeQuery();
            
            while (rs.next()) {
                com.transportportal.model.TransportData.Route route = new com.transportportal.model.TransportData.Route();
                route.setId(rs.getInt("id"));
                route.setOrigin(rs.getString("origin"));
                route.setDestination(rs.getString("destination"));
                route.setDistanceKm(rs.getDouble("distance_km"));
                routes.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return routes;
    }

    public int addDriver(com.transportportal.model.TransportData.Driver driver) {
        String sql = "INSERT INTO drivers (user_id, license_number, experience_years, status) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet keys = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, driver.getUserId());
            ps.setString(2, driver.getLicenseNumber());
            ps.setInt(3, driver.getExperienceYears());
            ps.setString(4, driver.getStatus());
            ps.executeUpdate();
            
            keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(keys);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return 0;
    }

    // ============================================================
    // DRIVER & VEHICLE DATABASE METHODS
    // ============================================================

    public List<com.transportportal.model.TransportData.Driver> getAllDrivers() throws SQLException {
        List<com.transportportal.model.TransportData.Driver> drivers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT d.id, d.license_number, d.experience_years, d.status, " +
                    "u.full_name, u.contact, u.email, u.id as user_id " +
                    "FROM drivers d " +
                    "JOIN users u ON d.user_id = u.id " +
                    "WHERE u.role = 'DRIVER' " +  // Ensure only DRIVER role users
                    "ORDER BY d.id";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                com.transportportal.model.TransportData.Driver driver = new com.transportportal.model.TransportData.Driver();
                driver.setDriverID(rs.getInt("id"));
                driver.setName(rs.getString("full_name"));
                driver.setLicenseNumber(rs.getString("license_number"));
                driver.setContact(rs.getString("contact"));
                driver.setExperienceYears(rs.getInt("experience_years"));
                driver.setStatus(rs.getString("status"));
                driver.setUserId(rs.getInt("user_id"));
                drivers.add(driver);
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return drivers;
    }
    
    public List<com.transportportal.model.TransportData.Vehicle> getAllVehicles() throws SQLException {
        List<com.transportportal.model.TransportData.Vehicle> vehicles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT id, plate_number, model, capacity, status FROM vehicles ORDER BY id";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                com.transportportal.model.TransportData.Vehicle vehicle = new com.transportportal.model.TransportData.Vehicle();
                vehicle.setVehicleID(rs.getInt("id"));
                vehicle.setIdentifier(rs.getString("plate_number"));
                vehicle.setName(rs.getString("model"));
                vehicle.setCapacity(rs.getInt("capacity"));
                vehicle.setStatus(rs.getString("status"));
                vehicles.add(vehicle);
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return vehicles;
    }
    
    public com.transportportal.model.TransportData.Driver saveDriver(com.transportportal.model.TransportData.Driver driver) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet keys = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            if (driver.getDriverID() == 0) {
                // Insert new driver
                String sql = "INSERT INTO drivers (user_id, license_number, experience_years, status) VALUES (?, ?, ?, ?)";
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, driver.getUserId());
                ps.setString(2, driver.getLicenseNumber());
                ps.setInt(3, driver.getExperienceYears());
                ps.setString(4, driver.getStatus());
                ps.executeUpdate();
                
                keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    driver.setDriverID(keys.getInt(1));
                }
            } else {
                // Update existing driver
                String sql = "UPDATE drivers SET license_number = ?, experience_years = ?, status = ? WHERE id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, driver.getLicenseNumber());
                ps.setInt(2, driver.getExperienceYears());
                ps.setString(3, driver.getStatus());
                ps.setInt(4, driver.getDriverID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(keys);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return driver;
    }
    
    public com.transportportal.model.TransportData.Vehicle saveVehicle(com.transportportal.model.TransportData.Vehicle vehicle) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet keys = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            if (vehicle.getVehicleID() == 0) {
                // Insert new vehicle
                String sql = "INSERT INTO vehicles (plate_number, model, capacity) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, vehicle.getIdentifier());
                ps.setString(2, vehicle.getName());
                ps.setInt(3, vehicle.getCapacity());
                ps.executeUpdate();
                
                keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    vehicle.setVehicleID(keys.getInt(1));
                }
            } else {
                // Update existing vehicle
                String sql = "UPDATE vehicles SET plate_number = ?, model = ?, capacity = ? WHERE id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, vehicle.getIdentifier());
                ps.setString(2, vehicle.getName());
                ps.setInt(3, vehicle.getCapacity());
                ps.setInt(4, vehicle.getVehicleID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(keys);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return vehicle;
    }
    
    public boolean deleteDriver(int driverId) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, driverId);
            return ps.executeUpdate() > 0;
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    
    public boolean deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    public boolean updateDriver(com.transportportal.model.TransportData.Driver driver) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "UPDATE drivers SET license_number = ?, experience_years = ?, status = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, driver.getLicenseNumber());
            ps.setInt(2, driver.getExperienceYears());
            ps.setString(3, driver.getStatus());
            ps.setInt(4, driver.getDriverID());
            return ps.executeUpdate() > 0;
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    public boolean updateVehicle(com.transportportal.model.TransportData.Vehicle vehicle) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "UPDATE vehicles SET plate_number = ?, model = ?, capacity = ?, status = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, vehicle.getIdentifier());
            ps.setString(2, vehicle.getName());
            ps.setInt(3, vehicle.getCapacity());
            ps.setString(4, vehicle.getStatus());
            ps.setInt(5, vehicle.getVehicleID());
            return ps.executeUpdate() > 0;
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    // ============================================================
    // EXISTING METHODS (KEEP AS IS)
    // ============================================================

    // Update trip status
    public void updateTripStatus(int tripId, String newStatus) throws SQLException {
        TripDAO.updateStatus(tripId, newStatus);
    }

    // Get bookings by trip
    public List<Booking> getBookingsByTrip(int tripId) throws SQLException {
        return BookingDAO.findByTrip(tripId);
    }

    // Driver trip history
    public List<Trip> getDriverTripHistory(int driverId) throws SQLException {
        return TripDAO.findHistoryByDriver(driverId);
    }
    
 // Add these methods to your TransportService class:

    public int addDriverWithUser(com.transportportal.model.TransportData.Driver driver) throws SQLException {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psDriver = null;
        ResultSet keys = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // Start transaction
            
            // 1. First create a user account for the driver
            String userSql = "INSERT INTO users (username, password, full_name, contact, role) VALUES (?, ?, ?, ?, 'DRIVER')";
            psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            
            // Generate username from name (you might want to make this more robust)
            String username = driver.getName().toLowerCase().replace(" ", "") + System.currentTimeMillis() % 1000;
            String defaultPassword = "driver123"; // You should hash this in production
            
            psUser.setString(1, username);
            psUser.setString(2, defaultPassword);
            psUser.setString(3, driver.getName());
            psUser.setString(4, driver.getContact());
            psUser.executeUpdate();
            
            int userId = 0;
            keys = psUser.getGeneratedKeys();
            if (keys.next()) {
                userId = keys.getInt(1);
            }
            keys.close();
            
            // 2. Then create the driver record
            String driverSql = "INSERT INTO drivers (user_id, license_number, experience_years, status) VALUES (?, ?, ?, ?)";
            psDriver = conn.prepareStatement(driverSql, Statement.RETURN_GENERATED_KEYS);
            psDriver.setInt(1, userId);
            psDriver.setString(2, driver.getLicenseNumber());
            psDriver.setInt(3, driver.getExperienceYears());
            psDriver.setString(4, driver.getStatus());
            psDriver.executeUpdate();
            
            int driverId = 0;
            keys = psDriver.getGeneratedKeys();
            if (keys.next()) {
                driverId = keys.getInt(1);
            }
            
            conn.commit(); // Commit transaction
            return driverId;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback on error
            }
            throw e;
        } finally {
            closeQuietly(keys);
            closeQuietly(psDriver);
            closeQuietly(psUser);
            closeQuietly(conn);
        }
    }

    
 // Add these methods to TransportService for driver-vehicle assignment

    public boolean assignDriverToVehicle(int driverId, int vehicleId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // First, deactivate any existing active assignment for this driver
            String deactivateSql = "UPDATE driver_vehicle_assignments SET status = 'INACTIVE' WHERE driver_id = ? AND status = 'ACTIVE'";
            ps = conn.prepareStatement(deactivateSql);
            ps.setInt(1, driverId);
            ps.executeUpdate();
            ps.close();
            
            // Also, deactivate any existing active assignment for this vehicle
            deactivateSql = "UPDATE driver_vehicle_assignments SET status = 'INACTIVE' WHERE vehicle_id = ? AND status = 'ACTIVE'";
            ps = conn.prepareStatement(deactivateSql);
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
            ps.close();
            
            // Create new active assignment
            String assignSql = "INSERT INTO driver_vehicle_assignments (driver_id, vehicle_id, status) VALUES (?, ?, 'ACTIVE')";
            ps = conn.prepareStatement(assignSql);
            ps.setInt(1, driverId);
            ps.setInt(2, vehicleId);
            
            return ps.executeUpdate() > 0;
            
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    public com.transportportal.model.TransportData.Vehicle getAssignedVehicleForDriver(int driverId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT v.id, v.plate_number, v.model, v.capacity, v.status " +
                     "FROM vehicles v " +
                     "JOIN driver_vehicle_assignments dva ON v.id = dva.vehicle_id " +
                     "WHERE dva.driver_id = ? AND dva.status = 'ACTIVE' " +
                     "LIMIT 1";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, driverId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                com.transportportal.model.TransportData.Vehicle vehicle = new com.transportportal.model.TransportData.Vehicle();
                vehicle.setVehicleID(rs.getInt("id"));
                vehicle.setIdentifier(rs.getString("plate_number"));
                vehicle.setName(rs.getString("model"));
                vehicle.setCapacity(rs.getInt("capacity"));
                vehicle.setStatus(rs.getString("status"));
                return vehicle;
            }
            return null;
            
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    public com.transportportal.model.TransportData.Driver getDriverForVehicle(int vehicleId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT d.id, d.license_number, d.experience_years, d.status, " +
                     "u.full_name, u.contact " +
                     "FROM drivers d " +
                     "JOIN users u ON d.user_id = u.id " +
                     "JOIN driver_vehicle_assignments dva ON d.id = dva.driver_id " +
                     "WHERE dva.vehicle_id = ? AND dva.status = 'ACTIVE' " +
                     "LIMIT 1";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, vehicleId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                com.transportportal.model.TransportData.Driver driver = new com.transportportal.model.TransportData.Driver();
                driver.setDriverID(rs.getInt("id"));
                driver.setName(rs.getString("full_name"));
                driver.setLicenseNumber(rs.getString("license_number"));
                driver.setContact(rs.getString("contact"));
                driver.setExperienceYears(rs.getInt("experience_years"));
                driver.setStatus(rs.getString("status"));
                return driver;
            }
            return null;
            
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    public List<Object[]> getCurrentAssignments() throws SQLException {
        List<Object[]> assignments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT d.id as driver_id, u.full_name as driver_name, " +
                     "v.id as vehicle_id, v.plate_number, v.model, " +
                     "dva.assigned_date " +
                     "FROM driver_vehicle_assignments dva " +
                     "JOIN drivers d ON dva.driver_id = d.id " +
                     "JOIN users u ON d.user_id = u.id " +
                     "JOIN vehicles v ON dva.vehicle_id = v.id " +
                     "WHERE dva.status = 'ACTIVE' " +
                     "ORDER BY dva.assigned_date DESC";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] assignment = new Object[]{
                    rs.getInt("driver_id"),
                    rs.getString("driver_name"),
                    rs.getInt("vehicle_id"),
                    rs.getString("plate_number"),
                    rs.getString("model"),
                    rs.getTimestamp("assigned_date")
                };
                assignments.add(assignment);
            }
            return assignments;
            
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    
    public int findDriverIdByUserId(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT id FROM drivers WHERE user_id = ?";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
            
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    
    public boolean convertUserToDriver(int userId, String licenseNumber, int experienceYears) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // First check if user exists and is not already a driver
            String checkSql = "SELECT u.id, u.role, d.id as driver_id FROM users u LEFT JOIN drivers d ON u.id = d.user_id WHERE u.id = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                // Check if user is already a driver
                if (rs.getInt("driver_id") > 0) {
                    throw new SQLException("User is already registered as a driver");
                }
                
                // Check if user role is DRIVER
                if (!"DRIVER".equalsIgnoreCase(rs.getString("role"))) {
                    throw new SQLException("User role is not DRIVER");
                }
                
                rs.close();
                ps.close();
                
                // Insert into drivers table
                String driverSql = "INSERT INTO drivers (user_id, license_number, experience_years, status) VALUES (?, ?, ?, ?)";
                ps = conn.prepareStatement(driverSql);
                ps.setInt(1, userId);
                ps.setString(2, licenseNumber);
                ps.setInt(3, experienceYears);
                ps.setString(4, "ACTIVE");
                
                return ps.executeUpdate() > 0;
            }
            
            return false;
            
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    
    
    public void debugCheckUser(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = null; 
        PreparedStatement ps = null; 
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                System.out.println("=== DATABASE USER INFO ===");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Password: " + rs.getString("password"));
                System.out.println("Role: " + rs.getString("role"));
                System.out.println("Full Name: " + rs.getString("full_name"));
                System.out.println("Contact: " + rs.getString("contact"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("==========================");
            } else {
                System.out.println("❌ User not found in database: " + username);
            }
        } finally {
            closeQuietly(rs); 
            closeQuietly(ps); 
            closeQuietly(conn);
        }
    }
    
    public void testLogin(String username, String password, String role) {
        try {
            System.out.println("🧪 TESTING LOGIN:");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("Role: " + role);
            
            // First check what's in database
            debugCheckUser(username);
            
            // Then try to authenticate
            User user = authenticate(username, password, role);
            if (user != null) {
                System.out.println("✅ LOGIN SUCCESS!");
            } else {
                System.out.println("❌ LOGIN FAILED!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // ============================================================
    // Utility
    // ============================================================

    private void closeQuietly(AutoCloseable c) {
        if (c != null) {
            try { c.close(); } catch (Exception ignore) {}
        }
    }
    
    
 // Add these methods to your TransportService class

    /**
     * Get all trips from database
     */
    public List<Trip> getAllTrips() throws SQLException {
        List<Trip> trips = new ArrayList<Trip>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT t.*, r.origin, r.destination, " +
                     "u.full_name as driver_name, v.plate_number, v.model " +
                     "FROM trips t " +
                     "LEFT JOIN routes r ON t.route_id = r.id " +
                     "LEFT JOIN drivers d ON t.driver_id = d.id " +
                     "LEFT JOIN users u ON d.user_id = u.id " +
                     "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                     "ORDER BY t.departure_time DESC";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setRouteId(rs.getInt("route_id"));
                trip.setDriverId(rs.getInt("driver_id"));
                trip.setVehicleId(rs.getInt("vehicle_id"));
                trip.setDepartureTime(rs.getTimestamp("departure_time"));
                trip.setArrivalTime(rs.getTimestamp("arrival_time"));
                trip.setPrice(rs.getDouble("price"));
                trips.add(trip);
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return trips;
    }

    /**
     * Get passenger count for a specific trip
     */
    public int getPassengerCountForTrip(int tripId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT COUNT(*) as passenger_count FROM bookings WHERE trip_id = ? AND status = 'CONFIRMED'";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("passenger_count");
            }
            return 0;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    public int registerDriverWithDetails(User user, String licenseNumber, int experienceYears) throws SQLException {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psDriver = null;
        ResultSet keys = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // Start transaction
            
            // 1. First create a user account for the driver
            String userSql = "INSERT INTO users (username, password, full_name, contact, email, role) VALUES (?, ?, ?, ?, ?, 'DRIVER')";
            psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            
            psUser.setString(1, user.getUsername());
            psUser.setString(2, user.getPassword());
            psUser.setString(3, user.getFullName());
            psUser.setString(4, user.getPhone());
            psUser.setString(5, user.getEmail());
            psUser.executeUpdate();
            
            int userId = 0;
            keys = psUser.getGeneratedKeys();
            if (keys.next()) {
                userId = keys.getInt(1);
            }
            keys.close();
            
            // 2. Then create the driver record - USE ONLY ALLOWED ENUM VALUES
            String driverSql = "INSERT INTO drivers (user_id, license_number, experience_years, status) VALUES (?, ?, ?, ?)";
            psDriver = conn.prepareStatement(driverSql, Statement.RETURN_GENERATED_KEYS);
            psDriver.setInt(1, userId);
            psDriver.setString(2, licenseNumber);
            psDriver.setInt(3, experienceYears);
            
            // MUST USE ONE OF THESE EXACT VALUES: 'ACTIVE', 'INACTIVE', 'SUSPENDED'
            // For new drivers, use 'ACTIVE' or 'INACTIVE' (not 'PENDING')
            String driverStatus = "ACTIVE"; // Use allowed ENUM value
            
            psDriver.setString(4, driverStatus);
            psDriver.executeUpdate();
            
            int driverId = 0;
            keys = psDriver.getGeneratedKeys();
            if (keys.next()) {
                driverId = keys.getInt(1);
            }
            
            conn.commit(); // Commit transaction
            return driverId;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback on error
            }
            throw e;
        } finally {
            closeQuietly(keys);
            closeQuietly(psDriver);
            closeQuietly(psUser);
            closeQuietly(conn);
        }
    }
    /**
     * Get trips assigned to a specific driver
     */
    public List<Trip> getTripsByDriver(int driverId) throws SQLException {
        List<Trip> trips = new ArrayList<Trip>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT t.*, r.origin, r.destination, v.plate_number, v.model " +
                     "FROM trips t " +
                     "LEFT JOIN routes r ON t.route_id = r.id " +
                     "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                     "WHERE t.driver_id = ? " +
                     "ORDER BY t.departure_time ASC";
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
                trip.setPrice(rs.getDouble("price"));
                trips.add(trip);
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
        return trips;
    }

   
}