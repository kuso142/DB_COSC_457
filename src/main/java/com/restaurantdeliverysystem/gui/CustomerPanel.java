package com.restaurantdeliverysystem.gui;

import com.restaurantdeliverysystem.dao.*;
import com.restaurantdeliverysystem.model.Customer;
import com.restaurantdeliverysystem.model.Driver;
import com.restaurantdeliverysystem.model.MenuItem;
import com.restaurantdeliverysystem.model.Order;
import com.restaurantdeliverysystem.model.Vendor;
import com.restaurantdeliverysystem.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerPanel extends JPanel {

    private final CustomerDAO  customerDAO  = new CustomerDAO();
    private final VendorDAO    vendorDAO    = new VendorDAO();
    private final MenuItemDAO  menuItemDAO  = new MenuItemDAO();
    private final OrderDAO     orderDAO     = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final DriverDAO    driverDAO    = new DriverDAO();

    //Widgets
    private JComboBox<Customer>  cbCustomer;
    private JComboBox<Vendor>    cbVendor;
    private DefaultTableModel    menuModel;
    private JTable               menuTable;
    private DefaultTableModel    cartModel;
    private JTable               cartTable;
    private JLabel               lblTotal;
    private DefaultTableModel    ordersModel;
    private JTable               ordersTable;

    //Cart state
    private final List<MenuItem> cartItems = new ArrayList<>();
    private boolean initialized = false;

    public CustomerPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(buildTopBar(),      BorderLayout.NORTH);
        add(buildCenter(),      BorderLayout.CENTER);
        add(buildOrderHistory(),BorderLayout.SOUTH);
        initialized = true;
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.setBorder(BorderFactory.createTitledBorder("Select Customer & Restaurant"));

        cbCustomer = new JComboBox<>();
        cbVendor   = new JComboBox<>();
        refreshCustomerCombo();
        refreshVendorCombo();

        cbVendor.addActionListener(e -> loadMenu());

        JButton btnNewCustomer = new JButton("+ New Customer");
        btnNewCustomer.addActionListener(e -> addCustomer());

        p.add(new JLabel("Customer:"));  p.add(cbCustomer);
        p.add(new JLabel("Restaurant:")); p.add(cbVendor);
        p.add(btnNewCustomer);
        return p;
    }

    private JSplitPane buildCenter() {
        //Menu panel
        menuModel = new DefaultTableModel(new String[]{"ID","Item","Price","Description"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        menuTable = new JTable(menuModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.getColumnModel().getColumn(0).setMaxWidth(40);
        menuTable.getColumnModel().getColumn(2).setMaxWidth(70);

        JButton btnAddToCart = new JButton("Add to Cart ▶");
        btnAddToCart.addActionListener(e -> addToCart());

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu"));
        menuPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        menuPanel.add(btnAddToCart, BorderLayout.SOUTH);

        //Cart panel
        cartModel = new DefaultTableModel(new String[]{"Item","Price"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        cartTable = new JTable(cartModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton btnRemove    = new JButton("Remove Item");
        JButton btnClearCart = new JButton("Clear Cart");
        JButton btnPlaceOrder = new JButton("✅ Place Order");
        btnPlaceOrder.setBackground(new Color(46,139,87));
        btnPlaceOrder.setForeground(Color.WHITE);

        btnRemove.addActionListener(e -> removeFromCart());
        btnClearCart.addActionListener(e -> clearCart());
        btnPlaceOrder.addActionListener(e -> placeOrder());

        JPanel cartButtons = new JPanel(new GridLayout(1,3,4,0));
        cartButtons.add(btnRemove);
        cartButtons.add(btnClearCart);
        cartButtons.add(btnPlaceOrder);

        JPanel cartBottom = new JPanel(new BorderLayout());
        cartBottom.add(lblTotal, BorderLayout.NORTH);
        cartBottom.add(cartButtons, BorderLayout.SOUTH);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("My Cart"));
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartPanel.add(cartBottom, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, cartPanel);
        split.setDividerLocation(500);
        return split;
    }

    private JPanel buildOrderHistory() {
        ordersModel = new DefaultTableModel(
            new String[]{"Order ID","Vendor","Driver","Rest. Status","Del. Status","Total","Time"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        ordersTable = new JTable(ordersModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getColumnModel().getColumn(0).setMaxWidth(70);
        ordersTable.getColumnModel().getColumn(3).setMaxWidth(100);
        ordersTable.getColumnModel().getColumn(4).setMaxWidth(80);

        JButton btnRefresh   = new JButton("Refresh Orders");
        JButton btnViewItems = new JButton("View Items");
        JButton btnCancel    = new JButton("Cancel Order");

        btnRefresh.addActionListener(e -> loadOrderHistory());
        btnViewItems.addActionListener(e -> viewOrderItems());
        btnCancel.addActionListener(e -> cancelOrder());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnRefresh); buttons.add(btnViewItems); buttons.add(btnCancel);

        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(0, 180));
        p.setBorder(BorderFactory.createTitledBorder("My Order History"));
        p.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    public void refresh() {
        refreshCustomerCombo();
        refreshVendorCombo();
    }

    //-------------------------------------------------------  helpers

    private void refreshCustomerCombo() {
        cbCustomer.removeAllItems();
        try {
            for (Customer c : customerDAO.getAllCustomers()) cbCustomer.addItem(c);
        } catch (SQLException e) { showError(e); }
    }

    private void refreshVendorCombo() {
        cbVendor.removeAllItems();
        try {
            for (Vendor v : vendorDAO.getAllVendors()) cbVendor.addItem(v);
        } catch (SQLException e) { showError(e); }
    }

    private void loadMenu() {
        menuModel.setRowCount(0);
        Vendor v = (Vendor) cbVendor.getSelectedItem();
        if (v == null) return;
        try {
            for (MenuItem m : menuItemDAO.getByVendor(v.getVendorId())) {
                menuModel.addRow(new Object[]{
                    m.getItemId(), m.getItemName(),
                    String.format("$%.2f", m.getPrice()), m.getDescription()
                });
            }
        } catch (SQLException e) { showError(e); }
    }

    private void addToCart() {
        int row = menuTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a menu item first."); return; }
        Vendor v = (Vendor) cbVendor.getSelectedItem();
        if (v == null) return;
        try {
            int itemId = (int) menuModel.getValueAt(row, 0);
            for (MenuItem m : menuItemDAO.getByVendor(v.getVendorId())) {
                if (m.getItemId() == itemId) {
                    cartItems.add(m);
                    cartModel.addRow(new Object[]{m.getItemName(), String.format("$%.2f", m.getPrice())});
                    updateTotal();
                    return;
                }
            }
        } catch (SQLException e) { showError(e); }
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row < 0) return;
        cartItems.remove(row);
        cartModel.removeRow(row);
        updateTotal();
    }

    private void clearCart() {
        cartItems.clear();
        cartModel.setRowCount(0);
        updateTotal();
    }

    private void updateTotal() {
        double t = cartItems.stream().mapToDouble(MenuItem::getPrice).sum();
        lblTotal.setText(String.format("Total: $%.2f", t));
    }

    private void placeOrder() {
        Customer customer = (Customer) cbCustomer.getSelectedItem();
        Vendor   vendor   = (Vendor)   cbVendor.getSelectedItem();
        if (customer == null || vendor == null) {
            JOptionPane.showMessageDialog(this, "Select a customer and restaurant."); return;
        }
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one item to the cart."); return;
        }
        try {
            List<Driver> available = driverDAO.getAvailableDrivers();
            if (available.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No drivers available right now."); return;
            }
            Driver driver = available.get(0); //auto-assign first available

            double total = cartItems.stream().mapToDouble(MenuItem::getPrice).sum();
            Order order = new Order(0, customer.getCustomerId(), vendor.getVendorId(),
                                    driver.getDriverId(), "preparing", "pending", null, total);
            int orderId = orderDAO.insert(order);
            for (MenuItem m : cartItems) {
                orderItemDAO.insert(orderId, m.getItemId(), 1, m.getPrice());
            }
            driverDAO.updateStatus(driver.getDriverId(), "on_delivery");

            JOptionPane.showMessageDialog(this,
                "Order #" + orderId + " placed!\nAssigned driver: " +
                driver.getFirstName() + " " + driver.getLastName());
            clearCart();
            loadOrderHistory();
        } catch (SQLException e) { showError(e); }
    }

    private void addCustomer() {
        JTextField tfFirst   = new JTextField(12);
        JTextField tfLast    = new JTextField(12);
        JTextField tfAddr    = new JTextField(20);
        JTextField tfPhone   = new JTextField(12);
        JComboBox<String> cbPay = new JComboBox<>(new String[]{"Credit Card","Debit Card","Cash","PayPal"});

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("First Name:")); form.add(tfFirst);
        form.add(new JLabel("Last Name:"));  form.add(tfLast);
        form.add(new JLabel("Address:"));    form.add(tfAddr);
        form.add(new JLabel("Phone:"));      form.add(tfPhone);
        form.add(new JLabel("Payment:"));    form.add(cbPay);

        int res = JOptionPane.showConfirmDialog(this, form, "New Customer", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;
        if (tfFirst.getText().isBlank() || tfLast.getText().isBlank() ||
            tfAddr.getText().isBlank()  || tfPhone.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required."); return;
        }
        try {
            Customer c = new Customer(0, tfFirst.getText().trim(), tfLast.getText().trim(),
                                      tfAddr.getText().trim(), tfPhone.getText().trim(),
                                      (String) cbPay.getSelectedItem());
            customerDAO.insert(c);
            refreshCustomerCombo();
            JOptionPane.showMessageDialog(this, "Customer added successfully.");
        } catch (SQLException e) { showError(e); }
    }

    private void loadOrderHistory() {
        ordersModel.setRowCount(0);
        Customer c = (Customer) cbCustomer.getSelectedItem();
        if (c == null) return;
        try {
            String sql =
                "SELECT o.order_id, v.name, CONCAT(d.first_name,' ',d.last_name), " +
                "       o.restaurant_status, o.delivery_status, o.total_amount, o.order_time " +
                "FROM orders o " +
                "JOIN vendors v  ON o.vendor_id  = v.vendor_id " +
                "JOIN drivers d  ON o.driver_id  = d.driver_id " +
                "WHERE o.customer_id = ? ORDER BY o.order_time DESC";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, c.getCustomerId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ordersModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5),
                    String.format("$%.2f", rs.getDouble(6)),
                    rs.getTimestamp(7)
                });
            }
        } catch (SQLException e) { showError(e); }
    }

    private void viewOrderItems() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an order."); return; }
        int orderId = (int) ordersModel.getValueAt(row, 0);
        try {
            ResultSet rs = orderItemDAO.getByOrder(orderId);
            StringBuilder sb = new StringBuilder("Items in Order #" + orderId + ":\n\n");
            while (rs.next()) {
                sb.append("  ").append(rs.getString("item_name"))
                  .append("  x").append(rs.getInt("quantity"))
                  .append("  @ $").append(String.format("%.2f", rs.getDouble("unit_price")))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        } catch (SQLException e) { showError(e); }
    }

    private void cancelOrder() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an order."); return; }
        int orderId = (int) ordersModel.getValueAt(row, 0);
        String deliveryStatus = (String) ordersModel.getValueAt(row, 4);
        if ("delivered".equals(deliveryStatus)) {
            JOptionPane.showMessageDialog(this, "Cannot cancel a delivered order."); return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Cancel order #" + orderId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            //Fetch driver_id before deleting so the driver can be freed
            int driverId = -1;
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                    "SELECT driver_id FROM orders WHERE order_id = ?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) driverId = rs.getInt(1);
                }
            }
            orderItemDAO.deleteByOrder(orderId);
            orderDAO.delete(orderId);
            if (driverId != -1) driverDAO.updateStatus(driverId, "available");
            loadOrderHistory();
            JOptionPane.showMessageDialog(this, "Order cancelled.");
        } catch (SQLException e) { showError(e); }
    }

    private void showError(Exception e) {
        e.printStackTrace();
        if (initialized) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
