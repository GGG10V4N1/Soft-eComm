package com.soft.ecommerce.service;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.CategoryResponse;
import com.soft.ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                           Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
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
        Optional<Category> savedCategory = categoryRepository.findByNameIgnoreCase(category.getName());
        if(savedCategory.isPresent()) {
            throw new APIException("Category with name: " + categoryDTO.getName() + " already exists");
        }
        Category categoryAdded = categoryRepository.save(category);
        return modelMapper.map(categoryAdded, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long id) {
        Category foundedCategory = categoryRepository
                                   .findById(id)
                                   .orElseThrow( () -> new ResourceNotFoundException("CATEGORY", "id", id) );
        categoryRepository.delete(foundedCategory);
        return modelMapper.map(foundedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category foundedCategory = categoryRepository
                                   .findById(id)
                                   .orElseThrow( () -> new ResourceNotFoundException("CATEGORY", "id", id) );
        Category categoryToSave = modelMapper.map(categoryDTO, Category.class);
        categoryToSave.setId(id); //saving the original id. the param category already has the new info
        Category updatedCategory = categoryRepository.save(categoryToSave);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }
}
