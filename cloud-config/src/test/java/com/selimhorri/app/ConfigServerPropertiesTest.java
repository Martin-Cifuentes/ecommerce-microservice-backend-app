// src/test/java/com/selimhorri/app/ConfigServerPropertiesTest.java
package com.selimhorri.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ConfigServerPropertiesTest {

    @Autowired
    private Environment environment;

    @Test
    void shouldHaveCorrectServerPort() {
        // Verifica que el puerto del Config Server es el correcto
        assertEquals("9296", environment.getProperty("server.port"));
    }

    @Test
    void shouldHaveGitConfiguration() {
        // Verifica que hay configuración de Git para el Config Server
        String gitUri = environment.getProperty("spring.cloud.config.server.git.uri");
        assertNotNull(gitUri, "Debe tener configuración de Git URI");
    }
}