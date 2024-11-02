package com.eska.evenity.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRecommendedResponse {
    private String vendorId;
    private String vendorName;
    private String vendorAddress;
    private String productId;
    private String productName;
    private String productDescription;
    private Long cost;
}
