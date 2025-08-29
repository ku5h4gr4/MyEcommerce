package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    List<Category> categories = new ArrayList<>();
    private  Long nextId =1L;

    public List<Category> getAllCatogories(){
        return categories;
    }

    public void createCategory(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
    }

    public String deleteCategory(Long categoryId){
        Category category = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resoure not Found"));
        categories.remove(category);
        return "Category id "+categoryId+" is deleted successfully";
    }

    @Override
    public Category updateCategory(Category newCategory, Long categoryId) {
        Optional<Category> optionalCategory = categories.stream()
                .filter(c-> c.getCategoryId().equals(categoryId))
                .findFirst();

        if(optionalCategory.isPresent()){
            Category existingCategory = optionalCategory.get();
            existingCategory.setCategoryName(newCategory.getCategoryName());
            return existingCategory;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found");
        }
    }


}
