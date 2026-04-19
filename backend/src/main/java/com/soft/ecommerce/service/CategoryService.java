package com.soft.ecommerce.service;

import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAll();
    CategoryDTO add(CategoryDTO categoryDTO);
    CategoryDTO delete(Long id);
    CategoryDTO update(Long id, CategoryDTO categoryDTO);
}
