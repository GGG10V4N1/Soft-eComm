package com.soft.ecommerce.controller;

import com.soft.ecommerce.config.AppConstants;
import com.soft.ecommerce.payload.PageResponse;
import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.service.api.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// finished
@RestController
@RequestMapping("/ecomApi")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable Long categoryId, @Valid @RequestBody ProductDTO productDTO) {

        ProductDTO addedProduct = productService.addProduct(categoryId,productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedProduct);
    }

    @PostMapping("/seller/categories/{categoryId}/product")// finished
    public ResponseEntity<ProductDTO> addProductForSeller(@PathVariable Long categoryId,
                                                       @Valid @RequestBody ProductDTO productDTO){
    
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductDTO);
    }

    @GetMapping("/public/products") //finished
    public ResponseEntity<PageResponse<ProductDTO>> findAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category){

        PageResponse<ProductDTO> productResponse = productService.findAllProducts(pageNumber, pageSize, sortBy, sortOrder,
                                                                         keyword, category);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @GetMapping("/admin/products")//finished
    public ResponseEntity<PageResponse<ProductDTO>> findAllProductsForAdmin(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){

        PageResponse<ProductDTO> productResponse = productService.findAllProductsForAdmin(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @GetMapping("/seller/products")//finished
    public ResponseEntity<PageResponse<ProductDTO>> findAllProductsForSeller( //not completed yet
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){

        PageResponse<ProductDTO> productResponse = productService.findAllProductsForSeller(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @GetMapping("/public/categories/{categoryId}/products")//finished
    public ResponseEntity<PageResponse<ProductDTO>> findProductsByCategory(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
            @PathVariable Long categoryId){

        PageResponse<ProductDTO> productResponse = productService.findProductsByCategory(pageNumber,pageSize, sortBy, sortOrder, categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @GetMapping("/public/products/keyword/{keyword}") //finished
    public ResponseEntity<PageResponse<ProductDTO>> findProductsByKeyword(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
            @PathVariable String keyword){

        PageResponse<ProductDTO> productResponse = productService.findProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @PutMapping("/admin/products/{productId}")//finished
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductDTO productDTO){

        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProductDTO);
    }

    @PutMapping("/seller/products/{productId}") //finished
    public ResponseEntity<ProductDTO> updateProductForSeller(@Valid @RequestBody ProductDTO productDTO,
                                                             @PathVariable Long productId){

        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProductDTO);
    }

    @PutMapping("/admin/products/{productId}/image") //finished
    public ResponseEntity<ProductDTO> updateProductImage(@RequestParam(name = "image") MultipartFile image,
                                                         @PathVariable Long productId) throws IOException {

        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @PutMapping("/seller/products/{productId}/image")//finished
    public ResponseEntity<ProductDTO> updateProductImageForSeller(@PathVariable Long productId,
                                                                  @RequestParam("image")MultipartFile image) throws IOException {

        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @DeleteMapping("/admin/products/{productId}")// finished
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){

        ProductDTO deletedProduct = productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedProduct);
    }

    @DeleteMapping("/seller/products/{productId}")//finished
    public ResponseEntity<ProductDTO> deleteProductForSeller(@PathVariable Long productId){

        ProductDTO deletedProduct = productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedProduct);
    }
}
