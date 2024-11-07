package com.eska.evenity.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminFeeResponse {
    private String adminFeeId;
    private String eventId;
    private String eventName;
    private String customerId;
    private String customerName;
    private String paymentStatus;
    private Long cost;
}
