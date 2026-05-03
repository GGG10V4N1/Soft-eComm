package com.soft.ecommerce.service;

import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);
    ProductResponse findAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,
                                    String keyword, String category);
    ProductResponse findProductsByCategory(Integer pageNumber, Integer pageSize, String sortBy,
                                           String sortOrder,Long categoryId);
    ProductResponse findProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize,
                                          String sortBy, String sortOrder);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO deleteProduct(Long productId);
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
    ProductResponse findAllProductsForAdmin(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse findAllProductsForSeller(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
