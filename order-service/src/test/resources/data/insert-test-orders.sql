-- src/test/resources/data/insert-test-orders.sql
INSERT INTO carts (cart_id, user_id, created_at, updated_at) 
VALUES (1, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO orders (order_id, order_date, order_desc, order_fee, cart_id, created_at, updated_at) 
VALUES (1, CURRENT_TIMESTAMP, 'Test Order 1', 50.0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO orders (order_id, order_date, order_desc, order_fee, cart_id, created_at, updated_at) 
VALUES (2, CURRENT_TIMESTAMP, 'Test Order 2', 75.0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);