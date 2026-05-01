package com.soft.ecommerce.service;

import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse findAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long id);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
}
