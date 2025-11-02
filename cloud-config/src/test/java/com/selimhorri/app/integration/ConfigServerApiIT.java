// src/test/java/com/selimhorri/app/integration/ConfigServerApiIT.java
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
class ConfigServerApiIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void configServerApplicationEndpointShouldRespond() {
        // Intenta acceder a un endpoint de configuración de aplicación
        // En un entorno real, esto buscaría configuración para una aplicación específica
        ResponseEntity<String> response = restTemplate.getForEntity("/application/default", String.class);
        
        // Puede devolver 404 si no hay configuración, pero el servidor debe responder
        assertNotNull(response);
        // Aceptamos 200 (config encontrada) o 404 (no encontrada) pero el servidor funciona
        assertTrue(response.getStatusCode() == HttpStatus.OK || 
                  response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    void configServerEnvironmentEndpointShouldBeAvailable() {
        // Verifica el endpoint de environment
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/env", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}