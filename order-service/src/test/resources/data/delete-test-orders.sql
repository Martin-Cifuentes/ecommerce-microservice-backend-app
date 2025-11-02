-- src/test/resources/data/delete-test-orders.sql
DELETE FROM orders WHERE order_id IN (1, 2);
DELETE FROM carts WHERE cart_id = 1;