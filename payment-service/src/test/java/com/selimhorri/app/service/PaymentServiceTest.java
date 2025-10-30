// src/test/java/com/selimhorri/app/service/PaymentServiceTest.java
package com.selimhorri.app.service;

import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentService paymentService;

    @Test
    void shouldFindPaymentById() {
        // Arrange
        Integer paymentId = 1;
        OrderDto orderDto = OrderDto.builder().orderId(100).orderDesc("Test Order").build();
        PaymentDto expectedPayment = PaymentDto.builder()
                .paymentId(paymentId)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .orderDto(orderDto)
                .build();
        
        when(paymentService.findById(paymentId)).thenReturn(expectedPayment);
        
        // Act
        PaymentDto result = paymentService.findById(paymentId);
        
        // Assert
        assertNotNull(result);
        assertEquals(paymentId, result.getPaymentId());
        assertTrue(result.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertNotNull(result.getOrderDto());
        assertEquals(100, result.getOrderDto().getOrderId());
    }

    @Test
    void shouldFindAllPayments() {
        // Arrange
        PaymentDto payment1 = PaymentDto.builder()
                .paymentId(1)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
        
        PaymentDto payment2 = PaymentDto.builder()
                .paymentId(2)
                .isPayed(false)
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build();
        
        List<PaymentDto> expectedPayments = Arrays.asList(payment1, payment2);
        
        when(paymentService.findAll()).thenReturn(expectedPayments);
        
        // Act
        List<PaymentDto> result = paymentService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getIsPayed());
        assertFalse(result.get(1).getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, result.get(0).getPaymentStatus());
        assertEquals(PaymentStatus.IN_PROGRESS, result.get(1).getPaymentStatus());
    }

    @Test
    void shouldSavePayment() {
        // Arrange
        PaymentDto paymentToSave = PaymentDto.builder()
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();
        
        PaymentDto savedPayment = PaymentDto.builder()
                .paymentId(1)
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();
        
        when(paymentService.save(paymentToSave)).thenReturn(savedPayment);
        
        // Act
        PaymentDto result = paymentService.save(paymentToSave);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getPaymentId());
        assertFalse(result.getIsPayed());
        assertEquals(PaymentStatus.NOT_STARTED, result.getPaymentStatus());
        verify(paymentService, times(1)).save(paymentToSave);
    }
}