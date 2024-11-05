package com.eska.evenity.service;

import java.util.List;
import java.util.Optional;

import com.eska.evenity.dto.request.CustomerRequest;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;

public interface CustomerService {
    Customer createCustomer(Customer customer, UserCredential userCredential);
    List<CustomerResponse> getAllCustomer();
    List<CustomerResponse> getAllActiveCustomer();
    CustomerResponse getCustomerById(String id);
    Customer getCustomerByCustomerId(String id);
    Customer getCustomerByUserId(String id);
    CustomerResponse editCustomer(String id, CustomerRequest request);
    void deleteCustomer(String id);
    List<Customer> searchCustomer(String name);
}
