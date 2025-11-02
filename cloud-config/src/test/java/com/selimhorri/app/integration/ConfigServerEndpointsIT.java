// src/test/java/com/selimhorri/app/integration/ConfigServerEndpointsIT.java
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
class ConfigServerEndpointsIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void configServerHealthEndpointShouldBeAccessible() {
        // Verifica el health endpoint del Config Server - ESTE SIEMPRE DEBE FUNCIONAR
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"status\":\"UP\""));
    }

    @Test
    void configServerInfoEndpointShouldBeAccessible() {
        // Verifica el info endpoint
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/info", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void configServerActuatorEndpointsShouldBeAvailable() {
        // Verifica que los endpoints de actuator están disponibles
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void configServerApplicationEndpointShouldRespond() {
        // Intenta acceder a un endpoint de configuración
        // Puede devolver 404 si no hay configuración, pero eso es válido
        ResponseEntity<String> response = restTemplate.getForEntity("/application/default", String.class);
        
        assertNotNull(response);
        // Aceptamos 200 (config encontrada) o 404 (no encontrada) - ambos indican que el servidor funciona
        assertTrue(response.getStatusCode() == HttpStatus.OK || 
                  response.getStatusCode() == HttpStatus.NOT_FOUND,
                  "El servidor debería responder incluso si no encuentra configuración");
    }
}