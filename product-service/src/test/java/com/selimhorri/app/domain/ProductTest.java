// src/test/java/com/selimhorri/app/domain/ProductTest.java
package com.selimhorri.app.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithValidData() {
        // Arrange
        Category category = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .build();
        
        // Act
        Product product = Product.builder()
                .productId(1)
                .productTitle("Smartphone")
                .imageUrl("smartphone.jpg")
                .sku("SMART-001")
                .priceUnit(599.99)
                .quantity(50)
                .category(category)
                .build();
        
        // Assert
        assertNotNull(product);
        assertEquals(1, product.getProductId());
        assertEquals("Smartphone", product.getProductTitle());
        assertEquals("smartphone.jpg", product.getImageUrl());
        assertEquals("SMART-001", product.getSku());
        assertEquals(599.99, product.getPriceUnit());
        assertEquals(50, product.getQuantity());
        assertEquals(category, product.getCategory());
    }

    @Test
    void shouldCreateProductWithNoArgsConstructor() {
        // Arrange & Act
        Product product = new Product();
        product.setProductId(2);
        product.setProductTitle("Laptop");
        product.setSku("LAP-002");
        product.setPriceUnit(1299.99);
        product.setQuantity(25);
        
        // Assert
        assertNotNull(product);
        assertEquals(2, product.getProductId());
        assertEquals("Laptop", product.getProductTitle());
        assertEquals("LAP-002", product.getSku());
        assertEquals(1299.99, product.getPriceUnit());
        assertEquals(25, product.getQuantity());
        assertNull(product.getCategory()); // Category no establecido
    }
}