package com.selimhorri.app.e2e;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.net.URL;

@Testcontainers
class MinimalComposeTest {

    private static File getComposeFile() {
        try {
            URL resource = MinimalComposeTest.class.getClassLoader()
                .getResource("minimal-compose.yml");
            return new File(resource.toURI());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Container
    private static final DockerComposeContainer<?> compose = 
        new DockerComposeContainer<>(getComposeFile());

    @Test
    void testComposeStarts() {
        System.out.println("=== MINIMAL COMPOSE TEST ===");
        System.out.println("✓ Docker Compose iniciado correctamente");
        
        // Simplemente verificar que no hay excepciones
        // Si llegamos aquí, significa que Docker Compose funcionó
        System.out.println("✓ Test completado sin errores");
    }
}