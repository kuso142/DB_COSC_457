package com.restaurantdeliverysystem.gui;

import com.restaurantdeliverysystem.dao.MenuItemDAO;
import com.restaurantdeliverysystem.dao.VendorDAO;
import com.restaurantdeliverysystem.model.MenuItem;
import com.restaurantdeliverysystem.model.Vendor;
import com.restaurantdeliverysystem.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VendorPanel extends JPanel {

    private final VendorDAO vendorDAO = new VendorDAO();
    private final MenuItemDAO menuItemDAO = new MenuItemDAO();

    private JComboBox<Vendor> cbVendor;
    private DefaultTableModel menuModel;
    private JTable menuTable;
    private DefaultTableModel ordersModel;
    private JTable ordersTable;
    private boolean initialized = false;

    public VendorPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSplitCenter(), BorderLayout.CENTER);
        initialized = true;
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.setBorder(BorderFactory.createTitledBorder("Select Restaurant"));
        cbVendor = new JComboBox<>();
        refreshVendorCombo();
        cbVendor.addActionListener(e -> {
            loadMenu();
            loadOrders();
        });

        JButton btnAddVendor = new JButton("+ New Restaurant");
        btnAddVendor.addActionListener(e -> addVendor());
        JButton btnDelVendor = new JButton("Delete Restaurant");
        btnDelVendor.addActionListener(e -> deleteVendor());

        p.add(new JLabel("Restaurant:"));
        p.add(cbVendor);
        p.add(btnAddVendor);
        p.add(btnDelVendor);
        return p;
    }

    private JSplitPane buildSplitCenter() {
        // ---- Menu management ----
        menuModel = new DefaultTableModel(new String[] { "ID", "Item", "Price", "Description" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        menuTable = new JTable(menuModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.getColumnModel().getColumn(0).setMaxWidth(40);
        menuTable.getColumnModel().getColumn(2).setMaxWidth(70);

        JButton btnAdd = new JButton("+ Add Item");
        JButton btnEdit = new JButton("Edit Item");
        JButton btnDelete = new JButton("Delete Item");
        btnAdd.addActionListener(e -> addMenuItem());
        btnEdit.addActionListener(e -> editMenuItem());
        btnDelete.addActionListener(e -> deleteMenuItem());

        JPanel menuButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuButtons.add(btnAdd);
        menuButtons.add(btnEdit);
        menuButtons.add(btnDelete);

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu Items"));
        menuPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        menuPanel.add(menuButtons, BorderLayout.SOUTH);

        // ---- Incoming orders ----
        ordersModel = new DefaultTableModel(
                new String[] { "Order ID", "Customer", "Status", "Total", "Time" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        ordersTable = new JTable(ordersModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnRefresh = new JButton("Refresh");
        JButton btnMarkDone = new JButton("Mark Completed");
        btnRefresh.addActionListener(e -> loadOrders());
        btnMarkDone.addActionListener(e -> markOrderCompleted());

        JPanel ordersButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ordersButtons.add(btnRefresh);
        ordersButtons.add(btnMarkDone);

        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBorder(BorderFactory.createTitledBorder("Incoming Orders"));
        ordersPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        ordersPanel.add(ordersButtons, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, ordersPanel);
        split.setDividerLocation(480);
        return split;
    }

    // ----- data loaders -----

    private void refreshVendorCombo() {
        cbVendor.removeAllItems();
        try {
            for (Vendor v : vendorDAO.getAllVendors())
                cbVendor.addItem(v);
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void loadMenu() {
        menuModel.setRowCount(0);
        Vendor v = (Vendor) cbVendor.getSelectedItem();
        if (v == null)
            return;
        try {
            for (MenuItem m : menuItemDAO.getByVendor(v.getVendorId())) {
                menuModel.addRow(new Object[] {
                        m.getItemId(), m.getItemName(),
                        String.format("$%.2f", m.getPrice()), m.getDescription()
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void loadOrders() {
        ordersModel.setRowCount(0);
        Vendor v = (Vendor) cbVendor.getSelectedItem();
        if (v == null)
            return;
        try {
            String sql = "SELECT o.order_id, CONCAT(c.first_name,' ',c.last_name), " +
                    "       o.status, o.total_amount, o.order_time " +
                    "FROM orders o JOIN customers c ON o.customer_id = c.customer_id " +
                    "WHERE o.vendor_id = ? ORDER BY o.order_time DESC";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, v.getVendorId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ordersModel.addRow(new Object[] {
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        String.format("$%.2f", rs.getDouble(4)), rs.getTimestamp(5)
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    // ----- actions -----

    private void addVendor() {
        JTextField tfName = new JTextField(15);
        JTextField tfAddr = new JTextField(20);
        JTextField tfPhone = new JTextField(12);

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Name:"));
        form.add(tfName);
        form.add(new JLabel("Address:"));
        form.add(tfAddr);
        form.add(new JLabel("Phone:"));
        form.add(tfPhone);

        if (JOptionPane.showConfirmDialog(this, form, "New Restaurant",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;
        if (tfName.getText().isBlank() || tfAddr.getText().isBlank() || tfPhone.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Name, address and phone are required.");
            return;
        }
        try {
            vendorDAO.insert(new Vendor(0, tfName.getText().trim(), tfAddr.getText().trim(),
                    tfPhone.getText().trim()));
            refreshVendorCombo();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void deleteVendor() {
        Vendor v = (Vendor) cbVendor.getSelectedItem();
        if (v == null)
            return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete restaurant '" + v.getName() + "'?\nThis will also delete its menu items.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        try {
            vendorDAO.delete(v.getVendorId());
            refreshVendorCombo();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void addMenuItem() {
        Vendor v = (Vendor) cbVendor.getSelectedItem();
        if (v == null) {
            JOptionPane.showMessageDialog(this, "Select a restaurant first.");
            return;
        }

        JTextField tfName = new JTextField(15);
        JTextField tfPrice = new JTextField(8);
        JTextField tfDesc = new JTextField(25);

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Item Name:"));
        form.add(tfName);
        form.add(new JLabel("Price ($):"));
        form.add(tfPrice);
        form.add(new JLabel("Description:"));
        form.add(tfDesc);

        if (JOptionPane.showConfirmDialog(this, form, "Add Menu Item",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;
        try {
            double price = Double.parseDouble(tfPrice.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be > 0.");
                return;
            }
            menuItemDAO
                    .insert(new MenuItem(0, v.getVendorId(), tfName.getText().trim(), price, tfDesc.getText().trim()));
            loadMenu();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price.");
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void editMenuItem() {
        int row = menuTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a menu item.");
            return;
        }
        int itemId = (int) menuModel.getValueAt(row, 0);
        String name = (String) menuModel.getValueAt(row, 1);
        String price = ((String) menuModel.getValueAt(row, 2)).replace("$", "");
        String desc = (String) menuModel.getValueAt(row, 3);

        JTextField tfName = new JTextField(name, 15);
        JTextField tfPrice = new JTextField(price, 8);
        JTextField tfDesc = new JTextField(desc, 25);

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Item Name:"));
        form.add(tfName);
        form.add(new JLabel("Price ($):"));
        form.add(tfPrice);
        form.add(new JLabel("Description:"));
        form.add(tfDesc);

        if (JOptionPane.showConfirmDialog(this, form, "Edit Menu Item",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;
        try {
            double p = Double.parseDouble(tfPrice.getText().trim());
            if (p <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be > 0.");
                return;
            }
            Vendor v = (Vendor) cbVendor.getSelectedItem();
            menuItemDAO
                    .update(new MenuItem(itemId, v.getVendorId(), tfName.getText().trim(), p, tfDesc.getText().trim()));
            loadMenu();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price.");
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void deleteMenuItem() {
        int row = menuTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a menu item.");
            return;
        }
        int itemId = (int) menuModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this menu item?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        try {
            menuItemDAO.delete(itemId);
            loadMenu();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void markOrderCompleted() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order.");
            return;
        }
        int orderId = (int) ordersModel.getValueAt(row, 0);
        try {
            // also set driver back to available
            String sql = "SELECT driver_id FROM orders WHERE order_id = ?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int driverId = rs.getInt(1);
                PreparedStatement upd = DBConnection.getConnection().prepareStatement(
                        "UPDATE drivers SET status='available' WHERE driver_id=?");
                upd.setInt(1, driverId);
                upd.executeUpdate();
            }
            PreparedStatement upd2 = DBConnection.getConnection().prepareStatement(
                    "UPDATE orders SET status='completed' WHERE order_id=?");
            upd2.setInt(1, orderId);
            upd2.executeUpdate();
            loadOrders();
            JOptionPane.showMessageDialog(this, "Order #" + orderId + " marked as completed.");
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        e.printStackTrace();
        if (initialized) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
