// src/test/java/com/selimhorri/app/dto/OrderDtoTest.java
package com.selimhorri.app.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {

    @Test
    void shouldCreateOrderDtoWithValidData() {
        // Arrange
        LocalDateTime orderDate = LocalDateTime.now();
        
        // Act
        OrderDto orderDto = OrderDto.builder()
                .orderId(1)
                .orderDate(orderDate)
                .orderDesc("Test order description")
                .orderFee(150.75)
                .build();
        
        // Assert
        assertNotNull(orderDto);
        assertEquals(1, orderDto.getOrderId());
        assertEquals(orderDate, orderDto.getOrderDate());
        assertEquals("Test order description", orderDto.getOrderDesc());
        assertEquals(150.75, orderDto.getOrderFee());
    }

    @Test
    void shouldCreateOrderDtoWithSetters() {
        // Arrange
        OrderDto orderDto = new OrderDto();
        LocalDateTime orderDate = LocalDateTime.now();
        
        // Act
        orderDto.setOrderId(2);
        orderDto.setOrderDate(orderDate);
        orderDto.setOrderDesc("Another order");
        orderDto.setOrderFee(99.99);
        
        // Assert
        assertEquals(2, orderDto.getOrderId());
        assertEquals(orderDate, orderDto.getOrderDate());
        assertEquals("Another order", orderDto.getOrderDesc());
        assertEquals(99.99, orderDto.getOrderFee());
    }
}