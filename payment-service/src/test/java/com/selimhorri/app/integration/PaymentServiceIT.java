// src/test/java/com/selimhorri/app/integration/PaymentServiceIT.java
package com.selimhorri.app.integration;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class PaymentServiceIT {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void shouldFindPaymentByIdWithMockedOrder() {
        // Arrange
        Payment payment = paymentRepository.save(Payment.builder()
                .orderId(1500)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build());
        
        // Mock del RestTemplate para simular la llamada a Order-Service
        OrderDto mockOrderDto = OrderDto.builder()
                .orderId(1500)
                .orderDate(LocalDateTime.now())
                .orderDesc("Mocked Order")
                .orderFee(199.99)
                .build();
        
        when(restTemplate.getForObject(anyString(), eq(OrderDto.class)))
                .thenReturn(mockOrderDto);
        
        // Act
        PaymentDto result = paymentService.findById(payment.getPaymentId());
        
        // Assert
        assertNotNull(result);
        assertEquals(payment.getPaymentId(), result.getPaymentId());
        assertNotNull(result.getOrderDto());
        assertEquals(1500, result.getOrderDto().getOrderId());
        assertEquals("Mocked Order", result.getOrderDto().getOrderDesc());
        assertTrue(result.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
    }

    @Test
    void shouldFindAllPaymentsWithMockedOrders() {
        // Arrange
        Payment payment1 = paymentRepository.save(Payment.builder()
                .orderId(1600)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build());
        
        Payment payment2 = paymentRepository.save(Payment.builder()
                .orderId(1700)
                .isPayed(false)
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build());
        
        // Mock del RestTemplate
        OrderDto mockOrder1 = OrderDto.builder()
                .orderId(1600)
                .orderDesc("Order 1600")
                .orderFee(100.0)
                .build();
        
        OrderDto mockOrder2 = OrderDto.builder()
                .orderId(1700)
                .orderDesc("Order 1700")
                .orderFee(200.0)
                .build();
        
        when(restTemplate.getForObject(anyString(), eq(OrderDto.class)))
                .thenReturn(mockOrder1)
                .thenReturn(mockOrder2);
        
        // Act
        List<PaymentDto> results = paymentService.findAll();
        
        // Assert
        assertNotNull(results);
        assertTrue(results.size() >= 2);
        
        // Verificar que los orders mockeados están presentes
        boolean foundOrder1600 = results.stream()
                .anyMatch(p -> p.getOrderDto() != null && 
                              p.getOrderDto().getOrderId().equals(1600));
        boolean foundOrder1700 = results.stream()
                .anyMatch(p -> p.getOrderDto() != null && 
                              p.getOrderDto().getOrderId().equals(1700));
        
        assertTrue(foundOrder1600);
        assertTrue(foundOrder1700);
    }

    @Test
    void shouldSavePaymentWithoutCallingRestTemplate() {
        // Arrange
        // Crear OrderDto primero para el PaymentDto
        OrderDto orderDto = OrderDto.builder()
                .orderId(1800)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order for Payment")
                .orderFee(250.0)
                .build();
        
        PaymentDto paymentDto = PaymentDto.builder()
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .orderDto(orderDto)  // ← CORREGIDO: usar orderDto en lugar de orderId
                .build();
        
        // Act - Save no debería llamar a RestTemplate
        PaymentDto result = paymentService.save(paymentDto);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getPaymentId());
        assertNotNull(result.getOrderDto());
        assertEquals(1800, result.getOrderDto().getOrderId());
        assertFalse(result.getIsPayed());
        assertEquals(PaymentStatus.NOT_STARTED, result.getPaymentStatus());
        
        // Verificar que se guardó en la base de datos
        Payment savedPayment = paymentRepository.findById(result.getPaymentId()).orElse(null);
        assertNotNull(savedPayment);
        assertEquals(1800, savedPayment.getOrderId()); // La entidad Payment sí tiene orderId
    }

    @Test
    void shouldUpdatePaymentStatus() {
        // Arrange
        // Primero crear y guardar un pago
        OrderDto orderDto = OrderDto.builder()
                .orderId(1900)
                .orderDesc("Order to Update Payment")
                .orderFee(300.0)
                .build();
        
        PaymentDto originalPayment = PaymentDto.builder()
                .isPayed(false)
                .paymentStatus(PaymentStatus.NOT_STARTED)
                .orderDto(orderDto)
                .build();
        
        PaymentDto savedPayment = paymentService.save(originalPayment);
        
        // Crear DTO actualizado
        PaymentDto updatedPayment = PaymentDto.builder()
                .paymentId(savedPayment.getPaymentId())
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .orderDto(orderDto)
                .build();
        
        // Act
        PaymentDto result = paymentService.update(updatedPayment);
        
        // Assert
        assertNotNull(result);
        assertEquals(savedPayment.getPaymentId(), result.getPaymentId());
        assertTrue(result.getIsPayed());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
    }

}