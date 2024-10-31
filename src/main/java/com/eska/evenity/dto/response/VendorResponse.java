package com.eska.evenity.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorResponse {
    private String id;
    private String name;
    private String phoneNumber;
    private String province;
    private String city;
    private String district;
    private String address;
    private String owner;
    private Integer scoring;
    private String status;
}
