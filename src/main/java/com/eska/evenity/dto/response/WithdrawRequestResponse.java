package com.eska.evenity.dto.response;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.TransactionType;
import com.eska.evenity.entity.Balance;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequestResponse {
    private String id;
    private Long amount;
    private String approvalStatus;
    private LocalDateTime requestTime;
    private String balanceId;
    private LocalDateTime modifiedDate;
}
