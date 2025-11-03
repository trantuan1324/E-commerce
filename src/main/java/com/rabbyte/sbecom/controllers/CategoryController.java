package com.rabbyte.sbecom.controllers;

import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.services.CategoryService;
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
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = this.categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @PostMapping("/api/public/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        var resCategory = this.categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCategory);
    }

    @PutMapping("/api/public/categories/{categoryId}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody Category category
    ) {
        try {
            Category resCategory = this.categoryService.updateCategory(categoryId, category);
            return new ResponseEntity<>(resCategory, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    @DeleteMapping("api/admin/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        try {
            this.categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }
}
