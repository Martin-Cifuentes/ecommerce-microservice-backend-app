// src/test/java/com/selimhorri/app/integration/ProductServiceApplicationIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationIT {

    @Test
    void contextLoads() {
        assertTrue(true, "El contexto de Spring debería cargarse con JPA, repositorios, etc.");
    }
}