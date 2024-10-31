package com.eska.evenity.controller;

import com.eska.evenity.dto.request.CategoryRequest;
import com.eska.evenity.dto.response.CategoryResponse;
import com.eska.evenity.dto.response.WebResponse;
import com.eska.evenity.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<WebResponse<CategoryResponse>> createCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
        WebResponse<CategoryResponse> response = WebResponse.<CategoryResponse>builder()
                .status(HttpStatus.CREATED.getReasonPhrase())
                .message("Category successfully created")
                .data(categoryResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<WebResponse<CategoryResponse>> getCategoryById(@PathVariable String categoryId) {
        return categoryService.getCategoryById(categoryId)
                .map(categoryResponse -> {
                    WebResponse<CategoryResponse> response = WebResponse.<CategoryResponse>builder()
                            .status(HttpStatus.OK.getReasonPhrase())
                            .message("Successfully retrieved category")
                            .data(categoryResponse)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(WebResponse.<CategoryResponse>builder()
                                .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                                .message("Category not found")
                                .build()));
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        WebResponse<List<CategoryResponse>> response = WebResponse.<List<CategoryResponse>>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Successfully retrieved all categories")
                .data(categories)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<WebResponse<CategoryResponse>> updateCategory(
            @PathVariable String categoryId,
            @RequestBody CategoryRequest categoryRequest) {
        return categoryService.updateCategory(categoryId, categoryRequest)
                .map(categoryResponse -> {
                    WebResponse<CategoryResponse> response = WebResponse.<CategoryResponse>builder()
                            .status(HttpStatus.OK.getReasonPhrase())
                            .message("Category successfully updated")
                            .data(categoryResponse)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(WebResponse.<CategoryResponse>builder()
                                .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                                .message("Category not found")
                                .build()));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);

        WebResponse<Void> response = WebResponse.<Void>builder()
                .status(HttpStatus.OK.getReasonPhrase())
                .message("Category deleted")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

}
