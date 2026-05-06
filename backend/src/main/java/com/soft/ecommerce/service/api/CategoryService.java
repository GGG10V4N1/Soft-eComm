package com.soft.ecommerce.service.api;

import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.PageResponse;

public interface CategoryService {
    PageResponse<CategoryDTO> findAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
}
