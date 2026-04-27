package com.soft.ecommerce.controller;

import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ecomApi")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

        @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> add(@PathVariable Long categoryId, @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO addedProduct = this.productService.add(categoryId,productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedProduct);
    }


}
