package com.restaurantdeliverysystem.dao;

import com.restaurantdeliverysystem.util.DBConnection;

import java.sql.*;

public class OrderItemDAO {

    public void insert(int orderId, int itemId, int quantity, double unitPrice) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, itemId);
            ps.setInt(3, quantity);
            ps.setDouble(4, unitPrice);
            ps.executeUpdate();
        }
    }

    public ResultSet getByOrder(int orderId) throws SQLException {
        String sql =
            "SELECT oi.quantity, oi.unit_price, mi.item_name " +
            "FROM order_items oi " +
            "JOIN menu_items mi ON oi.item_id = mi.item_id " +
            "WHERE oi.order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, orderId);
        return ps.executeQuery();
    }

    public void deleteByOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }
}
