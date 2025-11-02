// src/test/java/com/selimhorri/app/ServiceDiscoveryApplicationTests.java
package com.selimhorri.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ServiceDiscoveryApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto Spring se carga sin errores
        assertTrue(true, "El contexto de Spring debería cargarse correctamente");
    }
}