package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")                      //can use this or the below one
//    @RequestMapping(value = "/public/categories", method = RequestMethod.GET)
    private ResponseEntity<List<Category>> getAllCategories(){
        List<Category> catogories = categoryService.getAllCatogories();
        return new ResponseEntity<>(catogories, HttpStatus.OK);
    }

//    @PostMapping("/public/categories")
    @RequestMapping(value = "/public/categories", method = RequestMethod.POST)
    public ResponseEntity createCategory(@RequestBody Category category){
        categoryService.createCategory(category);
        return new ResponseEntity<>("Category added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity deleteCategory(@PathVariable Long categoryId){
        try{
            String status = categoryService.deleteCategory(categoryId);
//            return new ResponseEntity<>(status,HttpStatus.OK);          //first way(mostly used)
//            return ResponseEntity.ok(status);                            //second way
            return ResponseEntity.status(HttpStatus.OK).body(status);      //third way
        }
        catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity updateCategory(@RequestBody Category newCategory,
                                         @PathVariable Long categoryId){
        try{
            Category savedCategory = categoryService.updateCategory(newCategory,categoryId);
            return new ResponseEntity<>("Category Updated for id: "+categoryId, HttpStatus.OK);
        }
        catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }
}
