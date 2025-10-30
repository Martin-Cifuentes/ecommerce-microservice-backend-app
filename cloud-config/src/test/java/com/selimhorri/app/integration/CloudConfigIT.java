// src/test/java/com/selimhorri/app/integration/CloudConfigIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
class CloudConfigIT {

    @Test
    void contextLoads() {
        // Verifica que el contexto Spring se carga con Config Server y Eureka Client
        assertTrue(true, "El contexto de Spring debería cargarse con Config Server y Eureka Client");
    }
}