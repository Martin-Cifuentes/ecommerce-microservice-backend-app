// src/test/java/com/selimhorri/app/dto/PaymentDtoTest.java
package com.selimhorri.app.dto;

import com.selimhorri.app.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentDtoTest {

    @Test
    void shouldCreatePaymentDtoWithBuilder() {
        // Arrange
        OrderDto orderDto = OrderDto.builder()
                .orderId(100)
                .orderDesc("Order for payment")
                .orderFee(200.0)
                .build();
        
        // Act
        PaymentDto paymentDto = PaymentDto.builder()
                .paymentId(1)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .orderDto(orderDto)
                .build();
        
        // Assert
        assertNotNull(paymentDto);
        assertEquals(1, paymentDto.getPaymentId());
        assertTrue(paymentDto.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, paymentDto.getPaymentStatus());
        assertNotNull(paymentDto.getOrderDto());
        assertEquals(100, paymentDto.getOrderDto().getOrderId());
    }

    @Test
    void shouldCreatePaymentDtoWithoutOrder() {
        // Arrange & Act
        PaymentDto paymentDto = PaymentDto.builder()
                .paymentId(2)
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();
        
        // Assert
        assertNotNull(paymentDto);
        assertEquals(2, paymentDto.getPaymentId());
        assertFalse(paymentDto.getIsPayed());
        assertEquals(PaymentStatus.NOT_STARTED, paymentDto.getPaymentStatus());
        assertNull(paymentDto.getOrderDto()); // Order no establecido
    }

    @Test
    void shouldCreatePaymentDtoWithSetters() {
        // Arrange
        PaymentDto paymentDto = new PaymentDto();
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(300);
        
        // Act
        paymentDto.setPaymentId(3);
        paymentDto.setIsPayed(true);
        paymentDto.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        paymentDto.setOrderDto(orderDto);
        
        // Assert
        assertEquals(3, paymentDto.getPaymentId());
        assertTrue(paymentDto.getIsPayed());
        assertEquals(PaymentStatus.IN_PROGRESS, paymentDto.getPaymentStatus());
        assertNotNull(paymentDto.getOrderDto());
        assertEquals(300, paymentDto.getOrderDto().getOrderId());
    }
}