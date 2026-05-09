package com.restaurantdeliverysystem.dao;

import com.restaurantdeliverysystem.model.Driver;
import com.restaurantdeliverysystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {

    public List<Driver> getAllDrivers() throws SQLException {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT * FROM drivers ORDER BY last_name, first_name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Driver> getAvailableDrivers() throws SQLException {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE status = 'available' ORDER BY last_name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(Driver d) throws SQLException {
        String sql = "INSERT INTO drivers (first_name, last_name, phone_number, status) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getPhoneNumber());
            ps.setString(4, d.getStatus());
            ps.executeUpdate();
        }
    }

    public void update(Driver d) throws SQLException {
        String sql = "UPDATE drivers SET first_name=?, last_name=?, phone_number=?, status=? WHERE driver_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getPhoneNumber());
            ps.setString(4, d.getStatus());
            ps.setInt(5, d.getDriverId());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int driverId, String status) throws SQLException {
        String sql = "UPDATE drivers SET status=? WHERE driver_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, driverId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE driver_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Driver map(ResultSet rs) throws SQLException {
        return new Driver(
            rs.getInt("driver_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("phone_number"),
            rs.getString("status")
        );
    }
}
