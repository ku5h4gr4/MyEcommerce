package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService{


    @Autowired
    private CategoryRepository categoryRepository;

    //Get Categories
    public List<Category> getAllCategories(){
        List<Category> existingCategories = categoryRepository.findAll();
        if(existingCategories.isEmpty()){
            throw new ApiException("There are no Categories present!!");
        }
        return existingCategories;
    }

    //Create a new Category
    public void createCategory(Category category) {
        Category savedCategory = categoryRepository.
                findByCategoryName(category.getCategoryName());
        if(savedCategory != null){
            throw new ApiException("Category with name '"+category.getCategoryName()+"' already exists!!");
        }
        categoryRepository.save(category);
    }

    //Delete existing Category
    public String deleteCategory(Long categoryId){
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.delete(savedCategory);
        return "Category id "+categoryId+" is deleted successfully";
    }


    //Update existing Category
    public Category updateCategory(Category newCategory, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));
        newCategory.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(newCategory);
        return savedCategory;
    }

}
