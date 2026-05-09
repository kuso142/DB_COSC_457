USE food_delivery;

-- Customers
INSERT INTO customers
(first_name, last_name, address, phone_number, payment_method)
VALUES
('Abigail', 'Buckland', '123 Melody Overpass', '111-222-3333', 'Credit Card'),
('Lucas', 'Crowe', '45 South Bethany Road', '444-555-6666', 'Cash'),
('Chris', 'C', '333 New Kellen Drive', '777-888-9999', 'Debit Card'),
('Zaynab', 'Tabassi', '222 South Lawson Road', '123-456-7891', 'Cash'),
('Nghi', 'Tran', '23 Main Drive', '987-654-3219', 'Debit Card');

-- Vendors
INSERT INTO vendors
(name, address, phone_number)
VALUES
('Burger King', '1 York Road', '678-543-1234'),
('Mcdonalds', '2 Washington Street', '999-988-8888'),
('Chick Fil A', '3 Central Boulevard', '777-774-4444'),
('Canes', '4 Food Drive', '111-111-2222'),
('Cava', '5 Healthy Street', '333-333-3777');

-- Drivers
INSERT INTO drivers
(first_name, last_name, phone_number, status)
VALUES
('Grace', 'Miller', '111-111-1111', 'available'),
('Kailee', 'Smith', '222-222-2222', 'available'),
('Tyler', 'Hayes', '333-333-3333', 'busy');

-- Menu Items
INSERT INTO menu_items
(vendor_id, item_name, price, description) #this line is used because we are using auto increment
VALUES
(1, 'Whopper', 7.99, 'Burger King signature burger'),
(1, 'Chicken Fries', 4.99, 'Crispy chicken fries'),
(2, 'Big Mac', 6.99, 'McDonalds classic burger'),
(2, 'French Fries', 3.99, 'Crispy fries'),
(3, 'Chicken Sandwich', 6.49, 'Chick Fil A chicken sandwich'),
(3, 'Waffle Fries', 3.49, 'Chick Fil A waffle fries'),
(4, '3 Chicken Finger Combo', 10.99, 'Canes chicken finger meal'),
(4, 'Texas Toast', 1.99, 'Buttery toasted bread'),
(5, 'Chicken Bowl', 11.99, 'Cava chicken grain bowl'),
(5, 'Pita Chips', 2.99, 'Crunchy pita chips');

-- Orders
INSERT INTO orders
(customer_id, vendor_id, driver_id, status, total_amount)
VALUES
(1, 1, 1, 'in progress', 12.98),
(2, 2, 2, 'completed', 10.98);

-- Order Items
INSERT INTO order_items
(order_id, item_id, quantity, unit_price)
VALUES
(1, 1, 1, 7.99),
(1, 2, 1, 4.99),
(2, 3, 1, 6.99),
(2, 4, 1, 3.99);

#check to see if it worked
SELECT * FROM customers;
SELECT * FROM vendors;
SELECT * FROM drivers;
SELECT * FROM menu_items;
SELECT * FROM orders;
SELECT * FROM order_items;