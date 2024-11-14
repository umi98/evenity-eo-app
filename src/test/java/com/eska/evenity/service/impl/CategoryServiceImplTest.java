package com.eska.evenity.service.impl;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.dto.request.CategoryRequest;
import com.eska.evenity.dto.response.CategoryResponse;
import com.eska.evenity.entity.Category;
import com.eska.evenity.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        this.categoryRequest = new CategoryRequest("CATERING", "Snack");
        this.category = Category.builder()
                .id("1")
                .mainCategory(CategoryType.CATERING)
                .name("Snack")
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    @Test
    void createCategory_shouldReturnCategoryResponse() {
        when(categoryRepository.saveAndFlush(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.createCategory(categoryRequest);

        assertNotNull(response);
        assertEquals(category.getName(), response.getName());
        verify(categoryRepository, times(1)).saveAndFlush(any(Category.class));
    }

    @Test
    void getCategoryById_shouldReturnCategoryResponse() {
        when(categoryRepository.findById("1")).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.getCategoryById("1");

        assertNotNull(response);
        assertEquals(category.getName(), response.getName());
        verify(categoryRepository, times(1)).findById("1");
    }

    @Test
    void getCategoryById_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> categoryService.getCategoryById("1"));
    }

    @Test
    void getAllCategories_shouldReturnCategoryResponseList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryResponse> responseList = categoryService.getAllCategories();

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void updateCategory_shouldReturnUpdatedCategoryResponse() {
        when(categoryRepository.findById("1")).thenReturn(Optional.of(category));
        when(categoryRepository.saveAndFlush(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.updateCategory("1", categoryRequest);

        assertNotNull(response);
        assertEquals(categoryRequest.getName(), response.getName());
        verify(categoryRepository, times(1)).findById("1");
        verify(categoryRepository, times(1)).saveAndFlush(any(Category.class));
    }

    @Test
    void deleteCategory_shouldDeleteCategory() {
        when(categoryRepository.findById("1")).thenReturn(Optional.of(category));

        categoryService.deleteCategory("1");

        verify(categoryRepository, times(1)).findById("1");
        verify(categoryRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteCategory_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory("1"));
    }
}