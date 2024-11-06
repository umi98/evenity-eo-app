package com.eska.evenity.service;

import java.util.List;
import java.util.Optional;

import com.eska.evenity.dto.request.CustomerRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Customer createCustomer(Customer customer, UserCredential userCredential);
    Page<CustomerResponse> getAllCustomer(PagingRequest pagingRequest);
    Page<CustomerResponse> getAllActiveCustomer(PagingRequest pagingRequest);
    CustomerResponse getCustomerById(String id);
    Customer getCustomerByCustomerId(String id);
    Customer getCustomerByUserId(String id);
    CustomerResponse editCustomer(String id, CustomerRequest request);
    void deleteCustomer(String id);
    List<Customer> searchCustomer(String name);
}
