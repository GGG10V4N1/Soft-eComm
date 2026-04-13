package com.soft.ecommerce.service;

import com.soft.ecommerce.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAll();
    void add(Category category);
    void delete(Long id);

    void update(Long id, Category category);

}
