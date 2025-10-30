// src/test/java/com/selimhorri/app/integration/ConfigurationIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ConfigurationIT {

    @Autowired
    private Environment environment;

    @Test
    void shouldLoadEurekaServerConfiguration() {
        // Verifica que la configuración de Eureka Server se carga correctamente
        String port = environment.getProperty("server.port");
        String registerWithEureka = environment.getProperty("eureka.client.register-with-eureka");
        String fetchRegistry = environment.getProperty("eureka.client.fetch-registry");
        
        assertNotNull(port);
        assertEquals("false", registerWithEureka);
        assertEquals("false", fetchRegistry);
    }
}