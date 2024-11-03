package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventInfoRegenerateRequest {
    private String eventId;
    private Long qty;
    private String unit;
    private String notes;
    private Long cost;
    private String productId;
}
