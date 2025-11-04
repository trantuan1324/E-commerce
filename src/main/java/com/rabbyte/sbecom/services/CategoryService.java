package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.CategoryResponse;
import com.rabbyte.sbecom.entities.Category;
import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories();
    Category createCategory(Category reqCategory);
    void deleteCategory(Long categoryId);
    Category updateCategory(Long id, Category category);
    Category getCategoryById(Long categoryId);
}
