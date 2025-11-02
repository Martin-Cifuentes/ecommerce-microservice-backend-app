// src/test/java/com/selimhorri/app/integration/OrderServiceApplicationIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationIT {

    @Test
    void contextLoads() {
        // Verifica que el contexto Spring se carga con todas las dependencias
        assertTrue(true, "El contexto de Spring debería cargarse con JPA, Eureka, etc.");
    }
}