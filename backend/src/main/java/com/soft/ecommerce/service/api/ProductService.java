package com.soft.ecommerce.service.api;

import com.soft.ecommerce.payload.PageResponse;
import com.soft.ecommerce.payload.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);
    PageResponse<ProductDTO> findAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,
                                             String keyword, String category);
    PageResponse<ProductDTO> findProductsByCategory(Integer pageNumber, Integer pageSize, String sortBy,
                                           String sortOrder,Long categoryId);
    PageResponse<ProductDTO> findProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize,
                                          String sortBy, String sortOrder);
    PageResponse<ProductDTO> findAllProductsForAdmin(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    PageResponse<ProductDTO> findAllProductsForSeller(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO deleteProduct(Long productId);
}
