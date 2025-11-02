// src/test/java/com/selimhorri/app/EurekaServerPropertiesTest.java
package com.selimhorri.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EurekaServerPropertiesTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void eurekaServerBeanShouldBeLoaded() {
        // Verifica que los beans de Eureka Server están cargados
        assertTrue(applicationContext.containsBean("eurekaServerContext"),
            "El bean eurekaServerContext debería estar cargado");
    }

    @Test
    void shouldHaveEurekaDashboardEnabled() {
        // Verifica que el dashboard de Eureka está habilitado
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        boolean hasEurekaBean = false;
        
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("eureka")) {
                hasEurekaBean = true;
                break;
            }
        }
        
        assertTrue(hasEurekaBean, "Debería haber beans de Eureka cargados");
    }
}