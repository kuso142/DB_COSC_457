package com.restaurantdeliverysystem.dao;

import com.restaurantdeliverysystem.model.Order;
import com.restaurantdeliverysystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object for Order entity.
    Provides methods to perform CRUD operations on the orders table in the database,
    as well as some aggregate queries for reporting. 
*/
public class OrderDAO {

    /** Retrieves all orders from the database, ordered by order time descending. */
    public List<Order> getAllOrders() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_time DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Retrieves all orders for a specific customer, ordered by order time descending. */
    public List<Order> getByCustomer(int customerId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_time DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /** Insert a new order and return the generated order_id. */
    public int insert(Order o) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, vendor_id, driver_id, status, total_amount) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getCustomerId());
            ps.setInt(2, o.getVendorId());
            ps.setInt(3, o.getDriverId());
            ps.setString(4, o.getStatus());
            ps.setDouble(5, o.getTotalAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
    /** Updates the status of an existing order.
     * @param orderId The ID of the order to update.
     * @param status The new status to set (e.g., "Pending", "In Progress", "Delivered", "Cancelled").
     */
    public void updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status=? WHERE order_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    /** Deletes an order from the database by its ID. */
    public void delete(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    // ----- aggregate / join queries (used in Admin panel) -----
    /** Retrieves a summary of total orders and revenue by vendor and ordered by revenue. */
    public ResultSet getOrderSummaryByVendor() throws SQLException {
        String sql =
            "SELECT v.name AS vendor_name, COUNT(o.order_id) AS total_orders, " +
            "       SUM(o.total_amount) AS total_revenue " +
            "FROM orders o " +
            "JOIN vendors v ON o.vendor_id = v.vendor_id " +
            "GROUP BY v.vendor_id, v.name " +
            "ORDER BY total_revenue DESC";
        return DBConnection.getConnection().createStatement().executeQuery(sql);
    }

    /** Retrieves the count of orders grouped by their status. */
    public ResultSet getOrderCountByStatus() throws SQLException {
        String sql =
            "SELECT status, COUNT(*) AS count FROM orders GROUP BY status";
        return DBConnection.getConnection().createStatement().executeQuery(sql);
    }

    /** Retrieves recent orders along with customer, vendor, and driver details for display in the admin panel. */
    public ResultSet getRecentOrdersWithDetails() throws SQLException {
        String sql =
            "SELECT o.order_id, c.first_name, c.last_name, v.name AS vendor_name, " +
            "       d.first_name AS driver_first, d.last_name AS driver_last, " +
            "       o.status, o.total_amount, o.order_time " +
            "FROM orders o " +
            "JOIN customers c ON o.customer_id = c.customer_id " +
            "JOIN vendors   v ON o.vendor_id   = v.vendor_id " +
            "JOIN drivers   d ON o.driver_id   = d.driver_id " +
            "ORDER BY o.order_time DESC " +
            "LIMIT 50";
        return DBConnection.getConnection().createStatement().executeQuery(sql);
    }

    /** Retrieves the average order value and total spent for each customer, ordered by total spent. */
    public ResultSet getAvgOrderValueByCustomer() throws SQLException {
        String sql =
            "SELECT c.first_name, c.last_name, COUNT(o.order_id) AS order_count, " +
            "       AVG(o.total_amount) AS avg_order_value, " +
            "       SUM(o.total_amount) AS total_spent " +
            "FROM orders o " +
            "JOIN customers c ON o.customer_id = c.customer_id " +
            "GROUP BY c.customer_id, c.first_name, c.last_name " +
            "ORDER BY total_spent DESC";
        return DBConnection.getConnection().createStatement().executeQuery(sql);
    }

    /** Maps a ResultSet row queried from the db to an Order object. */
    private Order map(ResultSet rs) throws SQLException {
        return new Order(
            rs.getInt("order_id"),
            rs.getInt("customer_id"),
            rs.getInt("vendor_id"),
            rs.getInt("driver_id"),
            rs.getString("status"),
            rs.getTimestamp("order_time"),
            rs.getDouble("total_amount")
        );
    }
}
