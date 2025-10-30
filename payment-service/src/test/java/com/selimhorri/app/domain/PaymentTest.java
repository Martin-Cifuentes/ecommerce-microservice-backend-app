// src/test/java/com/selimhorri/app/domain/PaymentTest.java
package com.selimhorri.app.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void shouldCreatePaymentWithValidData() {
        // Arrange & Act
        Payment payment = Payment.builder()
                .paymentId(1)
                .orderId(100)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
        
        // Assert
        assertNotNull(payment);
        assertEquals(1, payment.getPaymentId());
        assertEquals(100, payment.getOrderId());
        assertTrue(payment.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, payment.getPaymentStatus());
    }

    @Test
    void shouldCreatePaymentWithNoArgsConstructor() {
        // Arrange & Act
        Payment payment = new Payment();
        payment.setPaymentId(2);
        payment.setOrderId(200);
        payment.setIsPayed(false);
        payment.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        
        // Assert
        assertNotNull(payment);
        assertEquals(2, payment.getPaymentId());
        assertEquals(200, payment.getOrderId());
        assertFalse(payment.getIsPayed());
        assertEquals(PaymentStatus.IN_PROGRESS, payment.getPaymentStatus());
    }

    @Test
    void shouldHandleNotStartedPaymentStatus() {
        // Arrange & Act
        Payment payment = Payment.builder()
                .paymentId(3)
                .orderId(300)
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();
        
        // Assert
        assertEquals(PaymentStatus.NOT_STARTED, payment.getPaymentStatus());
        assertEquals("not_started", payment.getPaymentStatus().getStatus());
        assertFalse(payment.getIsPayed());
    }
}