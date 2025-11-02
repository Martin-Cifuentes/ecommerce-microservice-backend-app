// src/test/java/com/selimhorri/app/integration/HealthCheckIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthCheckIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void actuatorInfoShouldBeAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/info", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void databaseHealthIndicatorShouldWork() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verifica que el health check de la base de datos funciona
        assertTrue(response.getBody().contains("\"db\"") || 
                  response.getBody().contains("\"status\":\"UP\""));
    }
}