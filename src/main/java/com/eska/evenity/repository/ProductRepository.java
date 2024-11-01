package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eska.evenity.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId);
    List<Product> findByVendorId(String vendorId);
    List<Product> findByIsDeleted(Boolean isDeleted);
    List<Product> findByVendorIdAndIsDeleted(String vendorId, Boolean isDeleted);
}
