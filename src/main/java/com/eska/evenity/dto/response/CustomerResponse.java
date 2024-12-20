package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private String userId;
    private String email;
    private String id;
    private String fullName;
    private String phoneNumber;
    private String province;
    private String city;
    private String district;
    private String address;
    private String status;
    private LocalDateTime modifiedDate;
    private LocalDateTime createdDate;
}
