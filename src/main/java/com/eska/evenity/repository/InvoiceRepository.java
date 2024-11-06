package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.PaymentStatus;
import com.eska.evenity.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
  Invoice findByEventId(String eventId);
  List<Invoice> findByStatus(PaymentStatus status);
  Page<Invoice> findByEvent_Customer_Id(String id, Pageable pageable);
}
