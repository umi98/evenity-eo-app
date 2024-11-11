package com.eska.evenity.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.PaymentStatus;
import com.eska.evenity.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
  Invoice findByEventId(String eventId);
  List<Invoice> findByStatus(PaymentStatus status);
  Page<Invoice> findByEvent_Customer_Id(String id, Pageable pageable);

  @Query("SELECT COALESCE(SUM(af.nominal), 0) + COALESCE(SUM(ed.cost), 0) " +
          "FROM Invoice i " +
          "JOIN AdminFee af ON af.invoice = i " +
          "JOIN InvoiceDetail id ON id.invoice = i " +
          "JOIN EventDetail ed ON id.eventDetail = ed " +
          "WHERE i.status = :status " +
          "AND i.paymentDate >= :startOfMonth " +
          "AND i.paymentDate < :endOfMonth")
  Long calculateGrossIncomeForMonth(@Param("status") PaymentStatus status,
                                    @Param("startOfMonth") LocalDateTime startOfMonth,
                                    @Param("endOfMonth") LocalDateTime endOfMonth);

  @Query("SELECT COALESCE(SUM(af.nominal), 0) + COALESCE(SUM(ed.cost), 0) " +
          "FROM Invoice i " +
          "JOIN AdminFee af ON af.invoice = i " +
          "JOIN InvoiceDetail id ON id.invoice = i " +
          "JOIN EventDetail ed ON id.eventDetail = ed " +
          "WHERE i.status = :status")
  Long calculateAllTimeGrossIncome(@Param("status") PaymentStatus status);

  @Query("SELECT i FROM Invoice i " +
          "JOIN i.event e " +
          "JOIN e.customer c " +
          "WHERE (:name IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
          "OR (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))")
  Page<Invoice> findByCustomerNameOrEventName(@Param("name") String name, Pageable pageable);

}
