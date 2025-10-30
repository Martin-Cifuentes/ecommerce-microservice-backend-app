// src/test/java/com/selimhorri/app/integration/PaymentStatusIT.java
package com.selimhorri.app.integration;

import com.selimhorri.app.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusIT {

    @Test
    void shouldHaveCorrectStatusValues() {
        assertEquals("not_started", PaymentStatus.NOT_STARTED.getStatus());
        assertEquals("in_progress", PaymentStatus.IN_PROGRESS.getStatus());
        assertEquals("completed", PaymentStatus.COMPLETED.getStatus());
    }

    @Test
    void shouldHandleAllEnumValues() {
        PaymentStatus[] statuses = PaymentStatus.values();
        
        assertEquals(3, statuses.length);
        assertArrayEquals(new PaymentStatus[]{
            PaymentStatus.NOT_STARTED,
            PaymentStatus.IN_PROGRESS, 
            PaymentStatus.COMPLETED
        }, statuses);
    }

    @Test
    void shouldConvertStringToEnum() {
        assertEquals(PaymentStatus.NOT_STARTED, PaymentStatus.valueOf("NOT_STARTED"));
        assertEquals(PaymentStatus.IN_PROGRESS, PaymentStatus.valueOf("IN_PROGRESS"));
        assertEquals(PaymentStatus.COMPLETED, PaymentStatus.valueOf("COMPLETED"));
    }

}