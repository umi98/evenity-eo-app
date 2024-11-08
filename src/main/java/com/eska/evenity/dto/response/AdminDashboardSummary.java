package com.eska.evenity.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardSummary {
    private Long totalEvent;
    private Long eventThisMonth;
    private Long eventInThePast;
    private Long eventInTheFuture;
    private Long grossIncomeThisMonth;
    private Long grossIncomeAllTime;
    private Long approvedWithdrawalThisMonth;
    private Long approvedWithdrawalAllTime;
//    Difference Gross Income And Approved Withdrawal This Month;
    private Long revenueThisMonth;
//    Difference Gross Income And Approved Withdrawal All Time;
    private Long revenueAllTime;
    private Integer vendorTotal;
    private Integer approvedVendor;
    private Integer pendingVendor;
    private Integer rejectedVendor;
    private Integer customerTotal;
    private Integer vendorRegisterThisMonth;
    private Integer customerRegisterThisMonth;
    private Integer userRegisteredThisMonth;
    private Integer registeredUsers;
}
