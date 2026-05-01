package com.soft.ecommerce.controller;

import com.soft.ecommerce.config.AppConstants;
import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.payload.ProductResponse;
import com.soft.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ecomApi")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")// not completed yet
    public ResponseEntity<ProductDTO> addProductAdmin(@PathVariable Long categoryId, @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO addedProduct = productService.addProduct(categoryId,productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedProduct);
    }

    @PostMapping("/seller/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProductSeller(@PathVariable Long categoryId,
                                                       @Valid @RequestBody ProductDTO productDTO){
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductDTO);
    }

    @GetMapping("/public/products") //finished
    public ResponseEntity<ProductResponse> findAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category){

        ProductResponse productResponse = productService.findAllProducts(pageNumber, pageSize, sortBy, sortOrder,
                                                                         keyword, category);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> findProductsByCategory(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
            @PathVariable Long categoryId){
        ProductResponse productResponse = productService.findProductsByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }


}
