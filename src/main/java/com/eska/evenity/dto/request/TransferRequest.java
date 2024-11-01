package com.eska.evenity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {
    @NotBlank(message = "Sender id should be filled")
    private String senderId;
    @NotBlank(message = "Recipient id should be filled")
    private String recipientId;
    @Positive(message = "Amount should have positive value")
    private Long amount;
}
