package com.eska.evenity.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Product;

@Repository
public interface EventDetailRepository extends JpaRepository<EventDetail, String> {
  List<EventDetail> findByEventId(String eventId);

  List<EventDetail> findByEventIdAndApprovalStatus(
          String eventId,
          ApprovalStatus approvalStatus
  );

  List<EventDetail> findByEventIdAndEventProgress(String eventId, EventProgress eventProgress);

  List<EventDetail> findByProduct(Product product);

  List<EventDetail> findByEventIdAndProduct(String eventId, Product product);

  Page<EventDetail> findByProduct_Vendor_Id(String id, Pageable pageable);

  Page<EventDetail> findByApprovalStatusAndCreatedDateBefore(
          ApprovalStatus approvalStatus,
          LocalDateTime createdDate,
          Pageable pageable
  );

  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
          "FROM EventDetail e WHERE e.event.id = :eventId AND e.approvalStatus = 'APPROVED' AND e.eventProgress = 'ON_PROGRESS'")
  boolean existsApprovedWithOnProgress(@Param("eventId") String eventId);

  @Query("SELECT SUM(e.cost) FROM EventDetail e WHERE e.event.id = :eventId")
  Long getTotalCostByEventId(@Param("eventId") String eventId);

  @Query("SELECT SUM(e.cost) FROM EventDetail e WHERE e.event.id = :eventId AND e.approvalStatus = 'APPROVED'")
  Long getTotalApprovedCostByEventId(@Param("eventId") String eventId);

  @Modifying
  @Query("UPDATE EventDetail e SET e.eventProgress = :eventProgress WHERE e.event.id = :eventId")
  void updateEventProgress(@Param("eventId") String eventId, @Param("eventProgress") EventProgress eventProgress);

  @Query("SELECT ed FROM EventDetail ed WHERE ed.event.id = :eventId " +
          "AND ed.product.category.id = :categoryId " +
          "AND ed.approvalStatus = :status")
  Optional<EventDetail> findByEventIdAndCategoryIdAndApprovalStatus(
          @Param("eventId") String eventId,
          @Param("categoryId") String categoryId,
          @Param("status") ApprovalStatus status);
}
