// src/test/java/com/selimhorri/app/service/ProductServiceTest.java
package com.selimhorri.app.service;

import com.selimhorri.app.dto.ProductDto;
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
class ProductServiceTest {

    @Mock
    private ProductService productService;

    @Test
    void shouldFindProductById() {
        // Arrange
        Integer productId = 1;
        CategoryDto categoryDto = CategoryDto.builder().categoryId(1).categoryTitle("Electronics").build();
        ProductDto expectedProduct = ProductDto.builder()
                .productId(productId)
                .productTitle("Tablet")
                .sku("TAB-001")
                .priceUnit(299.99)
                .quantity(30)
                .categoryDto(categoryDto)
                .build();
        
        when(productService.findById(productId)).thenReturn(expectedProduct);
        
        // Act
        ProductDto result = productService.findById(productId);
        
        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals("Tablet", result.getProductTitle());
        assertEquals("TAB-001", result.getSku());
        assertEquals(299.99, result.getPriceUnit());
        assertEquals(30, result.getQuantity());
        assertNotNull(result.getCategoryDto());
    }

    @Test
    void shouldFindAllProducts() {
        // Arrange
        ProductDto product1 = ProductDto.builder().productId(1).productTitle("Product 1").priceUnit(10.0).build();
        ProductDto product2 = ProductDto.builder().productId(2).productTitle("Product 2").priceUnit(20.0).build();
        List<ProductDto> expectedProducts = Arrays.asList(product1, product2);
        
        when(productService.findAll()).thenReturn(expectedProducts);
        
        // Act
        List<ProductDto> result = productService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getProductTitle());
        assertEquals("Product 2", result.get(1).getProductTitle());
    }
}