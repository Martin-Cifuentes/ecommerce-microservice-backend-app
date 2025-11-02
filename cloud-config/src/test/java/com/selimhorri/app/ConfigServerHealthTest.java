// src/test/java/com/selimhorri/app/ConfigServerHealthTest.java
package com.selimhorri.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerHealthTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void actuatorHealthEndpointShouldReturn200() {
        // Verifica que el endpoint de health responde correctamente
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void configServerEndpointsShouldBeAccessible() {
        // Verifica que los endpoints del Config Server están accesibles
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/info", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}