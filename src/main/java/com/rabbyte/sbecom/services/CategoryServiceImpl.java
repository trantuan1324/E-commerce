package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.CategoryDTO;
import com.rabbyte.sbecom.dtos.CategoryResponse;
import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.repositories.CategoryRepository;
import com.rabbyte.sbecom.utils.exceptions.ApiException;
import com.rabbyte.sbecom.utils.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<Category> categoriesPage = this.categoryRepository.findAll(pageable);

        List<Category> categories = categoriesPage.getContent();
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("categories");
        }

        List<CategoryDTO> categoryDTOS = categories.stream().map(category ->
            this.modelMapper.map(category, CategoryDTO.class)
        ).toList();

        CategoryResponse response = new CategoryResponse();
        response.setContent(categoryDTOS);
        response.setPageNumber(categoriesPage.getNumber() + 1);
        response.setPageSize(categoriesPage.getSize());
        response.setTotalPages(categoriesPage.getTotalPages());
        response.setTotalElements(categoriesPage.getTotalElements());
        response.setLastPage(categoriesPage.isLast());

        return response;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO reqCategory) {
        boolean isCategoryExist = this.categoryRepository.existsCategoryByCategoryName(reqCategory.getCategoryName());
        if (isCategoryExist) {
            throw new ApiException("Category already exist!!!");
        }
        Category category = this.modelMapper.map(reqCategory, Category.class);
        Category savedCategory = this.categoryRepository.save(category);
        return this.modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category findingResult = this.getCategoryById(categoryId);
        this.categoryRepository.delete(findingResult);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO reqCategory) {
        Category findingResult = this.getCategoryById(id);
        boolean isCategoryExist = this.categoryRepository.existsCategoryByCategoryName(reqCategory.getCategoryName());
        if (isCategoryExist) {
            throw new ApiException("Category already exist!!!");
        }
        findingResult.setCategoryName(reqCategory.getCategoryName());
        Category savedCategory = this.categoryRepository.save(findingResult);
        return this.modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return this.categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("category", "categoryId", categoryId)
        );
    }

}
