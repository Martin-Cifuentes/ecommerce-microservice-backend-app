// src/test/java/com/selimhorri/app/integration/CategoryControllerIT.java
package com.selimhorri.app.integration;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import javax.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CategoryControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldReturnAllCategories() {
        // Arrange
        categoryRepository.save(Category.builder().categoryTitle("Category 1").build());
        categoryRepository.save(Category.builder().categoryTitle("Category 2").build());
        
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/categories", String.class);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}