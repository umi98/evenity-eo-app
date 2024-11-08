package com.eska.evenity.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.eska.evenity.dto.request.CustomerRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;

public interface CustomerService {
    Customer createCustomer(Customer customer, UserCredential userCredential);
    Page<CustomerResponse> getAllCustomer(PagingRequest pagingRequest);
    Page<CustomerResponse> getAllActiveCustomer(PagingRequest pagingRequest);
    CustomerResponse getCustomerById(String id);
    Customer getCustomerByCustomerId(String id);
    Customer getCustomerByUserId(String id);
    CustomerResponse editCustomer(String id, CustomerRequest request);
    CustomerResponse disableCustomer(String id);
    CustomerResponse enableCustomer(String id);
    void deleteCustomer(String id);
    List<Customer> searchCustomer(String name);
    List<Customer> getAllCustomers();
    Integer countVendorRegisterThisMonth();
}
