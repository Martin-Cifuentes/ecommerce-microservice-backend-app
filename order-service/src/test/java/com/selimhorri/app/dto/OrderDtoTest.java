// src/test/java/com/selimhorri/app/dto/OrderDtoTest.java
package com.selimhorri.app.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {

    @Test
    void shouldCreateOrderDtoWithBuilder() {
        // Arrange
        CartDto cartDto = CartDto.builder().cartId(1).userId(100).build();
        LocalDateTime orderDate = LocalDateTime.now();
        
        // Act
        OrderDto orderDto = OrderDto.builder()
                .orderId(1)
                .orderDate(orderDate)
                .orderDesc("Test order DTO")
                .orderFee(50.75)
                .cartDto(cartDto)
                .build();
        
        // Assert
        assertNotNull(orderDto);
        assertEquals(1, orderDto.getOrderId());
        assertEquals(orderDate, orderDto.getOrderDate());
        assertEquals("Test order DTO", orderDto.getOrderDesc());
        assertEquals(50.75, orderDto.getOrderFee());
        assertNotNull(orderDto.getCartDto());
        assertEquals(1, orderDto.getCartDto().getCartId());
    }

    @Test
    void shouldCreateOrderDtoWithSetters() {
        // Arrange
        OrderDto orderDto = new OrderDto();
        CartDto cartDto = new CartDto();
        cartDto.setCartId(2);
        cartDto.setUserId(200);
        
        LocalDateTime orderDate = LocalDateTime.now();
        
        // Act
        orderDto.setOrderId(2);
        orderDto.setOrderDate(orderDate);
        orderDto.setOrderDesc("Another order DTO");
        orderDto.setOrderFee(25.25);
        orderDto.setCartDto(cartDto);
        
        // Assert
        assertEquals(2, orderDto.getOrderId());
        assertEquals(orderDate, orderDto.getOrderDate());
        assertEquals("Another order DTO", orderDto.getOrderDesc());
        assertEquals(25.25, orderDto.getOrderFee());
        assertNotNull(orderDto.getCartDto());
        assertEquals(2, orderDto.getCartDto().getCartId());
    }
}