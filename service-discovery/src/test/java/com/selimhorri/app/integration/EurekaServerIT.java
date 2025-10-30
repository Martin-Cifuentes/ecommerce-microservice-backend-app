// src/test/java/com/selimhorri/app/integration/EurekaServerIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EurekaServerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void eurekaDashboardShouldBeAccessible() {
        // Verifica que el dashboard de Eureka responde
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void eurekaApiEndpointsShouldBeAvailable() {
        // Verifica endpoints de la API de Eureka
        ResponseEntity<String> response = restTemplate.getForEntity("/eureka/apps", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}