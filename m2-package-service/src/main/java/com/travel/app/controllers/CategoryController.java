package com.travel.app.controllers;

import com.travel.app.entities.CategoryEntity;
import com.travel.app.entities.SeasonEntity;
import com.travel.app.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<List<CategoryEntity>> listCategories() {
        List<CategoryEntity> categories = categoryService.getCategories();
        return ResponseEntity.ok(categories);
    }


    @GetMapping("/active")
    public ResponseEntity<List<CategoryEntity>> listSeasonsActive() {
        List<CategoryEntity> categories = categoryService.getCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryEntity> getCategoryById(@PathVariable Long id) {
        CategoryEntity category = categoryService.getCategoryById(id);
        return category != null ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<CategoryEntity> saveCategory(@RequestBody CategoryEntity category) {
        CategoryEntity newCategory = categoryService.saveCategory(category);
        return ResponseEntity.ok(newCategory);
    }

    @PutMapping("/")
    public ResponseEntity<CategoryEntity> updateCategory(@RequestBody CategoryEntity category) {
        CategoryEntity updatedCategory = categoryService.updateCategory(category);
        return ResponseEntity.ok(updatedCategory);
    }

}