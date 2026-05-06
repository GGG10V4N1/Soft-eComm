package com.soft.ecommerce.controller;

import com.soft.ecommerce.config.AppConstants;
import com.soft.ecommerce.payload.CategoryDTO;
import com.soft.ecommerce.payload.PageResponse;
import com.soft.ecommerce.service.api.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ecomApi")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<PageResponse<CategoryDTO>> findAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){

        PageResponse<CategoryDTO> categories = categoryService.findAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO){

        CategoryDTO addedCategoryDTO = categoryService.addCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedCategoryDTO);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDTO categoryDTO){

        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCategoryDTO);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){

        CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedCategory);
    }


}
