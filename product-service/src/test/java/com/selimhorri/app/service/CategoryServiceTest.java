// src/test/java/com/selimhorri/app/service/CategoryServiceTest.java
package com.selimhorri.app.service;

import com.selimhorri.app.dto.CategoryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryService categoryService;

    @Test
    void shouldFindCategoryById() {
        // Arrange
        Integer categoryId = 1;
        CategoryDto expectedCategory = CategoryDto.builder()
                .categoryId(categoryId)
                .categoryTitle("Home Appliances")
                .imageUrl("home.jpg")
                .build();
        
        when(categoryService.findById(categoryId)).thenReturn(expectedCategory);
        
        // Act
        CategoryDto result = categoryService.findById(categoryId);
        
        // Assert
        assertNotNull(result);
        assertEquals(categoryId, result.getCategoryId());
        assertEquals("Home Appliances", result.getCategoryTitle());
        assertEquals("home.jpg", result.getImageUrl());
    }

    @Test
    void shouldFindAllCategories() {
        // Arrange
        CategoryDto category1 = CategoryDto.builder().categoryId(1).categoryTitle("Category 1").build();
        CategoryDto category2 = CategoryDto.builder().categoryId(2).categoryTitle("Category 2").build();
        List<CategoryDto> expectedCategories = Arrays.asList(category1, category2);
        
        when(categoryService.findAll()).thenReturn(expectedCategories);
        
        // Act
        List<CategoryDto> result = categoryService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getCategoryTitle());
        assertEquals("Category 2", result.get(1).getCategoryTitle());
    }
}