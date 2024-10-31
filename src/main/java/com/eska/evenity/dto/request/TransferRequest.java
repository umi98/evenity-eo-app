package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {
    private String senderId;
    private String recipientId;
    private String amount;
}
