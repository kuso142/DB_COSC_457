package com.restaurantdeliverysystem.gui;

import com.restaurantdeliverysystem.dao.DriverDAO;
import com.restaurantdeliverysystem.model.Driver;
import com.restaurantdeliverysystem.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DriverPanel extends JPanel {

    private final DriverDAO driverDAO = new DriverDAO();

    private JComboBox<Driver> cbDriver;
    private DefaultTableModel deliveriesModel;
    private JTable deliveriesTable;
    private DefaultTableModel allDriversModel;
    private JTable allDriversTable;
    private boolean initialized = false;

    public DriverPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildAllDrivers(), BorderLayout.SOUTH);
        initialized = true;
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.setBorder(BorderFactory.createTitledBorder("Select Driver"));
        cbDriver = new JComboBox<>();
        refreshDriverCombo();
        cbDriver.addActionListener(e -> loadDeliveries());

        JButton btnAdd = new JButton("+ Add Driver");
        JButton btnDel = new JButton("Remove Driver");
        btnAdd.addActionListener(e -> addDriver());
        btnDel.addActionListener(e -> removeDriver());

        p.add(new JLabel("Driver:")); p.add(cbDriver);
        p.add(btnAdd); p.add(btnDel);
        return p;
    }

    private JPanel buildCenter() {
        deliveriesModel = new DefaultTableModel(
            new String[]{"Order ID","Customer","Delivery Address","Vendor","Rest. Status","Del. Status","Total","Order Time"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        deliveriesTable = new JTable(deliveriesModel);
        deliveriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnRefresh    = new JButton("Refresh");
        JButton btnMarkDone   = new JButton("Mark Delivery Complete");
        btnRefresh.addActionListener(e -> { refreshDriverCombo(); loadDeliveries(); });
        btnMarkDone.addActionListener(e -> markDeliveryComplete());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnRefresh); buttons.add(btnMarkDone);

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("My Assigned Deliveries"));
        p.add(new JScrollPane(deliveriesTable), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildAllDrivers() {
        allDriversModel = new DefaultTableModel(
            new String[]{"ID","First","Last","Phone","Status"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        allDriversTable = new JTable(allDriversModel);
        allDriversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnRefresh = new JButton("Refresh All Drivers");
        btnRefresh.addActionListener(e -> loadAllDrivers());

        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(0, 170));
        p.setBorder(BorderFactory.createTitledBorder("All Drivers"));
        p.add(new JScrollPane(allDriversTable), BorderLayout.CENTER);
        p.add(btnRefresh, BorderLayout.SOUTH);
        loadAllDrivers();
        return p;
    }

    public void refresh() {
        refreshDriverCombo();
        loadDeliveries();
        loadAllDrivers();
    }

    // ----- data loaders -----

    private void refreshDriverCombo() {
        Driver prev = (Driver) cbDriver.getSelectedItem();
        int prevId = prev != null ? prev.getDriverId() : -1;
        cbDriver.removeAllItems();
        try {
            Driver toSelect = null;
            for (Driver d : driverDAO.getAllDrivers()) {
                cbDriver.addItem(d);
                if (d.getDriverId() == prevId) toSelect = d;
            }
            if (toSelect != null) cbDriver.setSelectedItem(toSelect);
        } catch (SQLException e) { showError(e); }
    }

    private void loadDeliveries() {
        deliveriesModel.setRowCount(0);
        Driver d = (Driver) cbDriver.getSelectedItem();
        if (d == null) return;
        try {
            String sql =
                "SELECT o.order_id, CONCAT(c.first_name,' ',c.last_name), c.address, v.name, " +
                "       o.restaurant_status, o.delivery_status, o.total_amount, o.order_time " +
                "FROM orders o " +
                "JOIN customers c ON o.customer_id = c.customer_id " +
                "JOIN vendors   v ON o.vendor_id   = v.vendor_id " +
                "WHERE o.driver_id = ? ORDER BY o.order_time DESC";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, d.getDriverId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                deliveriesModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    String.format("$%.2f", rs.getDouble(7)),
                    rs.getTimestamp(8)
                });
            }
        } catch (SQLException e) { showError(e); }
    }

    private void loadAllDrivers() {
        allDriversModel.setRowCount(0);
        try {
            for (Driver d : driverDAO.getAllDrivers()) {
                allDriversModel.addRow(new Object[]{
                    d.getDriverId(), d.getFirstName(), d.getLastName(),
                    d.getPhoneNumber(), d.getStatus()
                });
            }
        } catch (SQLException e) { showError(e); }
    }

    // ----- actions -----

    private void addDriver() {
        JTextField tfFirst = new JTextField(12);
        JTextField tfLast  = new JTextField(12);
        JTextField tfPhone = new JTextField(12);

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("First Name:")); form.add(tfFirst);
        form.add(new JLabel("Last Name:"));  form.add(tfLast);
        form.add(new JLabel("Phone:"));      form.add(tfPhone);

        if (JOptionPane.showConfirmDialog(this, form, "Add Driver",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (tfFirst.getText().isBlank() || tfLast.getText().isBlank() || tfPhone.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields required."); return;
        }
        try {
            driverDAO.insert(new Driver(0, tfFirst.getText().trim(), tfLast.getText().trim(),
                                        tfPhone.getText().trim(), "available"));
            refreshDriverCombo();
            loadAllDrivers();
        } catch (SQLException e) { showError(e); }
    }

    private void removeDriver() {
        Driver d = (Driver) cbDriver.getSelectedItem();
        if (d == null) return;
        int c = JOptionPane.showConfirmDialog(this, "Remove driver " + d + "?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            driverDAO.delete(d.getDriverId());
            refreshDriverCombo();
            loadAllDrivers();
        } catch (SQLException e) { showError(e); }
    }

    private void markDeliveryComplete() {
        int row = deliveriesTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an order."); return; }
        int orderId = (int) deliveriesModel.getValueAt(row, 0);
        Driver d = (Driver) cbDriver.getSelectedItem();
        if (d == null) return;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE orders SET delivery_status='delivered' WHERE order_id=?");
            ps.setInt(1, orderId);
            ps.executeUpdate();
            driverDAO.updateStatus(d.getDriverId(), "available");
            loadDeliveries();
            loadAllDrivers();
            refreshDriverCombo();
            JOptionPane.showMessageDialog(this, "Delivery #" + orderId + " marked delivered. " + d.getFirstName() + " " + d.getLastName() + " is now available.");
        } catch (SQLException e) { showError(e); }
    }

    private void showError(Exception e) {
        e.printStackTrace();
        if (initialized) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
