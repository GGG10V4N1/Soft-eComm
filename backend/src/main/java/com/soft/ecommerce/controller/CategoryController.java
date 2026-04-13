package com.soft.ecommerce.controller;

import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.service.CategoryService;
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
         return new ResponseEntity<>(this.categoryService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<String> add(@RequestBody Category category){

        this.categoryService.add(category);

        return new ResponseEntity<>(category.toString(), HttpStatus.CREATED);
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
