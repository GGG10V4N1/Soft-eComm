package com.soft.ecommerce.service;

import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.CategoryResponse;
import org.springframework.web.bind.annotation.RequestParam;

public interface CategoryService {
    CategoryResponse getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO add(CategoryDTO categoryDTO);
    CategoryDTO delete(Long id);
    CategoryDTO update(Long id, CategoryDTO categoryDTO);
}
