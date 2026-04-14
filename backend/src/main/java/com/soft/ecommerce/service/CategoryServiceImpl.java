package com.soft.ecommerce.service;

import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAll() {
        return this.categoryRepository.findAll();
    }

    @Override
    public void add(Category category) {
        this.categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        Category foundCategory = this.categoryRepository.findById(id)
                            .orElseThrow( () -> new ResourceNotFoundException("CATEGORY", "id", id) );

        categoryRepository.delete(foundCategory);
    }

    @Override
    public void update(Long id, Category category) {

    }
}
