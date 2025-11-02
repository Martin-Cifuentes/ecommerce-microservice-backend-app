// src/test/java/com/selimhorri/app/integration/HealthCheckIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthCheckIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void actuatorHealthShouldReturnUp() {
        // Verifica el endpoint de health de Spring Boot Actuator
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"UP\""));
    }

    @Test
    void actuatorInfoShouldBeAccessible() {
        // Verifica el endpoint de info
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/info", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}