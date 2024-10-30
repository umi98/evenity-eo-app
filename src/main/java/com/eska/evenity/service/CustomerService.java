package com.eska.evenity.service;

import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer, UserCredential userCredential);
    List<CustomerResponse> getAllCustomer();
    CustomerResponse getCustomerById(String id);
    CustomerResponse getCustomerByUserId(String id);
}
