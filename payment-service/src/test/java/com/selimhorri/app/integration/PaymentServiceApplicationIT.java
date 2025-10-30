// src/test/java/com/selimhorri/app/integration/PaymentServiceApplicationIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentServiceApplicationIT {

    @Test
    void contextLoads() {
        assertTrue(true, "El contexto de Spring debería cargarse con JPA, RestTemplate, etc.");
    }
}