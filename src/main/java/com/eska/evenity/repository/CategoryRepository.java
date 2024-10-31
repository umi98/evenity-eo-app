package com.eska.evenity.repository;

import com.eska.evenity.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository <Category, String> {
    Optional<Category> findByName(String name);
}