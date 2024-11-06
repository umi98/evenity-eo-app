package com.eska.evenity.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransNotification {
    private String transaction_status;
    private String order_id;
    private String payment_type;
    private String gross_amount;
    private String signature_key;
}
