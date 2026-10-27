-- =====================================================================
-- Online Shopping System (BCSP-064 Final Year Project)
-- Initial Sample Seed Data (MySQL 8)
-- =====================================================================

USE shopping_db;

-- ---------------------------------------------------------------------
-- 1. Insert Default Admins
-- Password for 'admin' is 'Admin@123' (BCrypt hashed with cost 12)
-- ---------------------------------------------------------------------
INSERT INTO admins (admin_id, username, password, email) VALUES
(1, 'admin', '$2a$12$PWgd/grZYZh5gnYOJ/1XLOAmaO1MLyyLsj6JTxlWaB/TzHQd/qmHG', 'admin@shopping.com');

-- ---------------------------------------------------------------------
-- 2. Insert Default Users (Customers)
-- Password for all sample users is 'Customer@123' (BCrypt hashed)
-- ---------------------------------------------------------------------
INSERT INTO users (user_id, name, email, password, phone, address) VALUES
(1, 'John Doe', 'john.doe@example.com', '$2a$12$pCm0Jv.gREhUOqTqcUJyw.m7x6RPd89ZSMPTFEXBtpxRz9nopaUP.', '9876543210', '124 Park Avenue, Connaught Place, New Delhi - 110001'),
(2, 'Priya Sharma', 'priya.sharma@example.com', '$2a$12$pCm0Jv.gREhUOqTqcUJyw.m7x6RPd89ZSMPTFEXBtpxRz9nopaUP.', '9812345678', 'Flat 402, Lotus Residency, Indiranagar, Bengaluru - 560038'),
(3, 'Amit Kumar', 'amit.kumar@example.com', '$2a$12$pCm0Jv.gREhUOqTqcUJyw.m7x6RPd89ZSMPTFEXBtpxRz9nopaUP.', '9923456789', '75 Civil Lines, Near Railway Junction, Jaipur - 302006');

-- ---------------------------------------------------------------------
-- 3. Insert Product Categories
-- ---------------------------------------------------------------------
INSERT INTO categories (category_id, category_name, status) VALUES
(1, 'Electronics & Gadgets', 'ACTIVE'),
(2, 'Fashion & Apparel', 'ACTIVE'),
(3, 'Home & Kitchen', 'ACTIVE'),
(4, 'Books & Education', 'ACTIVE'),
(5, 'Sports & Fitness', 'ACTIVE');

-- ---------------------------------------------------------------------
-- 4. Insert Products
-- ---------------------------------------------------------------------
INSERT INTO products (product_id, category_id, product_name, price, stock, image, description, status) VALUES
-- Electronics
(1, 1, 'Wireless Active Noise-Cancelling Headphones', 4999.00, 25, 'headphones.png', 'Premium over-ear Bluetooth headphones with 40mm drivers, 30 hours battery life, and crystal-clear microphone audio.', 'ACTIVE'),
(2, 1, 'Smart Fitness Watch Series 5', 2499.00, 40, 'smartwatch.png', '1.69 inch HD touch display, real-time heart rate monitoring, SpO2 sensor, sleep tracker, and IP68 waterproof rating.', 'ACTIVE'),
(3, 1, 'Ultra HD 4K Action Camera', 8499.00, 15, 'camera.png', '20MP sensor, 4K/60fps video recording, dual screens, 30m waterproof housing, and wide-angle 170-degree lens.', 'ACTIVE'),
(4, 1, 'Ergonomic RGB Mechanical Keyboard', 3299.00, 30, 'keyboard.png', 'Tenkeyless layout, hot-swappable tactile blue switches, customizable RGB backlighting, and detachable braided USB-C cable.', 'ACTIVE'),

-- Fashion
(5, 2, 'Men\'s Pure Cotton Slim Fit Casual Shirt', 999.00, 50, 'shirt.png', '100% breathable combed cotton, full sleeves, spread collar, and stylish contrast button design.', 'ACTIVE'),
(6, 2, 'Women\'s Floral Print Summer Maxi Dress', 1499.00, 35, 'dress.png', 'Elegant flowy georgette fabric with micro-floral print, comfortable smocked bodice, and adjustable shoulder straps.', 'ACTIVE'),
(7, 2, 'Handcrafted Genuine Leather Bi-fold Wallet', 599.00, 60, 'wallet.png', 'Top-grain cowhide leather, 8 card slots, dual cash compartments, and integrated RFID-blocking security lining.', 'ACTIVE'),

-- Home & Kitchen
(8, 3, 'Stainless Steel Fast-Boil Electric Kettle 1.8L', 1299.00, 20, 'kettle.png', '1500W rapid boil heating element, auto shut-off protection, boil-dry sensor, and 360-degree cordless swivel base.', 'ACTIVE'),
(9, 3, 'Granite Non-Stick Cookware Set (3 Pieces)', 2199.00, 18, 'cookware.png', 'Includes frying pan, saucepan with glass lid, and dosa tawa. PFOA-free 5-layer hard anodized granite coating.', 'ACTIVE'),
(10, 3, 'Artisanal Ceramic Coffee Mug Set of 4', 499.00, 45, 'mugs.png', 'Microwave and dishwasher safe handcrafted ceramic mugs, 350ml capacity with heat-resistant matte finish handles.', 'ACTIVE'),

