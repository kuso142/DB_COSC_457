package com.restaurantdeliverysystem.dao;

import com.restaurantdeliverysystem.util.DBConnection;

import java.sql.*;

/**
 * Data Access Object for order items [relates to OrderDetails in the schema??]. Provides methods to insert, retrieve, and delete order items from the database.
 */
public class OrderItemDAO {

    /** Inserts a new order item into the database. */
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

    /** Retrieves all order items for a specific order. */
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

    /** Deletes all order items for a specific order. */
    public void deleteByOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }
}
