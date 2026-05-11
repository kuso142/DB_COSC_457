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
        String sql = "INSERT INTO orders (customer_id, vendor_id, driver_id, restaurant_status, delivery_status, total_amount) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getCustomerId());
            ps.setInt(2, o.getVendorId());
            ps.setInt(3, o.getDriverId());
            ps.setString(4, o.getRestaurantStatus());
            ps.setString(5, o.getDeliveryStatus());
            ps.setDouble(6, o.getTotalAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
    public void updateRestaurantStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET restaurant_status=? WHERE order_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public void updateDeliveryStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET delivery_status=? WHERE order_id=?";
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
    // Each method closes its own Statement and ResultSet and returns a List<Object[]>.

    /** Returns [vendor_name, total_orders, total_revenue] rows ordered by revenue. */
    public List<Object[]> getOrderSummaryByVendor() throws SQLException {
        String sql =
            "SELECT v.name AS vendor_name, COUNT(o.order_id) AS total_orders, " +
            "       SUM(o.total_amount) AS total_revenue " +
            "FROM orders o " +
            "JOIN vendors v ON o.vendor_id = v.vendor_id " +
            "GROUP BY v.vendor_id, v.name " +
            "ORDER BY total_revenue DESC";
        List<Object[]> rows = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                rows.add(new Object[]{ rs.getString("vendor_name"), rs.getInt("total_orders"), rs.getDouble("total_revenue") });
        }
        return rows;
    }

    /** Returns [status, count] rows grouped by delivery status. */
    public List<Object[]> getOrderCountByStatus() throws SQLException {
        String sql =
            "SELECT delivery_status AS status, COUNT(*) AS count FROM orders GROUP BY delivery_status";
        List<Object[]> rows = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                rows.add(new Object[]{ rs.getString("status"), rs.getInt("count") });
        }
        return rows;
    }

    /** Returns [order_id, first_name, last_name, vendor_name, driver_first, driver_last,
     *           restaurant_status, delivery_status, total_amount, order_time] rows (latest 50). */
    public List<Object[]> getRecentOrdersWithDetails() throws SQLException {
        String sql =
            "SELECT o.order_id, c.first_name, c.last_name, v.name AS vendor_name, " +
            "       d.first_name AS driver_first, d.last_name AS driver_last, " +
            "       o.restaurant_status, o.delivery_status, o.total_amount, o.order_time " +
            "FROM orders o " +
            "JOIN customers c ON o.customer_id = c.customer_id " +
            "JOIN vendors   v ON o.vendor_id   = v.vendor_id " +
            "JOIN drivers   d ON o.driver_id   = d.driver_id " +
            "ORDER BY o.order_time DESC " +
            "LIMIT 50";
        List<Object[]> rows = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                rows.add(new Object[]{
                    rs.getInt("order_id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("vendor_name"), rs.getString("driver_first"), rs.getString("driver_last"),
                    rs.getString("restaurant_status"), rs.getString("delivery_status"),
                    rs.getDouble("total_amount"), rs.getTimestamp("order_time")
                });
        }
        return rows;
    }

    /** Returns [first_name, last_name, order_count, avg_order_value, total_spent] rows ordered by total spent. */
    public List<Object[]> getAvgOrderValueByCustomer() throws SQLException {
        String sql =
            "SELECT c.first_name, c.last_name, COUNT(o.order_id) AS order_count, " +
            "       AVG(o.total_amount) AS avg_order_value, " +
            "       SUM(o.total_amount) AS total_spent " +
            "FROM orders o " +
            "JOIN customers c ON o.customer_id = c.customer_id " +
            "GROUP BY c.customer_id, c.first_name, c.last_name " +
            "ORDER BY total_spent DESC";
        List<Object[]> rows = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                rows.add(new Object[]{
                    rs.getString("first_name"), rs.getString("last_name"),
                    rs.getInt("order_count"), rs.getDouble("avg_order_value"), rs.getDouble("total_spent")
                });
        }
        return rows;
    }

    /** Maps a ResultSet row queried from the db to an Order object. */
    private Order map(ResultSet rs) throws SQLException {
        return new Order(
            rs.getInt("order_id"),
            rs.getInt("customer_id"),
            rs.getInt("vendor_id"),
            rs.getInt("driver_id"),
            rs.getString("restaurant_status"),
            rs.getString("delivery_status"),
            rs.getTimestamp("order_time"),
            rs.getDouble("total_amount")
        );
    }
}
