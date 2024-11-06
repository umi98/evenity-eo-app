package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;


@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {
    Vendor getVendorByUserCredential_Username(String username);
    Vendor findVendorByUserCredential(UserCredential userCredential);
    List<Vendor> findAllByNameLikeIgnoreCase(String name);

    @Query("SELECT v FROM Vendor v WHERE v.userCredential.status = :status")
    Page<Vendor> getVendorByStatus(@Param("status") UserStatus status, Pageable pageable);

    Page<Vendor> findByStatus(VendorStatus status, Pageable pageable);
}
