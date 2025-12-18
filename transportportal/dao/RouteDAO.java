package com.transportportal.dao;

import com.transportportal.config.DBConnection;
import com.transportportal.model.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RouteDAO {

    public int create(Route r) throws SQLException {
        String sql = "INSERT INTO routes (origin, destination, distance_km, base_fare) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        try {
            ps.setString(1, r.getOrigin());
            ps.setString(2, r.getDestination());
            ps.setDouble(3, r.getDistanceKm());
            ps.setDouble(4, r.getBaseFare());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                r.setId(rs.getInt(1));
                return r.getId();
            }
            return -1;
        } finally {
            ps.close();
        }
    }

    public Route findById(int id) throws SQLException {
        String sql = "SELECT * FROM routes WHERE id=?";
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

    public List<Route> findAll() throws SQLException {
        String sql = "SELECT * FROM routes ORDER BY id";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Route> list = new ArrayList<Route>();
        try {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } finally {
            rs.close();
            ps.close();
        }
    }

    public boolean update(Route r) throws SQLException {
        String sql = "UPDATE routes SET origin=?, destination=?, distance_km=?, base_fare=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setString(1, r.getOrigin());
            ps.setString(2, r.getDestination());
            ps.setDouble(3, r.getDistanceKm());
            ps.setDouble(4, r.getBaseFare());
            ps.setInt(5, r.getId());
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM routes WHERE id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    private Route mapRow(ResultSet rs) throws SQLException {
        Route r = new Route();
        r.setId(rs.getInt("id"));
        r.setOrigin(rs.getString("origin"));
        r.setDestination(rs.getString("destination"));
        r.setDistanceKm(rs.getDouble("distance_km"));
        r.setBaseFare(rs.getDouble("base_fare"));
        return r;
    }
}
