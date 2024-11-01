package com.eska.evenity.service;

import java.util.List;

import com.eska.evenity.dto.request.CategoryRequest;
import com.eska.evenity.dto.response.CategoryResponse;
import com.eska.evenity.entity.Category;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    CategoryResponse getCategoryById(String categoryId);
    Category getCategoryUsingId(String id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(String categoryId, CategoryRequest categoryRequest);
    void deleteCategory(String categoryId);
}
