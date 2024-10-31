package com.eska.evenity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.TransactionHistory;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, String> {
  List<TransactionHistory> getTransactionHistoryByUserCredential_Id(String id);
}
