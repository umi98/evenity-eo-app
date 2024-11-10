package com.eska.evenity.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceDetailResponse {
    private String invoiceDetailId;
    private String forwardPaymentStatus;
    private String productId;
    private String productName;
    private String categoryId;
    private String categoryName;
    private String vendorId;
    private String vendorName;
    private Long qty;
    private String unit;
    private Long cost;
}
