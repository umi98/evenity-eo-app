package com.eska.evenity.service.impl;

import com.eska.evenity.dto.request.CategoryRequest;
import com.eska.evenity.dto.response.CategoryResponse;
import com.eska.evenity.entity.Category;
import com.eska.evenity.repository.CategoryRepository;
import com.eska.evenity.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();
        category = categoryRepository.save(category);
        return new CategoryResponse(category.getId(), category.getName());
    }

    @Override
    public Optional<CategoryResponse> getCategoryById(String categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> new CategoryResponse(category.getId(), category.getName()));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoryResponse> updateCategory(String categoryId, CategoryRequest categoryRequest) {
        return categoryRepository.findById(categoryId).map(category -> {
            category.setName(categoryRequest.getName());
            category = categoryRepository.save(category);
            return new CategoryResponse(category.getId(), category.getName());
        });
    }

    @Override
    public void deleteCategory(String categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
