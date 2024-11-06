package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.Vendor;
import com.eska.evenity.entity.WithdrawRequest;

@Repository
public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, String> {
  Page<WithdrawRequest> findAllByBalance_UserCredential_Id(String id, Pageable pageable);
  @Query("SELECT v FROM Vendor v JOIN v.userCredential uc WHERE uc.id = :userId")
  Vendor findVendorByUserCredentialId(@Param("userId") String userId);

  // Vendor getVendorByBalance_UserCredential_Id(String id);
}
