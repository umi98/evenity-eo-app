package com.eska.evenity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eska.evenity.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
  List<Event> getEventByCustomer_IdAndDeletionStatus(String id, Boolean status);
  List<Event> getEventByDeletionStatus(Boolean status);
}