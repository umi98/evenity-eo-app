package com.eska.evenity.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
  Page<Event> getEventByCustomer_IdAndIsDeleted(String id, Boolean status, Pageable pageable);
  Page<Event> getEventByIsDeleted(Boolean status, Pageable pageable);
  List<Event> findByIsDeleted(Boolean isDeleted);

  @Query("SELECT COUNT(e) FROM Event e WHERE " +
          "MONTH(e.startDate) = MONTH(CURRENT_DATE) AND " +
          "YEAR(e.startDate) = YEAR(CURRENT_DATE) AND " +
          "(e.isDeleted = false OR e.isDeleted IS NULL)")
  long countEventsThisMonth();

  @Query("SELECT COUNT(e) FROM Event e WHERE e.endDate < :currentDate AND " +
          "(e.isDeleted = false OR e.isDeleted IS NULL)")
  long countPastEvents(@Param("currentDate") LocalDate currentDate);

  @Query("SELECT COUNT(e) FROM Event e WHERE e.startDate > :currentDate AND " +
          "(e.isDeleted = false OR e.isDeleted IS NULL)")
  long countFutureEvents(@Param("currentDate") LocalDate currentDate);
}
