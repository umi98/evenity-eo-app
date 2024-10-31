package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceResponse {
    private String id;
    private Long amount;
    private String userId;
    private LocalDateTime modifiedDate;
    private LocalDateTime createdDate;
}
