package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.Vendor;
import com.eska.evenity.entity.WithdrawRequest;

@Repository
public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, String> {
  List<WithdrawRequest> findAllByBalance_UserCredential_Id(String id);
  Vendor getVendorByBalance_UserCredential_Id(String id);
}
