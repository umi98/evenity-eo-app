package com.eska.evenity.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Product;

@Repository
public interface EventDetailRepository extends JpaRepository<EventDetail, String> {
  List<EventDetail> findByEventId(String eventId);
  List<EventDetail> findByEventIdAndApprovalStatus(String eventId, ApprovalStatus approvalStatus);
  List<EventDetail> findByEventIdAndEventProgress(String eventId, EventProgress eventProgress);
  List<EventDetail> findByProduct(Product product);
  List<EventDetail> findByEventIdAndProduct(String eventId, Product product);
  Page<EventDetail> findByProduct_Vendor_Id(String id, Pageable pageable);
  Page<EventDetail> findByApprovalStatusAndCreatedDateBefore(ApprovalStatus approvalStatus, LocalDateTime createdDate, Pageable pageable);
}
