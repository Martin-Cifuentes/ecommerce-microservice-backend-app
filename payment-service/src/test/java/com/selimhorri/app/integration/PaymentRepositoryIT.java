// src/test/java/com/selimhorri/app/integration/PaymentRepositoryIT.java
package com.selimhorri.app.integration;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentRepositoryIT {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldSavePayment() {
        // Arrange
        Payment payment = Payment.builder()
                .orderId(100)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
        
        // Act
        Payment savedPayment = paymentRepository.save(payment);
        
        // Assert
        assertNotNull(savedPayment);
        assertNotNull(savedPayment.getPaymentId());
        assertEquals(100, savedPayment.getOrderId());
        assertTrue(savedPayment.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, savedPayment.getPaymentStatus());
    }

    @Test
    void shouldSavePaymentWithNotStartedStatus() {
        // Arrange
        Payment payment = Payment.builder()
                .orderId(200)
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build();
        
        // Act
        Payment savedPayment = paymentRepository.save(payment);
        
        // Assert
        assertNotNull(savedPayment);
        assertEquals(200, savedPayment.getOrderId());
        assertFalse(savedPayment.getIsPayed());
        assertEquals(PaymentStatus.NOT_STARTED, savedPayment.getPaymentStatus());
    }

    @Test
    void shouldFindPaymentById() {
        // Arrange
        Payment payment = paymentRepository.save(Payment.builder()
                .orderId(300)
                .isPayed(true)
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build());
        
        Integer paymentId = payment.getPaymentId();
        
        // Act
        Optional<Payment> foundPayment = paymentRepository.findById(paymentId);
        
        // Assert
        assertTrue(foundPayment.isPresent());
        assertEquals(paymentId, foundPayment.get().getPaymentId());
        assertEquals(300, foundPayment.get().getOrderId());
        assertTrue(foundPayment.get().getIsPayed());
        assertEquals(PaymentStatus.IN_PROGRESS, foundPayment.get().getPaymentStatus());
    }

    @Test
    void shouldFindAllPayments() {
        // Arrange
        paymentRepository.save(Payment.builder()
                .orderId(400)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build());
        
        paymentRepository.save(Payment.builder()
                .orderId(500)
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build());
        
        // Act
        List<Payment> payments = paymentRepository.findAll();
        
        // Assert
        assertNotNull(payments);
        assertTrue(payments.size() >= 2);
    }

    @Test
    void shouldUpdatePayment() {
        // Arrange
        Payment payment = paymentRepository.save(Payment.builder()
                .orderId(600)
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .build());
        
        // Act - Actualizar el pago
        payment.setIsPayed(true);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Assert
        assertTrue(updatedPayment.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, updatedPayment.getPaymentStatus());
    }

    @Test
    void shouldDeletePayment() {
        // Arrange
        Payment payment = paymentRepository.save(Payment.builder()
                .orderId(700)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build());
        
        Integer paymentId = payment.getPaymentId();
        
        // Verificar que existe antes de eliminar
        assertTrue(paymentRepository.findById(paymentId).isPresent());
        
        // Act
        paymentRepository.deleteById(paymentId);
        
        // Assert
        Optional<Payment> deletedPayment = paymentRepository.findById(paymentId);
        assertFalse(deletedPayment.isPresent());
    }

    @Test
    void shouldFindPaymentsByOrderIdUsingStream() {
        // Arrange
        Integer targetOrderId = 800;
        paymentRepository.save(Payment.builder()
                .orderId(targetOrderId)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build());
        
        paymentRepository.save(Payment.builder()
                .orderId(targetOrderId)
                .isPayed(false)
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build());
        
        paymentRepository.save(Payment.builder()
                .orderId(900) // Diferente orderId
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build());
        
        // Act - Filtrar por orderId usando stream
        List<Payment> allPayments = paymentRepository.findAll();
        List<Payment> targetPayments = allPayments.stream()
                .filter(p -> targetOrderId.equals(p.getOrderId()))
                .collect(java.util.stream.Collectors.toList());
        
        // Assert
        assertNotNull(targetPayments);
        assertEquals(2, targetPayments.size());
        assertTrue(targetPayments.stream()
                .allMatch(p -> targetOrderId.equals(p.getOrderId())));
    }
}