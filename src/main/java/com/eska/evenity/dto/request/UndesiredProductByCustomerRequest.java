package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UndesiredProductByCustomerRequest {
    private String vendorId;
    private Long cost;
}
