package com.restaurantdeliverysystem.gui;

import javax.swing.*;
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

        tabs.addTab("🛒  Customer",     new CustomerPanel());
        tabs.addTab("🍽  Restaurant",   new VendorPanel());
        tabs.addTab("🚗  Driver",       new DriverPanel());
        tabs.addTab("⚙  Admin",         new AdminPanel());

        add(tabs, BorderLayout.CENTER);

        // Status bar
        JLabel status = new JLabel(" Connected to: food_delivery database");
        status.setBorder(BorderFactory.createEtchedBorder());
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));
        add(status, BorderLayout.SOUTH);
    }
}
