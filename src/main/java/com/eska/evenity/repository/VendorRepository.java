package com.eska.evenity.repository;

import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {
    Vendor getVendorByUserCredential_Username(String username);
    Vendor findVendorByUserCredential(UserCredential userCredential);
}
