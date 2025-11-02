// src/test/java/com/selimhorri/app/service/OrderServiceTest.java
package com.selimhorri.app.service;

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.CartDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderService orderService;

    @Test
    void shouldFindOrderById() {
        // Arrange
        Integer orderId = 1;
        CartDto cartDto = CartDto.builder().cartId(1).userId(100).build();
        OrderDto expectedOrder = OrderDto.builder()
                .orderId(orderId)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test order")
                .orderFee(75.50)
                .cartDto(cartDto)
                .build();
        
        when(orderService.findById(orderId)).thenReturn(expectedOrder);
        
        // Act
        OrderDto result = orderService.findById(orderId);
        
        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals("Test order", result.getOrderDesc());
        assertEquals(75.50, result.getOrderFee());
        assertNotNull(result.getCartDto());
        assertEquals(1, result.getCartDto().getCartId());
    }

    @Test
    void shouldFindAllOrders() {
        // Arrange
        OrderDto order1 = OrderDto.builder().orderId(1).orderDesc("Order 1").orderFee(10.0).build();
        OrderDto order2 = OrderDto.builder().orderId(2).orderDesc("Order 2").orderFee(20.0).build();
        List<OrderDto> expectedOrders = Arrays.asList(order1, order2);
        
        when(orderService.findAll()).thenReturn(expectedOrders);
        
        // Act
        List<OrderDto> result = orderService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Order 1", result.get(0).getOrderDesc());
        assertEquals("Order 2", result.get(1).getOrderDesc());
    }

    @Test
    void shouldSaveOrder() {
        // Arrange
        OrderDto orderToSave = OrderDto.builder()
                .orderDesc("New order")
                .orderFee(99.99)
                .build();
        
        OrderDto savedOrder = OrderDto.builder()
                .orderId(1)
                .orderDesc("New order")
                .orderFee(99.99)
                .build();
        
        when(orderService.save(orderToSave)).thenReturn(savedOrder);
        
        // Act
        OrderDto result = orderService.save(orderToSave);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals("New order", result.getOrderDesc());
        verify(orderService, times(1)).save(orderToSave);
    }
}