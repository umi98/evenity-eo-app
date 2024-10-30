package com.eska.evenity.service.impl;

import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.repository.CustomerRepository;
import com.eska.evenity.service.CustomerService;
import com.eska.evenity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserService userService;

    @Override
    public Customer createCustomer(Customer customer, UserCredential userCredential) {
        Customer newCustomer = Customer.builder()
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .userCredential(userCredential)
                .createdDate(Date.from(Instant.now()))
                .modifiedDate(Date.from(Instant.now()))
                .build();
        customerRepository.saveAndFlush(newCustomer);
        return newCustomer;
    }

    @Override
    public List<CustomerResponse> getAllCustomer() {
        List<Customer> result = customerRepository.findAll();
        return result.stream().map(this::mapToResponse).toList();
    }

    @Override
    public CustomerResponse getCustomerById(String id) {
        Customer result = findByIdOrThrowNotFound(id);
        return mapToResponse(result);
    }

    @Override
    public CustomerResponse getCustomerByUserId(String id) {
        UserCredential user = userService.loadByUserId(id);
        Customer result = customerRepository.findCustomerByUserCredential(user);
        return mapToResponse(result);
    }

    private Customer findByIdOrThrowNotFound(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));
    }


    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .build();
    }

}
