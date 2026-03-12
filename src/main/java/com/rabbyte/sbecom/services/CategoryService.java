package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.CategoryRequestDTO;
import com.rabbyte.sbecom.dtos.CategoryResponseDTO;
import com.rabbyte.sbecom.entities.Category;

public interface CategoryService {
    CategoryResponseDTO handleGetAllCategories(Integer pageNumber, Integer size, String sortBy, String sortDir);
    CategoryRequestDTO handleCreateCategory(CategoryRequestDTO reqCategory);
    void handleDeleteCategory(Long categoryId);
    CategoryRequestDTO handleUpdateCategory(Long id, CategoryRequestDTO category);
    Category handleGetCategoryById(Long categoryId);
}
