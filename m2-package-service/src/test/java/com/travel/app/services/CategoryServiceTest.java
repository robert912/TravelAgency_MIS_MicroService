package com.travel.app.services;

import com.travel.app.entities.CategoryEntity;
import com.travel.app.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryEntity category;

    @BeforeEach
    void setUp() {
        category = new CategoryEntity();
        category.setId(1L);
        category.setName("Tours");
        category.setDescription("Guided tours");
        category.setActive(1);
    }

    @Test
    void getCategories_ShouldReturnListOfCategories() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));

        List<CategoryEntity> result = categoryService.getCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoriesActive_ShouldReturnOnlyActiveCategories() {
        when(categoryRepository.findByActive(1)).thenReturn(Arrays.asList(category));

        List<CategoryEntity> result = categoryService.getCategoriesActive();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getActive());
        verify(categoryRepository, times(1)).findByActive(1);
    }

    @Test
    void saveCategory_ShouldReturnSavedCategory() {
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(category);

        CategoryEntity result = categoryService.saveCategory(category);

        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void getCategoryById_WhenExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryEntity result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_WhenDoesNotExist_ShouldReturnNull() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        CategoryEntity result = categoryService.getCategoryById(2L);

        assertNull(result);
        verify(categoryRepository, times(1)).findById(2L);
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(category);

        CategoryEntity result = categoryService.updateCategory(category);

        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void deleteCategory_WhenExists_ShouldSetInactiveAndReturnTrue() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(category);

        boolean result = categoryService.deleteCategory(1L);

        assertTrue(result);
        assertEquals(0, category.getActive());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void deleteCategory_WhenDoesNotExist_ShouldReturnFalse() throws Exception {
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        boolean result = categoryService.deleteCategory(2L);

        assertFalse(result);
        verify(categoryRepository, times(1)).findById(2L);
        verify(categoryRepository, never()).save(any());
    }
}
