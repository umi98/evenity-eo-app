package com.eska.evenity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.Invoice;
import com.eska.evenity.entity.Payment;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
  Optional<Invoice> findByInvoiceId(String invoiceId);
  Payment findByOrderId(String orderId);
}
