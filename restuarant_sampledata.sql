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
('Kailee', 'Smith', '222-222-2222', 'on_delivery'),
('Tyler', 'Hayes', '333-333-3333', 'on_delivery'),
('Jordan', 'Lee', '444-444-4444', 'available');

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

-- Orders  (restaurant_status: 'preparing' | 'ready',  delivery_status: 'pending' | 'delivered')
INSERT INTO orders
(customer_id, vendor_id, driver_id, restaurant_status, delivery_status, total_amount)
VALUES
(1, 1, 1, 'preparing',  'pending',   12.98),  -- just placed, kitchen working on it
(2, 2, 2, 'ready',      'pending',   10.98),  -- ready for driver pickup
(3, 3, 3, 'ready',      'pending',   6.49),   -- ready, awaiting driver delivery
(4, 4, 2, 'preparing',  'pending',   12.98),  -- another active order
(5, 5, 1, 'ready',      'delivered', 14.98);  -- another completed order

-- Order Items
INSERT INTO order_items
(order_id, item_id, quantity, unit_price)
VALUES
(1, 1, 1, 7.99),   -- order 1: Whopper
(1, 2, 1, 4.99),   -- order 1: Chicken Fries
(2, 3, 1, 6.99),   -- order 2: Big Mac
(2, 4, 1, 3.99),   -- order 2: French Fries
(3, 5, 1, 6.49),   -- order 3: Chicken Sandwich
(4, 7, 1, 10.99),  -- order 4: 3 Chicken Finger Combo
(4, 8, 1, 1.99),   -- order 4: Texas Toast
(5, 9, 1, 11.99),  -- order 5: Chicken Bowl
(5, 10, 1, 2.99);  -- order 5: Pita Chips

#check to see if it worked
SELECT * FROM customers;
SELECT * FROM vendors;
SELECT * FROM drivers;
SELECT * FROM menu_items;
SELECT * FROM orders;
SELECT * FROM order_items;