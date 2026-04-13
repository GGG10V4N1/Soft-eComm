package com.soft.ecommerce.service;

import com.soft.ecommerce.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<Category> getAll() {
        return List.of();
    }

    @Override
    public void add(Category category) {

    }

    @Override
    public void delete(Long id) {
    }

    @Override
    public void update(Long id, Category category) {

    }
}
