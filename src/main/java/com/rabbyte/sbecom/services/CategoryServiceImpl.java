package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.repositories.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{


    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category reqCategory) {
        return this.categoryRepository.save(reqCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        this.categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category updateCategory(Long id, Category reqCategory) {
        Category findingResult = this.getCategoryById(id);
        findingResult.setCategoryName(reqCategory.getCategoryName());
        return this.categoryRepository.save(findingResult);
    }

    public Category getCategoryById(Long categoryId) {
        return this.categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }


}
