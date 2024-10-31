package com.eska.evenity.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.eska.evenity.constant.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eska.evenity.dto.request.CustomerRequest;
import com.eska.evenity.dto.response.CustomerResponse;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.repository.CustomerRepository;
import com.eska.evenity.service.CustomerService;
import com.eska.evenity.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Customer createCustomer(Customer customer, UserCredential userCredential) {
        Customer newCustomer = Customer.builder()
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .userCredential(userCredential)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
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
    public List<CustomerResponse> getAllActiveCustomer() {
        List<Customer> result = customerRepository.getCustomerByStatus(UserStatus.ACTIVE);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse editCustomer(String id, CustomerRequest request) {
        try {
            Customer customer = findByIdOrThrowNotFound(id);
            if (customer.getUserCredential().getStatus() != UserStatus.ACTIVE){
                throw new RuntimeException("User status is not active");
            }
            customer.setFullName(request.getFullName());
            customer.setPhoneNumber(request.getPhoneNumber());
            customer.setAddress(request.getAddress());
            customer.setModifiedDate(LocalDateTime.now());
            customerRepository.saveAndFlush(customer);
            return mapToResponse(customer);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteCustomer(String id) {
        try {
            Customer customer = findByIdOrThrowNotFound(id);
            customer.setModifiedDate(LocalDateTime.now());
            customerRepository.saveAndFlush(customer);
            String userCredential = customer.getUserCredential().getId();
            userService.softDeleteById(userCredential);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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
