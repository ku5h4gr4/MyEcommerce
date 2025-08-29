package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import java.util.List;

public interface CategoryService {

    List<Category> getAllCatogories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
    Category updateCategory(Category newCategory, Long categoryId);


}

