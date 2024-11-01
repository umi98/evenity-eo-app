package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.TransactionHistory;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, String> {
  List<TransactionHistory> getTransactionHistoryByCreatedBy_Id(String id);
}
