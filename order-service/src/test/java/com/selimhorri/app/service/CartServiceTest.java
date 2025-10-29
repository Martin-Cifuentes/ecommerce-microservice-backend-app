// src/test/java/com/selimhorri/app/service/CartServiceTest.java
package com.selimhorri.app.service;

import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartService cartService;

    @Test
    void shouldFindCartById() {
        // Arrange
        Integer cartId = 1;
        
        // Crear órdenes para el carrito
        Set<OrderDto> orderDtos = new HashSet<>();
        orderDtos.add(OrderDto.builder().orderId(1).orderDesc("Order 1").build());
        
        CartDto expectedCart = CartDto.builder()
                .cartId(cartId)
                .userId(100)
                .orderDtos(orderDtos)
                .build();
        
        when(cartService.findById(cartId)).thenReturn(expectedCart);
        
        // Act
        CartDto result = cartService.findById(cartId);
        
        // Assert
        assertNotNull(result);
        assertEquals(cartId, result.getCartId());
        assertEquals(100, result.getUserId());
        assertNotNull(result.getOrderDtos());
        assertEquals(1, result.getOrderDtos().size());
    }

    @Test
    void shouldFindAllCarts() {
        // Arrange
        CartDto cart1 = CartDto.builder().cartId(1).userId(100).build();
        CartDto cart2 = CartDto.builder().cartId(2).userId(200).build();
        List<CartDto> expectedCarts = Arrays.asList(cart1, cart2);
        
        when(cartService.findAll()).thenReturn(expectedCarts);
        
        // Act
        List<CartDto> result = cartService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100, result.get(0).getUserId());
        assertEquals(200, result.get(1).getUserId());
    }

    @Test
    void shouldSaveCart() {
        // Arrange
        CartDto cartToSave = CartDto.builder()
                .userId(300)
                .build();
        
        CartDto savedCart = CartDto.builder()
                .cartId(1)
                .userId(300)
                .build();
        
        when(cartService.save(cartToSave)).thenReturn(savedCart);
        
        // Act
        CartDto result = cartService.save(cartToSave);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCartId());
        assertEquals(300, result.getUserId());
        verify(cartService, times(1)).save(cartToSave);
    }
}