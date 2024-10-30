package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorRegisterRequest {
    private String username;
    private String password;
    private String name;
    private String phoneNumber;
    private String address;
    private String ownerName;
}
