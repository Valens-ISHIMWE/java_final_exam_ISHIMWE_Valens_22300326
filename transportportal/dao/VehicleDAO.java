package com.transportportal.dao;

import com.transportportal.config.DBConnection;
import com.transportportal.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class VehicleDAO {

    public int create(Vehicle v) throws SQLException {
        String sql = "INSERT INTO vehicles (plate_number, model, capacity, status) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        try {
            ps.setString(1, v.getPlateNumber());
            ps.setString(2, v.getModel());
            ps.setInt(3, v.getCapacity());
            ps.setString(4, v.getStatus());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                v.setId(rs.getInt(1));
                return v.getId();
            }
            return -1;
        } finally {
            ps.close();
        }
    }

    public Vehicle findById(int id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE id=?";
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

    public List<Vehicle> findAll() throws SQLException {
        String sql = "SELECT * FROM vehicles ORDER BY id";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Vehicle> list = new ArrayList<Vehicle>();
        try {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } finally {
            rs.close();
            ps.close();
        }
    }

    public boolean update(Vehicle v) throws SQLException {
        String sql = "UPDATE vehicles SET plate_number=?, model=?, capacity=?, status=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setString(1, v.getPlateNumber());
            ps.setString(2, v.getModel());
            ps.setInt(3, v.getCapacity());
            ps.setString(4, v.getStatus());
            ps.setInt(5, v.getId());
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setId(rs.getInt("id"));
        v.setPlateNumber(rs.getString("plate_number"));
        v.setModel(rs.getString("model"));
        v.setCapacity(rs.getInt("capacity"));
        v.setStatus(rs.getString("status"));
        return v;
    }
}
