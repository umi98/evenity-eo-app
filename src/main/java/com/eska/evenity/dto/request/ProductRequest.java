package com.eska.evenity.dto.request;

import com.eska.evenity.constant.ProductUnit;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigInteger price;
    private Long qty;
    private ProductUnit productUnit;
    private String categoryId;
    private String vendorId;
}
