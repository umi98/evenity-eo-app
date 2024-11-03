package com.eska.evenity.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class EventDetailTransactionResponse extends EventDetailResponse{
    private String paymentStatus;
}
