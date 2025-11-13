package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.CategoryDTO;
import com.rabbyte.sbecom.dtos.CategoryResponse;
import com.rabbyte.sbecom.entities.Category;

public interface CategoryService {
    CategoryResponse handleGetAllCategories(Integer pageNumber, Integer size, String sortBy, String sortDir);
    CategoryDTO handleCreateCategory(CategoryDTO reqCategory);
    void handleDeleteCategory(Long categoryId);
    CategoryDTO handleUpdateCategory(Long id, CategoryDTO category);
    Category handleGetCategoryById(Long categoryId);
}
