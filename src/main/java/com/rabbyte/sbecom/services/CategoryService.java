package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.CategoryDTO;
import com.rabbyte.sbecom.dtos.CategoryResponse;
import com.rabbyte.sbecom.entities.Category;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer size, String sortBy, String sortDir);
    CategoryDTO createCategory(CategoryDTO reqCategory);
    void deleteCategory(Long categoryId);
    CategoryDTO updateCategory(Long id, CategoryDTO category);
    Category getCategoryById(Long categoryId);
}
