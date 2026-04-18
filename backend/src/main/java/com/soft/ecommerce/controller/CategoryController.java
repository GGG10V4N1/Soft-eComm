package com.soft.ecommerce.controller;

import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ecomApi")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getAll(){
         return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAll());
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<String> add(@Valid @RequestBody Category category){
        this.categoryService.add(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(category.toString());
    }

    @DeleteMapping("/admin/categories/{id}")
    public String delete(@PathVariable Long id){
        this.categoryService.delete(id);
        return "success delete";
    }

    @PutMapping("/admin/categories/{id}")
    public String update(@PathVariable Long id, @RequestBody Category category){
        this.categoryService.update(id, category);
        return "success update";
    }

}
