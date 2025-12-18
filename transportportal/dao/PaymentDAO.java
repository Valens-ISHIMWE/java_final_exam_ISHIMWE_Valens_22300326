package com.transportportal.dao;

import com.transportportal.config.DBConnection;
import com.transportportal.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PaymentDAO {

    public int create(Payment p) throws SQLException {
        String sql = "INSERT INTO payments (booking_id, amount, method, status, paid_at) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        try {
            ps.setInt(1, p.getBookingId());
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getMethod());
            ps.setString(4, p.getStatus());
            ps.setTimestamp(5, p.getPaidAt());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                p.setId(rs.getInt(1));
                return p.getId();
            }
            return -1;
        } finally {
            ps.close();
        }
    }

    public Payment findById(int id) throws SQLException {
        String sql = "SELECT * FROM payments WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) return mapRow(rs);
            return null;
        } finally {
            rs.close();
            ps.close();
        }
    }

    public List<Payment> findAll() throws SQLException {
        String sql = "SELECT * FROM payments ORDER BY id";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Payment> list = new ArrayList<Payment>();
        try {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } finally {
            rs.close();
            ps.close();
        }
    }

    public boolean update(Payment p) throws SQLException {
        String sql = "UPDATE payments SET booking_id=?, amount=?, method=?, status=?, paid_at=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, p.getBookingId());
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getMethod());
            ps.setString(4, p.getStatus());
            ps.setTimestamp(5, p.getPaidAt());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM payments WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id"));
        p.setBookingId(rs.getInt("booking_id"));
        p.setAmount(rs.getDouble("amount"));
        p.setMethod(rs.getString("method"));
        p.setStatus(rs.getString("status"));
        p.setPaidAt(rs.getTimestamp("paid_at"));
        return p;
    }
}
