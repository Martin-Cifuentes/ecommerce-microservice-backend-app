// src/test/java/com/selimhorri/app/integration/CategoryRepositoryIT.java
package com.selimhorri.app.integration;

import com.selimhorri.app.domain.Category;
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
class CategoryRepositoryIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldSaveCategory() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Home Appliances")
                .imageUrl("home-appliances.jpg")
                .build();
        
        // Act
        Category savedCategory = categoryRepository.save(category);
        
        // Assert
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Home Appliances", savedCategory.getCategoryTitle());
        assertEquals("home-appliances.jpg", savedCategory.getImageUrl());
    }

    @Test
    void shouldSaveCategoryHierarchy() {
        // Arrange
        Category parentCategory = categoryRepository.save(Category.builder()
                .categoryTitle("Electronics")
                .build());
        
        Category subCategory = Category.builder()
                .categoryTitle("Smartphones")
                .parentCategory(parentCategory)
                .build();
        
        // Act
        Category savedSubCategory = categoryRepository.save(subCategory);
        
        // Assert
        assertNotNull(savedSubCategory);
        assertEquals("Smartphones", savedSubCategory.getCategoryTitle());
        assertEquals(parentCategory, savedSubCategory.getParentCategory());
    }

    @Test
    void shouldFindCategoryById() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Fashion")
                .imageUrl("fashion.jpg")
                .build());
        
        Integer categoryId = category.getCategoryId();
        
        // Act
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        
        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals(categoryId, foundCategory.get().getCategoryId());
        assertEquals("Fashion", foundCategory.get().getCategoryTitle());
        assertEquals("fashion.jpg", foundCategory.get().getImageUrl());
    }

    @Test
    void shouldFindAllCategories() {
        // Arrange
        categoryRepository.save(Category.builder().categoryTitle("Category 1").build());
        categoryRepository.save(Category.builder().categoryTitle("Category 2").build());
        categoryRepository.save(Category.builder().categoryTitle("Category 3").build());
        
        // Act
        List<Category> categories = categoryRepository.findAll();
        
        // Assert
        assertNotNull(categories);
        assertTrue(categories.size() >= 3);
    }

    @Test
    void shouldUpdateCategory() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Original Category")
                .imageUrl("original.jpg")
                .build());
        
        // Act - Actualizar la categoría
        category.setCategoryTitle("Updated Category");
        category.setImageUrl("updated.jpg");
        Category updatedCategory = categoryRepository.save(category);
        
        // Assert
        assertEquals("Updated Category", updatedCategory.getCategoryTitle());
        assertEquals("updated.jpg", updatedCategory.getImageUrl());
    }

    @Test
    void shouldDeleteCategory() {
        // Arrange
        Category category = categoryRepository.save(Category.builder()
                .categoryTitle("Category to Delete")
                .build());
        
        Integer categoryId = category.getCategoryId();
        
        // Verificar que existe antes de eliminar
        assertTrue(categoryRepository.findById(categoryId).isPresent());
        
        // Act
        categoryRepository.deleteById(categoryId);
        
        // Assert
        Optional<Category> deletedCategory = categoryRepository.findById(categoryId);
        assertFalse(deletedCategory.isPresent());
    }

    @Test
    void shouldFindSubCategoriesUsingStream() {
        // Arrange
        Category parent = categoryRepository.save(Category.builder()
                .categoryTitle("Parent Category")
                .build());
        
        categoryRepository.save(Category.builder()
                .categoryTitle("Child Category 1")
                .parentCategory(parent)
                .build());
        
        categoryRepository.save(Category.builder()
                .categoryTitle("Child Category 2")
                .parentCategory(parent)
                .build());
        
        categoryRepository.save(Category.builder()
                .categoryTitle("Independent Category")
                .build()); // Sin parent
        
        // Act - Filtrar subcategorías usando stream
        List<Category> allCategories = categoryRepository.findAll();
        List<Category> childCategories = allCategories.stream()
                .filter(category -> parent.equals(category.getParentCategory()))
                .collect(Collectors.toList());  // ← CORREGIDO
        
        // Assert
        assertNotNull(childCategories);
        assertEquals(2, childCategories.size());
        assertTrue(childCategories.stream()
                .allMatch(category -> parent.equals(category.getParentCategory())));
    }
}