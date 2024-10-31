package com.eska.evenity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.WithdrawRequest;

import java.util.List;

@Repository
public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, String> {
  List<WithdrawRequest> findAllByBalance_UserCredential_Id(String id);
}
