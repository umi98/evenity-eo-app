package com.eska.evenity.service.impl;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.response.AdminDashboardSummary;
import com.eska.evenity.entity.Customer;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;
import com.eska.evenity.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AdminDashboardServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private CustomerService customerService;
    @Mock
    private VendorService vendorService;
    @Mock
    private EventService eventService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private AdminDashboardServiceImpl adminDashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSummary() {
        when(eventService.numOfEventHeldThisMonth()).thenReturn(10L);

        HashMap<String, Long> pastFutureEvents = new HashMap<>();
        pastFutureEvents.put("pastEvents", 5L);
        pastFutureEvents.put("futureEvents", 3L);
        when(eventService.numOfFuturePastEvents()).thenReturn(pastFutureEvents);

        when(invoiceService.grossIncomeInMonth()).thenReturn(50000L);
        when(invoiceService.grossIncomeAllTime()).thenReturn(1000000L);

        when(transactionService.getTotalOfApprovedWithdrawalAllTime()).thenReturn(30000L);
        when(transactionService.getTotalOfApprovedWithdrawalThisMonth()).thenReturn(5000L);

        Vendor mockVendor1 = Vendor.builder()
                .id("1")
                .name("test 1")
                .phoneNumber("0812345678990")
                .province("East Java")
                .city("Surabaya")
                .district("Bungul")
                .address("Pahlawan St")
                .owner("Mr. X")
                .scoring(50)
                .status(VendorStatus.ACTIVE)
                .mainCategory(CategoryType.CATERING)
                .userCredential(new UserCredential())
                .modifiedDate(LocalDateTime.now())
                .createdDate(LocalDateTime.now())
                .build();
        when(vendorService.getVendors(VendorStatus.ACTIVE)).thenReturn(new ArrayList<>(List.of(mockVendor1)));
        when(vendorService.getVendors(VendorStatus.PENDING)).thenReturn(new ArrayList<>(List.of(mockVendor1)));
        when(vendorService.getVendors(VendorStatus.INACTIVE)).thenReturn(new ArrayList<>(List.of(mockVendor1)));

        when(customerService.countCustomerRegisterThisMonth()).thenReturn(15);
        when(vendorService.countVendorRegisterThisMonth()).thenReturn(8);
        when(vendorService.getAllVendors()).thenReturn(new ArrayList<>(List.of(mockVendor1)));
        when(customerService.getAllCustomers()).thenReturn(new ArrayList<>(List.of(new Customer())));

        when(userService.getTotalUser()).thenReturn(85);
        when(userService.UserRegisterThisMonth()).thenReturn(25);

        // Act: Call the method under test
        AdminDashboardSummary summary = adminDashboardService.getSummary();

        // Assert: Verify the results
        assertEquals(18, summary.getTotalEvent()); // 10 this month + 5 past + 3 future
        assertEquals(10, summary.getEventThisMonth());
        assertEquals(5, summary.getEventInThePast());
        assertEquals(3, summary.getEventInTheFuture());

        assertEquals(50000L, summary.getGrossIncomeThisMonth());
        assertEquals(1000000L, summary.getGrossIncomeAllTime());

        assertEquals(5000L, summary.getApprovedWithdrawalThisMonth());
        assertEquals(30000L, summary.getApprovedWithdrawalAllTime());

        assertEquals(45000L, summary.getRevenueThisMonth());
        assertEquals(970000L, summary.getRevenueAllTime());

        assertEquals(1, summary.getVendorTotal());
        assertEquals(1, summary.getApprovedVendor());
        assertEquals(1, summary.getPendingVendor());
        assertEquals(1, summary.getRejectedVendor());

        assertEquals(1, summary.getCustomerTotal());
        assertEquals(8, summary.getVendorRegisterThisMonth());
        assertEquals(15, summary.getCustomerRegisterThisMonth());
        assertEquals(85, summary.getUserRegisteredThisMonth());
        assertEquals(25, summary.getRegisteredUsers());
    }

}