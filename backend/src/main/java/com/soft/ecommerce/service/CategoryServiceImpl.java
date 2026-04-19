package com.soft.ecommerce.service;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.CategoryResponse;
import com.soft.ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
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
    public CategoryResponse getAll(){
        List<Category> categories = this.categoryRepository.findAll();
        if(categories.isEmpty()) {
            throw new APIException("NO CATEGORIES HAVE BEEN ADDED YET");
        }
        List<CategoryDTO> categoryDTOS = categories
                                        .stream()
                                        .map(category -> modelMapper.map(category, CategoryDTO.class))
                                        .toList();
//        CategoryResponse categoryResponse = new CategoryResponse();
//        categoryResponse.setContent(categoryDTOS);
//        return categoryResponse;
        return new CategoryResponse(categoryDTOS);
    }

    @Override
    public CategoryDTO add(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> savedCategory = this.categoryRepository.findByName(category.getName());
        if(savedCategory.isPresent()) {
            throw new APIException("Category with name: " + categoryDTO.getName() + " already exists");
        }
        Category categoryAdded = this.categoryRepository.save(category);
        return this.modelMapper.map(categoryAdded, CategoryDTO.class);
    }

    @Override
    public CategoryDTO delete(Long id) {
        Category foundedCategory = this.categoryRepository
                                       .findById(id)
                                       .orElseThrow( () -> new ResourceNotFoundException("CATEGORY", "id", id) );
        categoryRepository.delete(foundedCategory);
        return modelMapper.map(foundedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        Category foundedCategory = this.categoryRepository
                                     .findById(id)
                                     .orElseThrow( () -> new ResourceNotFoundException("CATEGORY", "id", id) );
        Category categoryToSave = modelMapper.map(categoryDTO, Category.class);
        categoryToSave.setId(id); //saving the original id. the param category already has the new info
        Category updatedCategory = this.categoryRepository.save(categoryToSave);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }
}
