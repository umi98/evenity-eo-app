package com.eska.evenity.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetail {
    private EventResponse eventResponse;
    private CustomerResponse customerResponse;
}
