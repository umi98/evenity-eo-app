package com.eska.evenity.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private LocalDateTime createdDate;
    private String createdBy;
    private String createdByName;
}
