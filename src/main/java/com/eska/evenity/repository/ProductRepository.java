package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId);
    List<Product> findByVendorId(String vendorId);
    List<Product> findByIsDeleted(Boolean isDeleted);
    List<Product> findByVendorIdAndIsDeleted(String vendorId, Boolean isDeleted);
    @Query("SELECT MIN(p.price) FROM Product p " +
            "INNER JOIN p.vendor v " +
            "INNER JOIN v.userCredential uc " +
            "WHERE p.category.id = :categoryId " +
            "AND v.province = :province " +
            "AND v.city = :city " +
            "AND uc.status = 'ACTIVE' " +
            "AND p.isDeleted = false")
    Long findMinPrice(String categoryId, String province, String city);
    @Query("SELECT MAX(p.price) FROM Product p " +
            "INNER JOIN p.vendor v " +
            "INNER JOIN v.userCredential uc " +
            "WHERE p.category.id = :categoryId " +
            "AND v.province = :province " +
            "AND v.city = :city " +
            "AND uc.status = 'ACTIVE' " +
            "AND p.isDeleted = false")
    Long findMaxPrice(String categoryId, String province, String city);
    @Query("SELECT p FROM Product p " +
            "JOIN p.vendor v " +
            "JOIN v.userCredential uc " +
            "WHERE v.province = :province " +
            "AND v.city = :city " +
            "AND p.category.id = :categoryId " +
            "AND p.price BETWEEN :minCost AND :maxCost " +
            "AND p.id NOT IN :previousProducts " +
            "AND uc.status = 'ACTIVE' " +
            "AND p.isDeleted = false " +
            "ORDER BY v.scoring DESC")
    List<Product> findRecommendation(String province, String city, String categoryId,Long minCost, Long maxCost, List<String> previousProducts);
}
