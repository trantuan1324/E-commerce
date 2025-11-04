package com.rabbyte.sbecom.controllers;

import com.rabbyte.sbecom.dtos.CategoryResponse;
import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories() {
        CategoryResponse categories = this.categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/api/public/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = this.categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(category);
    }

    @PostMapping("/api/public/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        var resCategory = this.categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCategory);
    }

    @PutMapping("/api/public/categories/{categoryId}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody Category category
    ) {
        Category resCategory = this.categoryService.updateCategory(categoryId, category);
        return new ResponseEntity<>(resCategory, HttpStatus.OK);
    }

    @DeleteMapping("api/admin/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        this.categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
