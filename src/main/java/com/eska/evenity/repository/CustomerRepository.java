package com.eska.evenity.repository;

import java.util.List;

import com.eska.evenity.constant.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer getCustomerByUserCredential_Username(String username);
    Customer findCustomerByUserCredential(UserCredential userCredential);
    List<Customer> findAllByFullNameLikeIgnoreCase(String fullName);

    @Query("SELECT c FROM Customer c WHERE c.userCredential.status = :status AND c.status = :customerStatus")
    Page<Customer> getCustomerByStatus(
            @Param("status") UserStatus status,
            @Param("customerStatus") CustomerStatus customerStatus,
            Pageable pageable
    );

    @Query("SELECT COUNT(c) FROM Customer c WHERE MONTH(c.createdDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(c.createdDate) = YEAR(CURRENT_DATE)")
    Integer countCustomersRegisteredThisMonth();
}
