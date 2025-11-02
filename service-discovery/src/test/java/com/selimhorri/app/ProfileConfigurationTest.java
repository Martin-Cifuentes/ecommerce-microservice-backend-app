// src/test/java/com/selimhorri/app/ProfileConfigurationTest.java
package com.selimhorri.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("dev")
class DevProfileConfigurationTest {

    @Test
    void shouldLoadDevProfileSuccessfully() {
        // Verifica que el perfil dev se carga sin errores
        assertTrue(true, "El perfil de desarrollo debería cargarse correctamente");
    }
}

@SpringBootTest
@ActiveProfiles("prod")
class ProdProfileConfigurationTest {

    @Test
    void shouldLoadProdProfileSuccessfully() {
        // Verifica que el perfil production se carga sin errores
        assertTrue(true, "El perfil de producción debería cargarse correctamente");
    }
}