package com.soft.ecommerce.service.api;

import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.PageResponse;

public interface CategoryService {
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    PageResponse<CategoryDTO> findAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
}
