package com.soft.ecommerce.service.impl;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.PageResponse;
import com.soft.ecommerce.repository.CategoryRepository;
import com.soft.ecommerce.service.api.CategoryService;
import com.soft.ecommerce.utils.RefactorMethods;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    private CategoryDTO mapToDto(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        categoryRepository.findByNameIgnoreCase(category.getName())
                          .ifPresent(c -> {
                              throw new APIException("Category with name: " + c.getName() + " already exists");
                          });
        Category categoryAdded = categoryRepository.save(category);
        return modelMapper.map(categoryAdded, CategoryDTO.class);
    }

    @Override
    public PageResponse<CategoryDTO> findAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = RefactorMethods.buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        return RefactorMethods.getPageResponse(categoryPage, this::mapToDto, "NO CATEGORIES HAVE BEEN ADDED YET");
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category foundedCategory = categoryRepository
                                   .findById(categoryId)
                                   .orElseThrow( () -> new ResourceNotFoundException("CATEGORY", "id", categoryId) );
        Category categoryChanges = modelMapper.map(categoryDTO, Category.class);
        foundedCategory.setName(categoryChanges.getName());
        Category updatedCategory = categoryRepository.save(foundedCategory);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category foundedCategory = categoryRepository
                                   .findById(categoryId)
                                   .orElseThrow( () -> new ResourceNotFoundException("CATEGORY", "id", categoryId) );
        categoryRepository.delete(foundedCategory);
        return modelMapper.map(foundedCategory, CategoryDTO.class);
    }
}
