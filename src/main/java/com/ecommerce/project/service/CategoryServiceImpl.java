package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

//    List<Category> categories = new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCatogories(){
        return categoryRepository.findAll();
    }

    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    public String deleteCategory(Long categoryId){

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resoure not Found"));
        categoryRepository.delete(savedCategory);
        return "Category id "+categoryId+" is deleted successfully";
    }

    @Override
    public Category updateCategory(Category newCategory, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));
        newCategory.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(newCategory);
        return savedCategory;
    }

}
