// src/test/java/com/selimhorri/app/domain/PaymentStatusTest.java
package com.selimhorri.app.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusTest {

    @Test
    void shouldHaveCorrectStatusValues() {
        // Assert
        assertEquals("not_started", PaymentStatus.NOT_STARTED.getStatus());
        assertEquals("in_progress", PaymentStatus.IN_PROGRESS.getStatus());
        assertEquals("completed", PaymentStatus.COMPLETED.getStatus());
    }

    @Test
    void shouldHandleAllEnumValues() {
        // Arrange
        PaymentStatus[] statuses = PaymentStatus.values();
        
        // Assert
        assertEquals(3, statuses.length);
        assertArrayEquals(new PaymentStatus[]{
            PaymentStatus.NOT_STARTED,
            PaymentStatus.IN_PROGRESS, 
            PaymentStatus.COMPLETED
        }, statuses);
    }

    @Test
    void shouldConvertStringToEnum() {
        // Act & Assert
        assertEquals(PaymentStatus.NOT_STARTED, PaymentStatus.valueOf("NOT_STARTED"));
        assertEquals(PaymentStatus.IN_PROGRESS, PaymentStatus.valueOf("IN_PROGRESS"));
        assertEquals(PaymentStatus.COMPLETED, PaymentStatus.valueOf("COMPLETED"));
    }
}