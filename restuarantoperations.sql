USE food_delivery;

-- SELECT, displays all menu items from menu_items table
SELECT * FROM menu_items;

-- INSERT
INSERT INTO vendors(name, address, phone_number)
VALUES ('Subway', '6 Market Street', '555-123-4567');


-- UPDATE, update phone number of customer 1
UPDATE customers
SET phone_number = '999-999-9999'
WHERE customer_id = 1;

-- DELETE, delete customer 6 from customer table
DELETE FROM customers
WHERE customer_id = 6;

-- JOIN, show each order with customer first and last name and driver assigned
SELECT o.order_id, c.first_name, d.first_name AS driver_name, o.status
FROM orders o
JOIN customers c ON o.customer_id = c.customer_id
JOIN drivers d ON o.driver_id = d.driver_id;

-- COUNT, count total number of drivers
SELECT COUNT(*) AS total_drivers
FROM drivers;

-- SUM, calculate totla amount of all orders together
SELECT SUM(total_amount) AS total_order_amount
FROM orders;

-- GROUP BY, groups the menu items by vendor and counts how many items the vendor has
SELECT vendor_id, COUNT(*) AS total_menu_items
FROM menu_items
GROUP BY vendor_id;

-- ORDER BY, shows customers ordered by last name alphabetically
SELECT * FROM customers
ORDER BY last_name ASC;

-- WHERE, shows all orders in progress
SELECT * FROM orders
WHERE status = 'in progress';