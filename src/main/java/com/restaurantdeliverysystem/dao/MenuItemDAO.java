package com.restaurantdeliverysystem.dao;

import com.restaurantdeliverysystem.model.MenuItem;
import com.restaurantdeliverysystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    public List<MenuItem> getByVendor(int vendorId) throws SQLException {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE vendor_id = ? ORDER BY item_name";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<MenuItem> getAllItems() throws SQLException {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items ORDER BY vendor_id, item_name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(MenuItem m) throws SQLException {
        String sql = "INSERT INTO menu_items (vendor_id, item_name, price, description) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, m.getVendorId());
            ps.setString(2, m.getItemName());
            ps.setDouble(3, m.getPrice());
            ps.setString(4, m.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(MenuItem m) throws SQLException {
        String sql = "UPDATE menu_items SET item_name=?, price=?, description=? WHERE item_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, m.getItemName());
            ps.setDouble(2, m.getPrice());
            ps.setString(3, m.getDescription());
            ps.setInt(4, m.getItemId());
            ps.executeUpdate();
        }
    }

    public void delete(int itemId) throws SQLException {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        }
    }

    private MenuItem map(ResultSet rs) throws SQLException {
        return new MenuItem(
            rs.getInt("item_id"),
            rs.getInt("vendor_id"),
            rs.getString("item_name"),
            rs.getDouble("price"),
            rs.getString("description")
        );
    }
}
