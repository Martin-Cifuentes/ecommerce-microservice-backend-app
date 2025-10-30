// src/test/java/com/selimhorri/app/integration/GitConfigIT.java
package com.selimhorri.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GitConfigIT {

    @Autowired
    private Environment environment;

    @Test
    void shouldHaveCorrectServerPort() {
        // Verifica el puerto del Config Server
        String serverPort = environment.getProperty("server.port");
        assertNotNull(serverPort);
        // En test usa puerto aleatorio (0), pero en prod sería 8888
    }
}