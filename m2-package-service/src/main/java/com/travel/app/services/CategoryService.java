package com.travel.app.services;

import com.travel.app.entities.CategoryEntity;
import com.travel.app.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    // Obtener todas las categorías
    public List<CategoryEntity> getCategories() {
        return categoryRepository.findAll();
    }

    // Obtener solo categorías activas (active = 1)
    public List<CategoryEntity> getCategoriesActive() {
        return categoryRepository.findByActive(1);
    }

    // Guardar una categoría
    public CategoryEntity saveCategory(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    // Obtener categoría por ID (solo si está activa)
    public CategoryEntity getCategoryById(Long id) {
        CategoryEntity category = categoryRepository.findById(id).orElse(null);
        if (category != null /*&& category.getActive() == 1*/) {
            return category;
        }
        return null;
    }

    // Actualizar categoría
    public CategoryEntity updateCategory(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    // Borrado lógico: Desactivar categoría (active = 0)
    public boolean deleteCategory(Long id) throws Exception {
        try {
            CategoryEntity category = categoryRepository.findById(id).orElse(null);
            if (category != null) {
                category.setActive(0);
                categoryRepository.save(category);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al desactivar la categoría: " + e.getMessage());
        }
    }
}