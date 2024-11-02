package com.eska.evenity.repository;

import com.eska.evenity.entity.EventDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDetailRepository extends JpaRepository<EventDetail, String> {
}
