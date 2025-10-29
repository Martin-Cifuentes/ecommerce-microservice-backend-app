// src/test/java/com/selimhorri/app/EurekaServerConfigTest.java
package com.selimhorri.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EurekaServerConfigTest {

    @Autowired
    private Environment environment;

    @Test
    void shouldHaveCorrectServerPort() {
        // Verifica que el puerto de Eureka es el correcto
        assertEquals("8761", environment.getProperty("server.port"));
    }

    @Test
    void shouldNotRegisterWithItself() {
        // Verifica que Eureka no se registra a sí mismo
        assertEquals("false", environment.getProperty("eureka.client.register-with-eureka"));
        assertEquals("false", environment.getProperty("eureka.client.fetch-registry"));
    }
}