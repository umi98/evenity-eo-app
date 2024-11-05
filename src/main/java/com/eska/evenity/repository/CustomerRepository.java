package com.eska.evenity.repository;

import java.util.List;

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

    @Query("SELECT c FROM Customer c WHERE c.userCredential.status = :status")
    List<Customer> getCustomerByStatus(@Param("status") UserStatus status);
}
