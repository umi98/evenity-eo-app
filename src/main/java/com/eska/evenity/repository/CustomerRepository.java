package com.eska.evenity.repository;

import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer getCustomerByUserCredential_Username(String username);
    Customer findCustomerByUserCredential(UserCredential userCredential);
}
