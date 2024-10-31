package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistoryResponse {
    private String id;
    private Long amount;
    private String activity;
    private String description;
    private LocalDateTime transactionDate;
    private String userId;

}
