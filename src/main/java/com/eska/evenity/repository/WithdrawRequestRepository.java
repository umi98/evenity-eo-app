package com.eska.evenity.repository;

import com.eska.evenity.constant.ApprovalStatus;
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

  @Query("SELECT COALESCE(SUM(w.amount), 0) " +
          "FROM WithdrawRequest w " +
          "WHERE w.approvalStatus = :status")
  Long getTotalApprovedWithdrawalAmount(@Param("status") ApprovalStatus status);

  @Query("SELECT COALESCE(SUM(w.amount), 0) " +
          "FROM WithdrawRequest w " +
          "WHERE w.approvalStatus = :status " +
          "AND YEAR(w.createdDate) = :year " +
          "AND MONTH(w.createdDate) = :month")
  Long getTotalApprovedWithdrawalAmountForCurrentMonth(
          @Param("status") ApprovalStatus status,
          @Param("year") int year,
          @Param("month") int month);

  @Query("SELECT COUNT(wr) > 0 FROM WithdrawRequest wr " +
          "WHERE wr.balance.userCredential.id = :userId " +
          "AND wr.approvalStatus = :status")
  boolean existsPendingRequestByUserId(
          @Param("userId") String userId,
          @Param("status") ApprovalStatus status);
  // Vendor getVendorByBalance_UserCredential_Id(String id);
}
