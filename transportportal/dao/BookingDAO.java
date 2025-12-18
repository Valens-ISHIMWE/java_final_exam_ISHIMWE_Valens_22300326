package com.transportportal.dao;

import com.transportportal.config.DBConnection;
import com.transportportal.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BookingDAO {

    public static int create(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings (trip_id, user_id, seats, total_price, status, booked_at) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        try {
            ps.setInt(1, b.getTripId());
            ps.setInt(2, b.getUserId());
            ps.setInt(3, b.getSeats());
            ps.setDouble(4, b.getTotalPrice());
            ps.setString(5, b.getStatus());
            ps.setTimestamp(6, b.getBookedAt() != null ? b.getBookedAt() : new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                b.setId(id);
                return id;
            }
            return -1;
        } finally {
            ps.close();
            conn.close();
        }
    }

    public static List<Booking> findByUser(int userId) throws SQLException {
        String sql = "SELECT b.*, t.departure_time, r.origin, r.destination, v.plate_number " +
                    "FROM bookings b " +
                    "JOIN trips t ON b.trip_id = t.id " +
                    "JOIN routes r ON t.route_id = r.id " +
                    "JOIN vehicles v ON t.vehicle_id = v.id " +
                    "WHERE b.user_id = ? " +
                    "ORDER BY b.booked_at DESC";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        List<Booking> list = new ArrayList<Booking>();
        try {
            while (rs.next()) {
                Booking booking = mapRow(rs);
                // Set additional display information
                booking.setRouteName(rs.getString("origin") + " - " + rs.getString("destination"));
                booking.setVehiclePlate(rs.getString("plate_number"));
                booking.setDepartureTime(rs.getTimestamp("departure_time"));
                list.add(booking);
            }
            return list;
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
    }

    public static List<Booking> findByTrip(int tripId) throws SQLException {
        String sql = "SELECT b.*, u.full_name as passenger_name " +
                    "FROM bookings b " +
                    "JOIN users u ON b.user_id = u.id " +
                    "WHERE b.trip_id = ? AND b.status != 'CANCELLED' " +
                    "ORDER BY b.booked_at DESC";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, tripId);
        ResultSet rs = ps.executeQuery();
        List<Booking> list = new ArrayList<Booking>();
        try {
            while (rs.next()) {
                Booking booking = mapRow(rs);
                booking.setPassengerName(rs.getString("passenger_name"));
                list.add(booking);
            }
            return list;
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
    }

    public static boolean updateStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE bookings SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
            conn.close();
        }
    }

    public static boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM bookings WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
            conn.close();
        }
    }

    private static Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setTripId(rs.getInt("trip_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setSeats(rs.getInt("seats"));
        b.setTotalPrice(rs.getDouble("total_price"));
        b.setStatus(rs.getString("status"));
        b.setBookedAt(rs.getTimestamp("booked_at"));
        return b;
    }
}