package com.eska.evenity.service.impl;

import com.eska.evenity.constant.VendorStatus;
import com.eska.evenity.dto.response.AdminDashboardSummary;
import com.eska.evenity.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final UserService userService;
    private final CustomerService customerService;
    private final VendorService vendorService;
    private final EventService eventService;
    private final TransactionService transactionService;
    private final InvoiceService invoiceService;

    @Override
    public AdminDashboardSummary getSummary() {
        Long thisMonthEvents = eventService.numOfEventHeldThisMonth();
        HashMap<String, Long> pastFutureEvents = eventService.numOfFuturePastEvents();
        Long grossIncomeMonth = invoiceService.grossIncomeInMonth();
        Long grossIncomeAllTime = invoiceService.grossIncomeAllTime();
        Long totalApprovedWithdrawal = transactionService.getTotalOfApprovedWithdrawalAllTime();
        Long thisMonthApprovedWithdrawal = transactionService.getTotalOfApprovedWithdrawalThisMonth();
        Integer approvedVendor = vendorService.getVendors(VendorStatus.ACTIVE).size();
        Integer pendingVendor = vendorService.getVendors(VendorStatus.PENDING).size();
        Integer inactiveVendor = vendorService.getVendors(VendorStatus.INACTIVE).size();
        Integer customerRegisterThisMonth = customerService.countCustomerRegisterThisMonth();
        Integer vendorRegisterThisMonth = vendorService.countVendorRegisterThisMonth();
        Integer totalVendor = vendorService.getAllVendors().size();
        Integer totalCustomer = customerService.getAllCustomers().size();
        Integer totalUser = userService.getTotalUser();
        Integer userThisMonth = userService.UserRegisterThisMonth();

        return AdminDashboardSummary.builder()
                .totalEvent(thisMonthEvents + pastFutureEvents.get("pastEvents") + pastFutureEvents.get("futureEvents"))
                .eventThisMonth(thisMonthEvents)
                .eventInThePast(pastFutureEvents.get("pastEvents"))
                .eventInTheFuture(pastFutureEvents.get("futureEvents"))
                .grossIncomeThisMonth(grossIncomeMonth)
                .grossIncomeAllTime(grossIncomeAllTime)
                .approvedWithdrawalThisMonth(thisMonthApprovedWithdrawal)
                .approvedWithdrawalAllTime(totalApprovedWithdrawal)
                .revenueThisMonth(grossIncomeMonth - thisMonthApprovedWithdrawal)
                .revenueAllTime(grossIncomeAllTime - totalApprovedWithdrawal)
                .vendorTotal(totalVendor)
                .approvedVendor(approvedVendor)
                .pendingVendor(pendingVendor)
                .rejectedVendor(inactiveVendor)
                .customerTotal(totalCustomer)
                .vendorRegisterThisMonth(vendorRegisterThisMonth)
                .customerRegisterThisMonth(customerRegisterThisMonth)
                .userRegisteredThisMonth(totalUser)
                .registeredUsers(userThisMonth)
                .build();
    }
}
