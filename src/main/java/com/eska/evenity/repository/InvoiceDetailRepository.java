package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.entity.InvoiceDetail;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, String> {
  List<InvoiceDetail> findByInvoice_Id(String id);
  List<InvoiceDetail> findByInvoice_IdAndEventDetail_ApprovalStatus(String id, ApprovalStatus approval);
  InvoiceDetail findByEventDetail_Id(String id);
  Page<InvoiceDetail> findByEventDetail_Product_Vendor_Id(String id, Pageable pageable);
  @Query("SELECT ed.cost FROM InvoiceDetail id " +
          "JOIN id.eventDetail ed " +
          "WHERE id.invoice.id = :invoiceId")
  List<Long> findAllCostsByInvoiceId(@Param("invoiceId") String invoiceId);
  @Query("SELECT eventDetail.cost FROM InvoiceDetail invoiceDetail " +
          "JOIN invoiceDetail.eventDetail eventDetail " +
          "WHERE invoiceDetail.id = :invoiceDetailId")
  Long findCostFromInvoiceDetail(@Param("invoiceDetailId") String invoiceDetailId);
}
