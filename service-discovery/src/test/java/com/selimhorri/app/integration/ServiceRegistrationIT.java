// src/test/java/com/selimhorri/app/integration/ServiceRegistrationIT.java
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
class ServiceRegistrationIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldAccessEurekaAppsEndpoint() {
        // Verifica que el endpoint de aplicaciones registradas funciona
        ResponseEntity<String> response = restTemplate.getForEntity("/eureka/apps", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHaveEmptyRegistryOnStartup() {
        // En un entorno de test, el registry debería estar vacío inicialmente
        ResponseEntity<String> response = restTemplate.getForEntity("/eureka/apps", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // El cuerpo puede estar vacío o contener solo el servidor Eureka mismo
    }
}