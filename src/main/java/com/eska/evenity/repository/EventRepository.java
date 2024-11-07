package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
  Page<Event> getEventByCustomer_IdAndIsDeleted(String id, Boolean status, Pageable pageable);
  Page<Event> getEventByIsDeleted(Boolean status, Pageable pageable);
  List<Event> findByIsDeleted(Boolean isDeleted);
}
