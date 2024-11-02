package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDetailRequest {
    private String status;
    private Long qty;
    private String unit;
    private String notes;
    private Long cost;
    private String productId;
    private String eventId;
}
