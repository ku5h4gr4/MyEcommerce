package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService{


    @Autowired
    private CategoryRepository categoryRepository;

     @Autowired
    private ModelMapper modelMapper;

    //Get Categories
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> existingCategories = categoryPage.getContent();

        if(existingCategories.isEmpty()){
            throw new ApiException("There are no Categories present!!");
        }
        List<CategoryDTO> categoryDTOS = existingCategories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    //Create a new Category
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDB = categoryRepository.
                findByCategoryName(category.getCategoryName());
        if(categoryFromDB != null){
            throw new ApiException("Category with name '"+category.getCategoryName()+"' already exists!!");
        }
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    //Delete existing Category
    public CategoryDTO deleteCategory(Long categoryId){
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.delete(savedCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }


    //Update existing Category
    public CategoryDTO updateCategory(CategoryDTO newCategoryDTO, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Category newCategory = modelMapper.map(newCategoryDTO, Category.class);
        newCategory.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(newCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

}
