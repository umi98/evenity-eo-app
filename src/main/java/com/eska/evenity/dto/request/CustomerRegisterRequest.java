package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRegisterRequest {
    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
}
