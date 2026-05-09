package com.restaurantdeliverysystem.dao;

import com.restaurantdeliverysystem.model.Vendor;
import com.restaurantdeliverysystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendorDAO {

    public List<Vendor> getAllVendors() throws SQLException {
        List<Vendor> list = new ArrayList<>();
        String sql = "SELECT * FROM vendors ORDER BY name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Vendor getById(int id) throws SQLException {
        String sql = "SELECT * FROM vendors WHERE vendor_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public void insert(Vendor v) throws SQLException {
        String sql = "INSERT INTO vendors (name, address, phone_number, cuisine_type) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, v.getName());
            ps.setString(2, v.getAddress());
            ps.setString(3, v.getPhoneNumber());
            ps.setString(4, v.getCuisineType());
            ps.executeUpdate();
        }
    }

    public void update(Vendor v) throws SQLException {
        String sql = "UPDATE vendors SET name=?, address=?, phone_number=?, cuisine_type=? WHERE vendor_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, v.getName());
            ps.setString(2, v.getAddress());
            ps.setString(3, v.getPhoneNumber());
            ps.setString(4, v.getCuisineType());
            ps.setInt(5, v.getVendorId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM vendors WHERE vendor_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Vendor map(ResultSet rs) throws SQLException {
        return new Vendor(
            rs.getInt("vendor_id"),
            rs.getString("name"),
            rs.getString("address"),
            rs.getString("phone_number"),
            rs.getString("cuisine_type")
        );
    }
}
