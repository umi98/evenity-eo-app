package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceResponse {
    private String invoiceId;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private String eventId;
    private String eventName;
    private Boolean isEventDeleted;
    private Boolean isEventCancelled;
    private String customerId;
    private String customerName;
    private String customerProvince;
    private String customerCity;
    private String customerDistrict;
    private String customerAddress;
    private String phoneNumber;
    private String theme;
    private String province;
    private String city;
    private String district;
    private String address;
    private Long participant;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private LocalDateTime createdDate;
    private Long totalCost;
    private List<InvoiceDetailResponse> invoiceDetailResponseList;
    private AdminFeeResponse adminFeeResponse;
}
