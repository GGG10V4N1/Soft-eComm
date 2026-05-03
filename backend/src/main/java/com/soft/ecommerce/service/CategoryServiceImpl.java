package com.soft.ecommerce.service;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.CategoryResponse;
import com.soft.ecommerce.repository.CategoryRepository;
import com.soft.ecommerce.utils.RefactorMethods;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse findAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = RefactorMethods.buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();

        if(categories.isEmpty()) throw new APIException("NO CATEGORIES HAVE BEEN ADDED YET");

        List<CategoryDTO> categoryDTOS = categories.stream()
                                                   .map(c -> modelMapper.map(c, CategoryDTO.class))
                                                   .toList();

        return CategoryResponse.builder()
                               .content(categoryDTOS)
                               .totalElements(categoryPage.getTotalElements())
                               .pageSize(categoryPage.getSize())
                               .pageNumber(categoryPage.getNumber())
                               .totalPages(categoryPage.getTotalPages())
                               .lastPage(categoryPage.isLast())
                               .build();
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
    public CategoryDTO deleteCategory(Long categoryId) {
        Category foundedCategory = categoryRepository
                                   .findById(categoryId)
                                   .orElseThrow( () -> new ResourceNotFoundException("CATEGORY", "id", categoryId) );
        categoryRepository.delete(foundedCategory);
        return modelMapper.map(foundedCategory, CategoryDTO.class);
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
}
