package com.eska.evenity.repository;

import com.eska.evenity.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository <Category, String> {
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE EXISTS (" +
            "SELECT p FROM Product p WHERE p.category = c AND p.isDeleted = false" +
            " AND p.vendor.status = 'ACTIVE')")
    List<Category> findCategoriesWithProducts();
}