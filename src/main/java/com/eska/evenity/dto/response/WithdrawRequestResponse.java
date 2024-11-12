package com.eska.evenity.dto.response;

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
    private String balanceId;
    private String userName;
    private String vendorId;
    private String vendorName;
    private String imageProofUrl;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
