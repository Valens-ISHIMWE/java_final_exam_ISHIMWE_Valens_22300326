package com.transportportal.dao;

import com.transportportal.config.DBConnection;
import com.transportportal.model.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DriverDAO {

    // CREATE
    public int create(Driver d) throws SQLException {
        String sql = "INSERT INTO drivers (full_name, license_number, phone, status) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, d.getFullName());
            ps.setString(2, d.getLicenseNumber());
            ps.setString(3, d.getPhone());
            ps.setString(4, d.getStatus());
            int rows = ps.executeUpdate();
            if (rows == 0) return -1;
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                d.setId(id);
                return id;
            }
            return -1;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    // READ by ID
    public Driver findById(int id) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE id = ?";
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

    // LIST ALL
    public List<Driver> findAll() throws SQLException {
        String sql = "SELECT * FROM drivers ORDER BY id";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Driver> list = new ArrayList<Driver>();
        try {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } finally {
            rs.close();
            ps.close();
        }
    }

    // UPDATE
    public boolean update(Driver d) throws SQLException {
        String sql = "UPDATE drivers SET full_name=?, license_number=?, phone=?, status=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setString(1, d.getFullName());
            ps.setString(2, d.getLicenseNumber());
            ps.setString(3, d.getPhone());
            ps.setString(4, d.getStatus());
            ps.setInt(5, d.getId());
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    // DELETE
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    private Driver mapRow(ResultSet rs) throws SQLException {
        Driver d = new Driver();
        d.setId(rs.getInt("id"));
        d.setFullName(rs.getString("full_name"));
        d.setLicenseNumber(rs.getString("license_number"));
        d.setPhone(rs.getString("phone"));
        d.setStatus(rs.getString("status"));
        return d;
    }
}
