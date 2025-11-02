// src/test/java/com/selimhorri/app/integration/ProductRepositoryIT.java
package com.selimhorri.app.integration;

import com.selimhorri.app.domain.Product;
import com.selimhorri.app.domain.Category;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldSaveProduct() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .build());
        
        Product product = Product.builder()
                .productTitle("Smartphone")
                .imageUrl("smartphone.jpg")
                .sku("SMART-001")
                .priceUnit(599.99)
                .quantity(50)
                .category(category)
                .build();
        
        // Act
        Product savedProduct = productRepository.save(product);
        
        // Assert
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getProductId());
        assertEquals("Smartphone", savedProduct.getProductTitle());
        assertEquals("smartphone.jpg", savedProduct.getImageUrl());
        assertEquals("SMART-001", savedProduct.getSku());
        assertEquals(599.99, savedProduct.getPriceUnit());
        assertEquals(50, savedProduct.getQuantity());
        assertEquals(category, savedProduct.getCategory());
    }

    @Test
    void shouldFindProductById() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Books")
                .build());
        
        Product product = productRepository.save(Product.builder()
                .productTitle("Java Programming")
                .sku("BOOK-001")
                .priceUnit(39.99)
                .quantity(100)
                .category(category)
                .build());
        
        Integer productId = product.getProductId();
        
        // Act
        Optional<Product> foundProduct = productRepository.findById(productId);
        
        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(productId, foundProduct.get().getProductId());
        assertEquals("Java Programming", foundProduct.get().getProductTitle());
        assertEquals("BOOK-001", foundProduct.get().getSku());
        assertEquals(39.99, foundProduct.get().getPriceUnit());
    }

    @Test
    void shouldFindAllProducts() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Test Category")
                .build());
        
        productRepository.save(Product.builder()
                .productTitle("Product 1")
                .sku("PROD-001")
                .priceUnit(10.0)
                .quantity(5)
                .category(category)
                .build());
        
        productRepository.save(Product.builder()
                .productTitle("Product 2")
                .sku("PROD-002")
                .priceUnit(20.0)
                .quantity(10)
                .category(category)
                .build());
        
        // Act
        List<Product> products = productRepository.findAll();
        
        // Assert
        assertNotNull(products);
        assertTrue(products.size() >= 2);
    }

    @Test
    void shouldUpdateProduct() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Update Category")
                .build());
        
        Product product = productRepository.save(Product.builder()
                .productTitle("Original Product")
                .sku("ORIG-001")
                .priceUnit(25.0)
                .quantity(10)
                .category(category)
                .build());
        
        // Act - Actualizar el producto
        product.setProductTitle("Updated Product");
        product.setPriceUnit(35.0);
        product.setQuantity(20);
        Product updatedProduct = productRepository.save(product);
        
        // Assert
        assertEquals("Updated Product", updatedProduct.getProductTitle());
        assertEquals(35.0, updatedProduct.getPriceUnit());
        assertEquals(20, updatedProduct.getQuantity());
    }

    @Test
    void shouldDeleteProduct() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Delete Category")
                .build());
        
        Product product = productRepository.save(Product.builder()
                .productTitle("Product to Delete")
                .sku("DEL-001")
                .priceUnit(15.0)
                .quantity(5)
                .category(category)
                .build());
        
        Integer productId = product.getProductId();
        
        // Verificar que existe antes de eliminar
        assertTrue(productRepository.findById(productId).isPresent());
        
        // Act
        productRepository.deleteById(productId);
        
        // Assert
        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void shouldFindProductsByCategoryUsingStream() {
        // Arrange
        Category electronics = categoryRepository.save(Category.builder()
                .categoryTitle("Electronics")
                .build());
        
        Category books = categoryRepository.save(Category.builder()
                .categoryTitle("Books")
                .build());
        
        productRepository.save(Product.builder()
                .productTitle("Laptop")
                .sku("LAP-001")
                .priceUnit(999.99)
                .quantity(10)
                .category(electronics)
                .build());
        
        productRepository.save(Product.builder()
                .productTitle("Tablet")
                .sku("TAB-001")
                .priceUnit(299.99)
                .quantity(20)
                .category(electronics)
                .build());
        
        productRepository.save(Product.builder()
                .productTitle("Novel")
                .sku("BOOK-002")
                .priceUnit(15.99)
                .quantity(50)
                .category(books)
                .build());
        
        // Act - Filtrar por categoría usando stream
        List<Product> allProducts = productRepository.findAll();
        List<Product> electronicsProducts = allProducts.stream()
                .filter(p -> p.getCategory().equals(electronics))
                .collect(Collectors.toList());  // ← CORREGIDO
        
        // Assert
        assertNotNull(electronicsProducts);
        assertEquals(2, electronicsProducts.size());
        assertTrue(electronicsProducts.stream()
                .allMatch(product -> product.getCategory().equals(electronics)));
    }

    @Test
    void shouldFindProductBySkuUsingStream() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Test Category")
                .build());
        
        String targetSku = "TARGET-SKU-123";
        productRepository.save(Product.builder()
                .productTitle("Target Product")
                .sku(targetSku)
                .priceUnit(99.99)
                .quantity(25)
                .category(category)
                .build());
        
        productRepository.save(Product.builder()
                .productTitle("Other Product")
                .sku("OTHER-001")
                .priceUnit(50.0)
                .quantity(10)
                .category(category)
                .build());
        
        // Act - Buscar por SKU usando stream
        List<Product> allProducts = productRepository.findAll();
        Optional<Product> foundProduct = allProducts.stream()
                .filter(p -> targetSku.equals(p.getSku()))
                .findFirst();
        
        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals("Target Product", foundProduct.get().getProductTitle());
        assertEquals(targetSku, foundProduct.get().getSku());
    }
}