-- ============================================================
-- food_delivery.sql
-- Database schema for the Multi-Vendor Food Delivery Platform
-- COSC 457 — Database Management Systems
-- ============================================================

CREATE DATABASE food_delivery;
USE food_delivery;

-- Stores customers who place orders through the platform.
-- payment_method tracks the customer's preferred payment type (e.g. Credit Card, PayPal).
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY, ##auto increment creates the ids automatically so each has a unique ID (guarenteed)
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    address VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    payment_method VARCHAR(50) NOT NULL
);

-- Stores restaurants (vendors) registered on the platform.
CREATE TABLE vendors (
    vendor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL
);

-- Stores delivery drivers available to fulfill orders.
-- status tracks availability: 'available' or 'on_delivery'.
CREATE TABLE drivers (
    driver_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL
);

-- Stores menu items offered by each vendor.
-- Each item belongs to exactly one vendor via vendor_id.
CREATE TABLE menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
);

-- Stores customer orders placed through the platform.
-- restaurant_status tracks kitchen progress: 'preparing' or 'ready'.
-- delivery_status tracks delivery progress: 'pending' or 'delivered'.
-- order_time is set automatically to the current timestamp when the order is created.
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    vendor_id INT NOT NULL,
    driver_id INT NOT NULL,
    restaurant_status VARCHAR(30) NOT NULL DEFAULT 'preparing',
    delivery_status VARCHAR(30) NOT NULL DEFAULT 'pending',
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id),
    FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
);

-- Stores the individual items within each order.
-- unit_price is recorded at the time of purchase in case menu prices change later.
-- An order can contain multiple items, each tracked as a separate row.
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id)
);
