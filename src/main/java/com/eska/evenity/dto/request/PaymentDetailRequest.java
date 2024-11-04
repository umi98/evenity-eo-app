package com.eska.evenity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDetailRequest {
    @JsonProperty("invoice_id")
    private String invoiceId;
    @JsonProperty("gross_amount")
    private Long amount;
}
