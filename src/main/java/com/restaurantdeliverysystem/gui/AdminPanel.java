package com.restaurantdeliverysystem.gui;

import com.restaurantdeliverysystem.dao.*;
import com.restaurantdeliverysystem.model.Customer;
import com.restaurantdeliverysystem.model.Driver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPanel extends JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final DriverDAO   driverDAO   = new DriverDAO();
    private final OrderDAO    orderDAO    = new OrderDAO();

    public AdminPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Orders Overview",     buildOrdersTab());
        tabs.addTab("Revenue by Vendor",   buildRevenueTab());
        tabs.addTab("Customer Stats",      buildCustomerStatsTab());
        tabs.addTab("Manage Customers",    buildManageCustomersTab());
        tabs.addTab("Manage Drivers",      buildManageDriversTab());
        add(tabs, BorderLayout.CENTER);
    }

    // -------- Orders Overview --------
    private JPanel buildOrdersTab() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Order ID","Customer","Vendor","Driver","Status","Total","Time"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnLoad   = new JButton("Load All Orders");
        JButton btnStatus = new JButton("Toggle Status");
        JButton btnDelete = new JButton("Delete Order");

        btnLoad.addActionListener(e -> {
            model.setRowCount(0);
            try {
                ResultSet rs = orderDAO.getRecentOrdersWithDetails();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("vendor_name"),
                        rs.getString("driver_first") + " " + rs.getString("driver_last"),
                        rs.getString("status"),
                        String.format("$%.2f", rs.getDouble("total_amount")),
                        rs.getTimestamp("order_time")
                    });
                }
            } catch (SQLException ex) { showError(ex); }
        });

        btnStatus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(null,"Select an order."); return; }
            int orderId = (int) model.getValueAt(row, 0);
            String cur  = (String) model.getValueAt(row, 4);
            String next = "in progress".equals(cur) ? "completed" : "in progress";
            try {
                orderDAO.updateStatus(orderId, next);
                model.setValueAt(next, row, 4);
            } catch (SQLException ex) { showError(ex); }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int orderId = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(null, "Delete order #" + orderId + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                new OrderItemDAO().deleteByOrder(orderId);
                orderDAO.delete(orderId);
                model.removeRow(row);
            } catch (SQLException ex) { showError(ex); }
        });

        // Status count sub-panel
        JTextArea statusArea = new JTextArea(3, 30);
        statusArea.setEditable(false);
        JButton btnStatusCount = new JButton("Show Status Summary");
        btnStatusCount.addActionListener(e -> {
            try {
                ResultSet rs = orderDAO.getOrderCountByStatus();
                StringBuilder sb = new StringBuilder("Order Status Summary:\n");
                while (rs.next()) sb.append("  ").append(rs.getString(1)).append(": ").append(rs.getInt(2)).append("\n");
                statusArea.setText(sb.toString());
            } catch (SQLException ex) { showError(ex); }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnLoad); buttons.add(btnStatus); buttons.add(btnDelete);
        buttons.add(btnStatusCount);

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(new JScrollPane(statusArea), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    // -------- Revenue by Vendor --------
    private JPanel buildRevenueTab() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Vendor","Total Orders","Total Revenue"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        JTable table = new JTable(model);
        JButton btnLoad = new JButton("Load Revenue Report");
        btnLoad.addActionListener(e -> {
            model.setRowCount(0);
            try {
                ResultSet rs = orderDAO.getOrderSummaryByVendor();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("vendor_name"),
                        rs.getInt("total_orders"),
                        String.format("$%.2f", rs.getDouble("total_revenue"))
                    });
                }
            } catch (SQLException ex) { showError(ex); }
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(btnLoad, BorderLayout.SOUTH);
        return p;
    }

    // -------- Customer Stats --------
    private JPanel buildCustomerStatsTab() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"First","Last","# Orders","Avg Order Value","Total Spent"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        JTable table = new JTable(model);
        JButton btnLoad = new JButton("Load Customer Stats");
        btnLoad.addActionListener(e -> {
            model.setRowCount(0);
            try {
                ResultSet rs = orderDAO.getAvgOrderValueByCustomer();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("first_name"), rs.getString("last_name"),
                        rs.getInt("order_count"),
                        String.format("$%.2f", rs.getDouble("avg_order_value")),
                        String.format("$%.2f", rs.getDouble("total_spent"))
                    });
                }
            } catch (SQLException ex) { showError(ex); }
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(btnLoad, BorderLayout.SOUTH);
        return p;
    }

    // -------- Manage Customers --------
    private JPanel buildManageCustomersTab() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","First","Last","Address","Phone","Payment"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable load = () -> {
            model.setRowCount(0);
            try {
                for (Customer c : customerDAO.getAllCustomers()) {
                    model.addRow(new Object[]{
                        c.getCustomerId(), c.getFirstName(), c.getLastName(),
                        c.getAddress(), c.getPhoneNumber(), c.getPaymentMethod()
                    });
                }
            } catch (SQLException ex) { showError(ex); }
        };

        JButton btnLoad = new JButton("Load");
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDel  = new JButton("Delete Selected");

        btnLoad.addActionListener(e -> load.run());
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            JTextField tfFirst = new JTextField((String) model.getValueAt(row, 1), 12);
            JTextField tfLast  = new JTextField((String) model.getValueAt(row, 2), 12);
            JTextField tfAddr  = new JTextField((String) model.getValueAt(row, 3), 20);
            JTextField tfPhone = new JTextField((String) model.getValueAt(row, 4), 12);
            JTextField tfPay   = new JTextField((String) model.getValueAt(row, 5), 12);
            JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
            form.add(new JLabel("First:")); form.add(tfFirst);
            form.add(new JLabel("Last:"));  form.add(tfLast);
            form.add(new JLabel("Addr:"));  form.add(tfAddr);
            form.add(new JLabel("Phone:")); form.add(tfPhone);
            form.add(new JLabel("Pay:"));   form.add(tfPay);
            if (JOptionPane.showConfirmDialog(null, form, "Edit Customer",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
            try {
                customerDAO.update(new Customer(id, tfFirst.getText().trim(), tfLast.getText().trim(),
                                                tfAddr.getText().trim(), tfPhone.getText().trim(), tfPay.getText().trim()));
                load.run();
            } catch (SQLException ex) { showError(ex); }
        });
        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(null, "Delete customer #" + id + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try { customerDAO.delete(id); load.run(); }
            catch (SQLException ex) { showError(ex); }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnLoad); buttons.add(btnEdit); buttons.add(btnDel);

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    // -------- Manage Drivers --------
    private JPanel buildManageDriversTab() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","First","Last","Phone","Status"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable load = () -> {
            model.setRowCount(0);
            try {
                for (Driver d : driverDAO.getAllDrivers()) {
                    model.addRow(new Object[]{
                        d.getDriverId(), d.getFirstName(), d.getLastName(),
                        d.getPhoneNumber(), d.getStatus()
                    });
                }
            } catch (SQLException ex) { showError(ex); }
        };

        JButton btnLoad   = new JButton("Load");
        JButton btnToggle = new JButton("Toggle Status");
        JButton btnDel    = new JButton("Delete");

        btnLoad.addActionListener(e -> load.run());
        btnToggle.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            String cur  = (String) model.getValueAt(row, 4);
            String next = "available".equals(cur) ? "on_delivery" : "available";
            try { driverDAO.updateStatus(id, next); load.run(); }
            catch (SQLException ex) { showError(ex); }
        });
        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(null, "Delete driver #" + id + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try { driverDAO.delete(id); load.run(); }
            catch (SQLException ex) { showError(ex); }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnLoad); buttons.add(btnToggle); buttons.add(btnDel);

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
