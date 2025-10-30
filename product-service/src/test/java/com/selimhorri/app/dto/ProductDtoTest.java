// src/test/java/com/selimhorri/app/dto/ProductDtoTest.java
package com.selimhorri.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductDtoTest {

    @Test
    void shouldCreateProductDtoWithBuilder() {
        // Arrange
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .build();
        
        // Act
        ProductDto productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Wireless Headphones")
                .imageUrl("headphones.jpg")
                .sku("HEAD-001")
                .priceUnit(89.99)
                .quantity(100)
                .categoryDto(categoryDto)
                .build();
        
        // Assert
        assertNotNull(productDto);
        assertEquals(1, productDto.getProductId());
        assertEquals("Wireless Headphones", productDto.getProductTitle());
        assertEquals("headphones.jpg", productDto.getImageUrl());
        assertEquals("HEAD-001", productDto.getSku());
        assertEquals(89.99, productDto.getPriceUnit());
        assertEquals(100, productDto.getQuantity());
        assertNotNull(productDto.getCategoryDto());
        assertEquals("Electronics", productDto.getCategoryDto().getCategoryTitle());
    }

    @Test
    void shouldCreateProductDtoWithSetters() {
        // Arrange
        ProductDto productDto = new ProductDto();
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(2);
        categoryDto.setCategoryTitle("Books");
        
        // Act
        productDto.setProductId(2);
        productDto.setProductTitle("Java Programming");
        productDto.setSku("BOOK-002");
        productDto.setPriceUnit(39.99);
        productDto.setQuantity(75);
        productDto.setCategoryDto(categoryDto);
        
        // Assert
        assertEquals(2, productDto.getProductId());
        assertEquals("Java Programming", productDto.getProductTitle());
        assertEquals("BOOK-002", productDto.getSku());
        assertEquals(39.99, productDto.getPriceUnit());
        assertEquals(75, productDto.getQuantity());
        assertNotNull(productDto.getCategoryDto());
        assertEquals("Books", productDto.getCategoryDto().getCategoryTitle());
    }
}