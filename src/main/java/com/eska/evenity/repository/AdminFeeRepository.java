package com.eska.evenity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.AdminFee;

@Repository
public interface AdminFeeRepository extends JpaRepository<AdminFee, String> {
  Optional<AdminFee> findByInvoice_Id(String invoiceId);
}
