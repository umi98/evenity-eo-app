package com.eska.evenity.repository;

import com.eska.evenity.entity.ProceedingEventFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProceedingEventFeeRepository extends JpaRepository<ProceedingEventFee, String> {
}
