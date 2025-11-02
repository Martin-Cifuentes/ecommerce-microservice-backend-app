// src/test/java/com/selimhorri/app/domain/CartTest.java
package com.selimhorri.app.domain;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void shouldCreateCartWithValidData() {
        // Arrange & Act
        Cart cart = Cart.builder()
                .cartId(1)
                .userId(200)
                .orders(new HashSet<>())
                .build();
        
        // Assert
        assertNotNull(cart);
        assertEquals(1, cart.getCartId());
        assertEquals(200, cart.getUserId());
        assertNotNull(cart.getOrders());
        assertTrue(cart.getOrders().isEmpty());
    }

    @Test
    void shouldAddOrderToCart() {
        // Arrange
        Cart cart = Cart.builder()
                .cartId(1)
                .userId(300)
                .orders(new HashSet<>())
                .build();
        
        Order order = Order.builder()
                .orderId(1)
                .orderDesc("Order for cart")
                .orderFee(25.50)
                .cart(cart) // Establecer la relación bidireccional
                .build();
        
        // Act
        cart.getOrders().add(order);
        
        // Assert
        assertEquals(1, cart.getOrders().size());
        assertTrue(cart.getOrders().contains(order));
        assertEquals(cart, order.getCart()); // Verificar relación bidireccional
    }
}