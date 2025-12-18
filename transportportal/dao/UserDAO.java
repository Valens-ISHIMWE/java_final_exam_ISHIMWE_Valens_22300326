package com.transportportal.dao;

import com.transportportal.config.DBConnection;
import com.transportportal.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {

    // CREATE
    public int create(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getPhone());
            int rows = ps.executeUpdate();
            if (rows == 0) return -1;
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                u.setId(id);
                return id;
            }
            return -1;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    // READ by id
    public User findById(int id) throws SQLException {
        String sql = "SELECT id, username, password, full_name, role, email, phone, created_at FROM users WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    // READ by username (useful for login)
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, full_name, role, email, phone, created_at FROM users WHERE username = ? LIMIT 1";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    // LIST all
    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, username, password, full_name, role, email, phone, created_at FROM users ORDER BY id";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<User>();
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    // UPDATE
    public boolean update(User u) throws SQLException {
        String sql = "UPDATE users SET username=?, password=?, full_name=?, role=?, email=?, phone=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getPhone());
            ps.setInt(7, u.getId());
            return ps.executeUpdate() > 0;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    // DELETE
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    // helper
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(rs.getString("role"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
