package com.eska.evenity.service;

import com.eska.evenity.dto.request.CategoryRequest;
import com.eska.evenity.dto.response.CategoryResponse;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    Optional<CategoryResponse> getCategoryById(String categoryId);
    List<CategoryResponse> getAllCategories();
    Optional<CategoryResponse> updateCategory(String categoryId, CategoryRequest categoryRequest);
    void deleteCategory(String categoryId);
}
