package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDetailResponse {
    private String eventDetailId;
    private String approvalStatus;
    private String eventProgress;
    private Long quantity;
    private String unit;
    private String notes;
    private Long cost;
    private String eventId;
    private String eventName;
    private String productId;
    private String productName;
    private String vendorId;
    private String vendorName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
