package com.restaurantdeliverysystem.gui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Multi-Vendor Food Delivery Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        CustomerPanel customerPanel = new CustomerPanel();
        tabs.addTab("🛒  Customer",     customerPanel);
        VendorPanel vendorPanel = new VendorPanel();
        tabs.addTab("🍽  Restaurant",   vendorPanel);
        DriverPanel driverPanel = new DriverPanel();
        tabs.addTab("🚗  Driver",       driverPanel);
        AdminPanel adminPanel = new AdminPanel();
        tabs.addTab("⚙  Admin",         adminPanel);

        tabs.addChangeListener(e -> {
            switch (tabs.getSelectedIndex()) {
                case 0 -> customerPanel.refresh();
                case 1 -> vendorPanel.refresh();
                case 2 -> driverPanel.refresh();
                case 3 -> adminPanel.refresh();
            }
        });

        add(tabs, BorderLayout.CENTER);

        // Status bar
        JLabel status = new JLabel(" Connected to: food_delivery database");
        status.setBorder(BorderFactory.createEtchedBorder());
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));
        add(status, BorderLayout.SOUTH);
    }
}
