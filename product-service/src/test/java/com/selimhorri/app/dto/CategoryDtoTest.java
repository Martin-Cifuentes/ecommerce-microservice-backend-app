// src/test/java/com/selimhorri/app/dto/CategoryDtoTest.java
package com.selimhorri.app.dto;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class CategoryDtoTest {

    @Test
    void shouldCreateCategoryDtoWithBuilder() {
        // Arrange
        Set<CategoryDto> subCategoriesDtos = new HashSet<>();
        subCategoriesDtos.add(CategoryDto.builder().categoryId(2).categoryTitle("Sub Category").build());
        
        Set<ProductDto> productDtos = new HashSet<>();
        productDtos.add(ProductDto.builder().productId(1).productTitle("Test Product").build());
        
        CategoryDto parentCategoryDto = CategoryDto.builder()
                .categoryId(0)
                .categoryTitle("Parent Category")
                .build();
        
        // Act
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Main Category")
                .imageUrl("category.jpg")
                .subCategoriesDtos(subCategoriesDtos)
                .parentCategoryDto(parentCategoryDto)
                .productDtos(productDtos)
                .build();
        
        // Assert
        assertNotNull(categoryDto);
        assertEquals(1, categoryDto.getCategoryId());
        assertEquals("Main Category", categoryDto.getCategoryTitle());
        assertEquals("category.jpg", categoryDto.getImageUrl());
        assertNotNull(categoryDto.getSubCategoriesDtos());
        assertEquals(1, categoryDto.getSubCategoriesDtos().size());
        assertNotNull(categoryDto.getParentCategoryDto());
        assertEquals("Parent Category", categoryDto.getParentCategoryDto().getCategoryTitle());
        assertNotNull(categoryDto.getProductDtos());
        assertEquals(1, categoryDto.getProductDtos().size());
    }

    @Test
    void shouldCreateCategoryDtoWithSetters() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        Set<CategoryDto> subCategoriesDtos = new HashSet<>();
        subCategoriesDtos.add(new CategoryDto());
        
        Set<ProductDto> productDtos = new HashSet<>();
        productDtos.add(new ProductDto());
        
        CategoryDto parentCategoryDto = new CategoryDto();
        parentCategoryDto.setCategoryTitle("Parent");
        
        // Act
        categoryDto.setCategoryId(3);
        categoryDto.setCategoryTitle("Test Category");
        categoryDto.setImageUrl("test.jpg");
        categoryDto.setSubCategoriesDtos(subCategoriesDtos);
        categoryDto.setParentCategoryDto(parentCategoryDto);
        categoryDto.setProductDtos(productDtos);
        
        // Assert
        assertEquals(3, categoryDto.getCategoryId());
        assertEquals("Test Category", categoryDto.getCategoryTitle());
        assertEquals("test.jpg", categoryDto.getImageUrl());
        assertNotNull(categoryDto.getSubCategoriesDtos());
        assertEquals(1, categoryDto.getSubCategoriesDtos().size());
        assertNotNull(categoryDto.getParentCategoryDto());
        assertEquals("Parent", categoryDto.getParentCategoryDto().getCategoryTitle());
        assertNotNull(categoryDto.getProductDtos());
        assertEquals(1, categoryDto.getProductDtos().size());
    }
}