package com.eska.evenity.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.eska.evenity.constant.CategoryType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eska.evenity.dto.request.CategoryRequest;
import com.eska.evenity.dto.response.CategoryResponse;
import com.eska.evenity.entity.Category;
import com.eska.evenity.repository.CategoryRepository;
import com.eska.evenity.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
            System.out.println(categoryRequest);
        try {
            Category category = Category.builder()
                    .mainCategory(CategoryType.valueOf(categoryRequest.getMainType()))
                    .name(categoryRequest.getName())
                    .createdDate(LocalDateTime.now())
                    .modifiedDate(LocalDateTime.now())
                    .build();
            categoryRepository.saveAndFlush(category);
            return mapToResponse(category);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CategoryResponse getCategoryById(String categoryId) {
        Category result = findByIdOrThrowException(categoryId);
        return mapToResponse(result);
    }

    @Override
    public Category getCategoryUsingId(String id) {
        return findByIdOrThrowException(id);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> result = categoryRepository.findAll();
        return result.stream().map(this::mapToResponse).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CategoryResponse updateCategory(String categoryId, CategoryRequest categoryRequest) {
        try {
            Category category = findByIdOrThrowException(categoryId);
            category.setMainCategory(CategoryType.valueOf(categoryRequest.getMainType()));
            category.setName(categoryRequest.getName());
            category.setModifiedDate(LocalDateTime.now());
            categoryRepository.saveAndFlush(category);
            return mapToResponse(category);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCategory(String categoryId) {
        try {
            findByIdOrThrowException(categoryId);
            categoryRepository.deleteById(categoryId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Category findByIdOrThrowException(String id) {
        Optional<Category> result = categoryRepository.findById(id);
        return result.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found"));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .mainCategory(category.getMainCategory().name())
                .name(category.getName())
                .createdDate(category.getCreatedDate())
                .modifiedDate(category.getModifiedDate())
                .build();
    }
}
