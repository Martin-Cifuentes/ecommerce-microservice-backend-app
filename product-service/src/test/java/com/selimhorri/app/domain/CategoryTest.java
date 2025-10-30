// src/test/java/com/selimhorri/app/domain/CategoryTest.java
package com.selimhorri.app.domain;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void shouldCreateCategoryWithValidData() {
        // Arrange & Act
        Category category = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .subCategories(new HashSet<>())
                .products(new HashSet<>())
                .build();
        
        // Assert
        assertNotNull(category);
        assertEquals(1, category.getCategoryId());
        assertEquals("Electronics", category.getCategoryTitle());
        assertEquals("electronics.jpg", category.getImageUrl());
        assertNotNull(category.getSubCategories());
        assertTrue(category.getSubCategories().isEmpty());
        assertNotNull(category.getProducts());
        assertTrue(category.getProducts().isEmpty());
        assertNull(category.getParentCategory());
    }

    @Test
    void shouldCreateCategoryHierarchy() {
        // Arrange
        Category parentCategory = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .build();
        
        Category subCategory = Category.builder()
                .categoryId(2)
                .categoryTitle("Smartphones")
                .parentCategory(parentCategory)
                .build();
        
        Set<Category> subCategories = new HashSet<>();
        subCategories.add(subCategory);
        parentCategory.setSubCategories(subCategories);
        
        // Assert
        assertNotNull(parentCategory.getSubCategories());
        assertEquals(1, parentCategory.getSubCategories().size());
        assertEquals(parentCategory, subCategory.getParentCategory());
        assertTrue(parentCategory.getSubCategories().contains(subCategory));
    }

    @Test
    void shouldAddProductToCategory() {
        // Arrange
        Category category = Category.builder()
                .categoryId(1)
                .categoryTitle("Books")
                .products(new HashSet<>())
                .build();
        
        Product product = Product.builder()
                .productId(1)
                .productTitle("Spring Boot Guide")
                .sku("BOOK-001")
                .priceUnit(29.99)
                .category(category)
                .build();
        
        // Act
        category.getProducts().add(product);
        
        // Assert
        assertEquals(1, category.getProducts().size());
        assertTrue(category.getProducts().contains(product));
        assertEquals(category, product.getCategory());
    }
}