package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.CategoryDTO;
import com.rabbyte.sbecom.dtos.CategoryResponse;
import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.repositories.CategoryRepository;
import com.rabbyte.sbecom.utils.exceptions.ApiException;
import com.rabbyte.sbecom.utils.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse getAllCategories() {
        List<Category> categories = this.categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new ApiException("No categories found!!!");
        }

        List<CategoryDTO> categoryDTOS = categories.stream().map(category ->
            this.modelMapper.map(category, CategoryDTO.class)
        ).toList();

        return new CategoryResponse(categoryDTOS);
    }

    @Override
    public Category createCategory(Category reqCategory) {
        boolean isCategoryExist = this.categoryRepository.existsCategoryByCategoryName(reqCategory.getCategoryName());
        if (isCategoryExist) {
            throw new ApiException("Category already exist!!!");
        }
        return this.categoryRepository.save(reqCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category findingResult = this.getCategoryById(categoryId);
        this.categoryRepository.delete(findingResult);
    }

    @Override
    public Category updateCategory(Long id, Category reqCategory) {
        Category findingResult = this.getCategoryById(id);
        boolean isCategoryExist = this.categoryRepository.existsCategoryByCategoryName(reqCategory.getCategoryName());
        if (isCategoryExist) {
            throw new ApiException("Category already exist!!!");
        }
        findingResult.setCategoryName(reqCategory.getCategoryName());
        return this.categoryRepository.save(findingResult);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return this.categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("category", "categoryId", categoryId)
        );
    }

}
