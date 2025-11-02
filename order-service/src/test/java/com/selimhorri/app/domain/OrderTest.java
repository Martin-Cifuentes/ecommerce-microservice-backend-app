// src/test/java/com/selimhorri/app/domain/OrderTest.java
package com.selimhorri.app.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithValidData() {
        // Arrange
        Cart cart = Cart.builder()
                .cartId(1)
                .userId(100)
                .build();
        
        LocalDateTime orderDate = LocalDateTime.now();
        
        // Act
        Order order = Order.builder()
                .orderId(1)
                .orderDate(orderDate)
                .orderDesc("Test order description")
                .orderFee(99.99)
                .cart(cart)
                .build();
        
        // Assert
        assertNotNull(order);
        assertEquals(1, order.getOrderId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals("Test order description", order.getOrderDesc());
        assertEquals(99.99, order.getOrderFee());
        assertEquals(cart, order.getCart());
    }

    @Test
    void shouldCreateOrderWithNoArgsConstructor() {
        // Arrange & Act
        Order order = new Order();
        order.setOrderId(2);
        order.setOrderDesc("Another test order");
        order.setOrderFee(49.99);
        
        // Assert
        assertNotNull(order);
        assertEquals(2, order.getOrderId());
        assertEquals("Another test order", order.getOrderDesc());
        assertEquals(49.99, order.getOrderFee());
        assertNull(order.getCart()); // Cart no establecido
    }
}