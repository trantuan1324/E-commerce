package com.rabbyte.sbecom.controllers;

import com.rabbyte.sbecom.dtos.CategoryRequestDTO;
import com.rabbyte.sbecom.dtos.CategoryResponseDTO;
import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.services.CategoryService;
import com.rabbyte.sbecom.utils.constants.AppConstant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponseDTO> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_BY_CATEGORY_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstant.SORT_DIR, required = false) String sortDir
    ) {
        CategoryResponseDTO categoryResponseDTO = this.categoryService.handleGetAllCategories(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(categoryResponseDTO);
    }

    @GetMapping("/api/public/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = this.categoryService.handleGetCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(category);
    }

    @PostMapping("/api/public/categories")
    public ResponseEntity<CategoryRequestDTO> createCategory(@Valid @RequestBody CategoryRequestDTO reqCategory) {
        var resCategory = this.categoryService.handleCreateCategory(reqCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCategory);
    }

    @PutMapping("/api/public/categories/{categoryId}")
    public ResponseEntity<CategoryRequestDTO> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody CategoryRequestDTO reqCategory
    ) {
        CategoryRequestDTO categoryRequestDTO = this.categoryService.handleUpdateCategory(categoryId, reqCategory);
        return new ResponseEntity<>(categoryRequestDTO, HttpStatus.OK);
    }

    @DeleteMapping("api/admin/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        this.categoryService.handleDeleteCategory(categoryId);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

    
}