-- Books
(11, 4, 'Java: The Complete Reference (12th Edition)', 850.00, 50, 'java_book.png', 'Comprehensive programming guide covering core Java, lambdas, streams, modules, concurrency, and modern language features.', 'ACTIVE'),
(12, 4, 'Clean Code: Handbook of Agile Software Craftsmanship', 950.00, 30, 'clean_code.png', 'Classic software engineering textbook by Robert C. Martin on best practices, refactoring, and code readability.', 'ACTIVE'),

-- Sports
(13, 5, 'High-Density Anti-Slip Yoga Mat (6mm)', 799.00, 28, 'yogamat.png', 'Eco-friendly TPE material with laser-etched body alignment lines, sweat-resistant non-slip grip, and free shoulder strap.', 'ACTIVE'),
(14, 5, 'Adjustable Rubber Encased Dumbbell Pair (10 KG)', 2999.00, 12, 'dumbbells.png', 'Cast iron weight plates with floor-protecting rubber encasement, ergonomic chrome knurled handles, and star lock collars.', 'ACTIVE');

-- ---------------------------------------------------------------------
-- 5. Insert Sample Cart Items (for John Doe user_id=1)
-- ---------------------------------------------------------------------
INSERT INTO cart (cart_id, user_id, product_id, quantity) VALUES
(1, 1, 1, 1),
(2, 1, 5, 2);

-- ---------------------------------------------------------------------
-- 6. Insert Sample Orders
-- ---------------------------------------------------------------------
INSERT INTO orders (order_id, user_id, order_date, total_amount, shipping_address, order_status) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), 5849.00, '124 Park Avenue, Connaught Place, New Delhi - 110001', 'DELIVERED'),
(2, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), 2499.00, 'Flat 402, Lotus Residency, Indiranagar, Bengaluru - 560038', 'PROCESSING'),
(3, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), 1799.00, '75 Civil Lines, Near Railway Junction, Jaipur - 302006', 'SHIPPED');

-- ---------------------------------------------------------------------
-- 7. Insert Order Items
-- ---------------------------------------------------------------------
-- Order 1: 1 x Headphones (4999) + 1 x Java Book (850) = 5849
INSERT INTO order_items (item_id, order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 1, 4999.00),
(2, 1, 11, 1, 850.00);

-- Order 2: 1 x Smart Watch (2499) = 2499
INSERT INTO order_items (item_id, order_id, product_id, quantity, unit_price) VALUES
(3, 2, 2, 1, 2499.00);

-- Order 3: 1 x Cotton Shirt (999) + 1 x Yoga Mat (800 special/799) = 1798 ~ approx
INSERT INTO order_items (item_id, order_id, product_id, quantity, unit_price) VALUES
(4, 3, 5, 1, 999.00),
(5, 3, 13, 1, 799.00);

-- ---------------------------------------------------------------------
-- 8. Insert Payments
-- ---------------------------------------------------------------------
INSERT INTO payments (payment_id, order_id, user_id, payment_mode, payment_status, transaction_id, payment_date) VALUES
(1, 1, 1, 'ONLINE', 'COMPLETED', 'TXN9812039481', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 2, 2, 'COD', 'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 3, 3, 'ONLINE', 'COMPLETED', 'TXN9812039912', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ---------------------------------------------------------------------
-- 9. Insert Tracking Details
-- ---------------------------------------------------------------------
INSERT INTO tracking_details (tracking_id, order_id, courier_name, tracking_no, delivery_status, updated_at) VALUES
(1, 1, 'BlueDart Express', 'BD-99214012IN', 'DELIVERED', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 2, 'Delhivery Logistics', 'DL-88231044IN', 'ORDER_PLACED', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 3, 'DTDC Express', 'DT-77123901IN', 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 12 HOUR));

-- ---------------------------------------------------------------------
-- 10. Insert Feedback Messages
-- ---------------------------------------------------------------------
INSERT INTO feedback_messages (id, name, email, message, created_at) VALUES
(1, 'Rahul Verma', 'rahul.verma@example.com', 'Excellent website and seamless checkout experience. Loved the swift delivery of my headphones!', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'Sneha Patel', 'sneha.patel@example.com', 'Can you please add more technical computer science books to the collection? Overall great UI.', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ---------------------------------------------------------------------
-- 11. Insert Ratings & Reviews (verified buyers)
-- ---------------------------------------------------------------------
INSERT INTO ratings (rating_id, user_id, product_id, rating, review, created_at) VALUES
(1, 1, 1, 5, 'Superb sound quality and deep bass. Active noise cancellation works like a charm on flights!', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 1, 11, 5, 'Must-read book for any BCA or MCA student preparing for software engineering interviews.', DATE_SUB(NOW(), INTERVAL 3 DAY));
