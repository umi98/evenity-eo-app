package com.eska.evenity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.entity.Balance;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, String> {
  List<Balance> findBalanceByUserCredential_Status(UserStatus status);
  Optional<Balance> findBalanceByUserCredential_Id(String userId);
}
