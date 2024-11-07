package com.eska.evenity.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.entity.Balance;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, String> {
  Page<Balance> findBalanceByUserCredential_Status(UserStatus status, Pageable pageable);
  Optional<Balance> findBalanceByUserCredential_Id(String userId);
}
