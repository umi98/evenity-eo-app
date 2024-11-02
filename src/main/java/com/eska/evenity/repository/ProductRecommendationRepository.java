package com.eska.evenity.repository;

import com.eska.evenity.entity.ProductRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRecommendationRepository extends JpaRepository<ProductRecommendation, String> {
}
