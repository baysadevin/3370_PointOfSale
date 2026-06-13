-- Delete existing data
DELETE FROM transaction_items;
DELETE FROM transactions;
DELETE FROM inventory_alerts;
DELETE FROM purchase_orders;
DELETE FROM products;
DELETE FROM users;

-- Reset auto increment
DELETE FROM sqlite_sequence WHERE name='users';
DELETE FROM sqlite_sequence WHERE name='products';
DELETE FROM sqlite_sequence WHERE name='transactions';
DELETE FROM sqlite_sequence WHERE name='transaction_items';
DELETE FROM sqlite_sequence WHERE name='inventory_alerts';
DELETE FROM sqlite_sequence WHERE name='purchase_orders';

-- ===== USERS =====
INSERT INTO users (employeeID, firstName, lastName, role, active, employeePin) VALUES ('001', 'Devin', 'Baysa', 'ADMIN', 1, '1234');
INSERT INTO users (employeeID, firstName, lastName, role, active, employeePin) VALUES ('002', 'Maruos', 'Balyos', 'ADMIN', 1, '1234');
INSERT INTO users (employeeID, firstName, lastName, role, active, employeePin) VALUES ('003', 'Nisa', 'Bulut', 'ADMIN', 1, '1234');
INSERT INTO users (employeeID, firstName, lastName, role, active, employeePin) VALUES ('004', 'Test', 'One', 'CASHIER', 1, '1234');
INSERT INTO users (employeeID, firstName, lastName, role, active, employeePin) VALUES ('005', 'Test', 'Two', 'MANAGER', 1, '1234');

-- ===== PRODUCTS =====

-- Dept 11 - Clothing & Apparel
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11001', 'T-Shirt', 19.99, 75, 10, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11002', 'Jeans', 49.99, 50, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11003', 'Jacket', 89.99, 30, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11004', 'Socks (3-Pack)', 9.99, 100, 15, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11005', 'Baseball Hat', 24.99, 40, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11006', 'Sneakers', 79.99, 35, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11007', 'Belt', 14.99, 45, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11008', 'Scarf', 12.99, 3, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11009', 'Gloves', 9.99, 4, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11010', 'Shorts', 29.99, 40, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11011', 'Hoodie', 59.99, 2, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11012', 'Dress', 69.99, 20, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11013', 'Polo Shirt', 34.99, 30, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11014', 'Sweater', 44.99, 25, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('11015', 'Sweatpants', 39.99, 30, 8, 1);

-- Dept 12 - Home Goods
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('12001', 'Bath Towel', 19.99, 60, 10, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('12002', 'Bed Sheet Set', 49.99, 25, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('12003', 'Pillow (2-Pack)', 29.99, 40, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('12004', 'Blanket', 39.99, 20, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('12005', 'Curtains', 34.99, 15, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('12006', 'Throw Pillow', 14.99, 50, 10, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('12007', 'Laundry Basket', 24.99, 20, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('12008', 'Storage Bins (3-Pack)', 29.99, 4, 5, 1);

-- Dept 13 - Kitchen
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('13001', 'Non-Stick Pan', 34.99, 25, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('13002', 'Coffee Maker', 49.99, 15, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('13003', 'Cutting Board', 14.99, 30, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('13004', 'Dish Set (12-Piece)', 59.99, 10, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('13005', 'Cookware Set', 89.99, 2, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('13006', 'Kitchen Knife Set', 44.99, 12, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('13007', 'Blender', 39.99, 10, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('13008', 'Toaster', 29.99, 8, 3, 1);

-- Dept 14 - Electronics
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('14001', 'Headphones', 79.99, 20, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('14002', 'USB-C Charger', 19.99, 50, 10, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('14003', 'Bluetooth Speaker', 49.99, 15, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('14004', 'Smart Watch', 149.99, 10, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('14005', 'Wireless Earbuds', 59.99, 2, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('14006', 'Power Bank', 29.99, 25, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('14007', 'HDMI Cable', 12.99, 40, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('14008', 'Laptop Stand', 34.99, 15, 3, 1);

-- Dept 15 - Beauty & Personal Care
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('15001', 'Perfume', 59.99, 20, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('15002', 'Face Moisturizer', 24.99, 30, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('15003', 'Shampoo', 9.99, 60, 10, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('15004', 'Conditioner', 9.99, 60, 10, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('15005', 'Makeup Kit', 44.99, 15, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('15006', 'Razor Set', 14.99, 40, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('15007', 'Body Lotion', 12.99, 4, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('15008', 'Deodorant', 7.99, 75, 15, 1);

-- Dept 16 - Sporting Goods
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('16001', 'Yoga Mat', 29.99, 20, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('16002', 'Water Bottle', 19.99, 40, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('16003', 'Dumbbell Set', 49.99, 10, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('16004', 'Jump Rope', 9.99, 30, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('16005', 'Gym Bag', 34.99, 15, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('16006', 'Resistance Bands', 14.99, 3, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('16007', 'Running Shoes', 89.99, 20, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('16008', 'Foam Roller', 24.99, 12, 3, 1);

-- Dept 17 - Toys & Kids
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('17001', 'Board Game', 29.99, 20, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('17002', 'Action Figure', 14.99, 35, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('17003', 'Stuffed Animal', 19.99, 25, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('17004', 'Lego Set', 49.99, 2, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('17005', 'Puzzle (1000 Piece)', 19.99, 15, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('17006', 'Remote Control Car', 39.99, 10, 3, 1);

-- Dept 18 - Bags & Luggage
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('18001', 'Backpack', 44.99, 25, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('18002', 'Tote Bag', 24.99, 30, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('18003', 'Suitcase', 99.99, 10, 3, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('18004', 'Wallet', 19.99, 40, 8, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('18005', 'Fanny Pack', 14.99, 3, 5, 1);
INSERT INTO products (barcode, name, price, stockQuantity, lowThreshold, active) VALUES ('18006', 'Duffle Bag', 39.99, 15, 3, 1);
