package com.soft.ecommerce.service;

import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.payload.ProductResponse;

public interface ProductService {
    ProductDTO add(Long categoryId, ProductDTO productDTO);

    ProductResponse getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category);
}
