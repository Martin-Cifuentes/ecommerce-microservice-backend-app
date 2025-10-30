// src/test/java/com/selimhorri/app/integration/ServiceDiscoveryIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
class ServiceDiscoveryIT {

    @Test
    void contextLoads() {
        // Verifica que el contexto Spring se carga con todas las dependencias
        assertTrue(true, "El contexto de Spring debería cargarse con todas las configuraciones");
    }
}