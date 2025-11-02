// src/test/java/com/selimhorri/app/dto/CartDtoTest.java
package com.selimhorri.app.dto;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class CartDtoTest {

    @Test
    void shouldCreateCartDtoWithBuilder() {
        // Arrange
        Set<OrderDto> orderDtos = new HashSet<>();
        orderDtos.add(OrderDto.builder().orderId(1).orderDesc("Order 1").build());
        
        UserDto userDto = UserDto.builder().userId(1).firstName("John").build();
        
        // Act
        CartDto cartDto = CartDto.builder()
                .cartId(1)
                .userId(400)
                .orderDtos(orderDtos)
                .userDto(userDto)
                .build();
        
        // Assert
        assertNotNull(cartDto);
        assertEquals(1, cartDto.getCartId());
        assertEquals(400, cartDto.getUserId());
        assertNotNull(cartDto.getOrderDtos());
        assertEquals(1, cartDto.getOrderDtos().size());
        assertNotNull(cartDto.getUserDto());
        assertEquals("John", cartDto.getUserDto().getFirstName());
    }

    @Test
    void shouldCreateCartDtoWithSetters() {
        // Arrange
        CartDto cartDto = new CartDto();
        Set<OrderDto> orderDtos = new HashSet<>();
        orderDtos.add(new OrderDto());
        
        UserDto userDto = new UserDto();
        userDto.setUserId(2);
        userDto.setFirstName("Jane");
        
        // Act
        cartDto.setCartId(2);
        cartDto.setUserId(500);
        cartDto.setOrderDtos(orderDtos);
        cartDto.setUserDto(userDto);
        
        // Assert
        assertEquals(2, cartDto.getCartId());
        assertEquals(500, cartDto.getUserId());
        assertNotNull(cartDto.getOrderDtos());
        assertEquals(1, cartDto.getOrderDtos().size());
        assertNotNull(cartDto.getUserDto());
        assertEquals("Jane", cartDto.getUserDto().getFirstName());
    }
}