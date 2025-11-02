// src/test/java/com/selimhorri/app/integration/OrderRepositoryIT.java
package com.selimhorri.app.integration;

import com.selimhorri.app.domain.Order;
import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.repository.OrderRepository;
import com.selimhorri.app.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Test
    void shouldSaveOrder() {
        // Arrange
        Cart cart = cartRepository.save(Cart.builder().userId(100).build());
        
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order Description")
                .orderFee(99.99)
                .cart(cart)
                .build();
        
        // Act
        Order savedOrder = orderRepository.save(order);
        
        // Assert
        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getOrderId());
        assertEquals("Test Order Description", savedOrder.getOrderDesc());
        assertEquals(99.99, savedOrder.getOrderFee());
        assertEquals(cart, savedOrder.getCart());
    }

    @Test
    void shouldFindAllOrders() {
        // Arrange & Act
        List<Order> orders = orderRepository.findAll();
        
        // Assert
        assertNotNull(orders);
        // Puede estar vacío si no hay datos, pero la consulta debe funcionar
    }

    @Test
    void shouldDeleteOrder() {
        // Arrange
        Cart cart = cartRepository.save(Cart.builder().userId(200).build());
        Order order = orderRepository.save(Order.builder()
                .orderDate(LocalDateTime.now())
                .orderDesc("Order to delete")
                .orderFee(50.0)
                .cart(cart)
                .build());
        
        Integer orderId = order.getOrderId();
        
        // Act
        orderRepository.deleteById(orderId);
        
        // Assert
        Optional<Order> deletedOrder = orderRepository.findById(orderId);
        assertFalse(deletedOrder.isPresent());
    }
}