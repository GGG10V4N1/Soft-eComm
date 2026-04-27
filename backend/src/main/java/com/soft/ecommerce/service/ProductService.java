package com.soft.ecommerce.service;

import com.soft.ecommerce.payload.ProductDTO;

public interface ProductService {
    ProductDTO add(Long categoryId, ProductDTO productDTO);
}
