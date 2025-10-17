-- Insert sample data for testing
INSERT INTO orders (customer_name, customer_email, product_name, quantity, unit_price, total_amount, status, shipping_address, created_at, updated_at) VALUES
('John Doe', 'john.doe@example.com', 'Smartphone Samsung Galaxy S24', 2, 599.99, 1199.98, 'PENDING', '123 Main St, Apt 4B, New York, NY 10001', NOW(), NOW());

INSERT INTO orders (customer_name, customer_email, product_name, quantity, unit_price, total_amount, status, shipping_address, created_at, updated_at) VALUES
('Alice Smith', 'alice.smith@example.com', 'Laptop Dell XPS 13', 1, 1299.99, 1299.99, 'CONFIRMED', '789 Pine St, Unit 5A, Chicago, IL 60601', NOW(), NOW());

INSERT INTO orders (customer_name, customer_email, product_name, quantity, unit_price, total_amount, status, shipping_address, created_at, updated_at) VALUES
('Bob Johnson', 'bob.johnson@example.com', 'Tablet iPad Air', 3, 699.99, 2099.97, 'SHIPPED', '456 Oak Ave, Suite 12, Los Angeles, CA 90210', NOW(), NOW());

INSERT INTO orders (customer_name, customer_email, product_name, quantity, unit_price, total_amount, status, shipping_address, created_at, updated_at) VALUES
('Emily Brown', 'emily.brown@example.com', 'Headphones Sony WH-1000XM5', 1, 349.99, 349.99, 'DELIVERED', '321 Elm Dr, Floor 3, Miami, FL 33101', NOW(), NOW());

INSERT INTO orders (customer_name, customer_email, product_name, quantity, unit_price, total_amount, status, shipping_address, created_at, updated_at) VALUES
('Michael Davis', 'michael.davis@example.com', 'Smart Watch Apple Series 9', 2, 449.99, 899.98, 'CANCELLED', '654 Maple Ln, Apt 8C, Seattle, WA 98101', NOW(), NOW());