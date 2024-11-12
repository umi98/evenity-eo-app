package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockedProductEventRequest {
    private String vendorId;
    private String vendorName;
    private String vendorAddress;
    private String productId;
    private String productName;
    private String categoryId;
    private String categoryName;
    private String productDescription;
    private Long cost;
}
