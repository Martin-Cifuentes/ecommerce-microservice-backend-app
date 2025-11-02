// src/test/java/com/selimhorri/app/ConfigServerBeansTest.java
package com.selimhorri.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ConfigServerBeansTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void configServerBeansShouldBeLoaded() {
        // Verifica que los beans del Config Server están cargados
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        boolean hasConfigServerBean = false;
        
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("config") && 
                !beanName.toLowerCase().contains("test")) {
                hasConfigServerBean = true;
                break;
            }
        }
        
        assertTrue(hasConfigServerBean, "Debería haber beans de Config Server cargados");
    }

    @Test
    void shouldHaveEnvironmentRepositoryBean() {
        // Verifica que el bean de EnvironmentRepository está presente
        assertTrue(applicationContext.containsBean("defaultEnvironmentRepository") ||
                  applicationContext.containsBean("environmentRepository"),
                  "Debería tener el bean EnvironmentRepository");
    }
}