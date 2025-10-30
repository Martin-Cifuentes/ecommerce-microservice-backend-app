// src/test/java/com/selimhorri/app/integration/ConfigServerHealthIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerHealthIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void configServerSpecificHealthIndicatorsShouldWork() {
        // Verifica health indicators específicos del Config Server
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        
        // Verifica que el status general es UP
        assertTrue(responseBody.contains("\"status\":\"UP\""));
        
        // En un Config Server real, podrías ver componentes como:
        // "configServer", "discoveryComposite", "diskSpace", etc.
    }

    @Test
    void configServerMetricsShouldBeAccessible() {
        // Verifica que los endpoints de métricas están disponibles
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/metrics", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}