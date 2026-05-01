package com.soft.ecommerce.service;

import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);
    ProductResponse findAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,
                                    String keyword, String category);
    ProductResponse findProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                                           String sortOrder);
}
