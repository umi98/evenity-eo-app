package com.eska.evenity.dto.response;

import com.eska.evenity.constant.ProductUnit;
import lombok.*;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductResponse {
    private String productId;
    private String name;
    private String description;
    private BigInteger price;
    private Long qty;
    private ProductUnit productUnit;
    private String categoryName;
    private String vendorName;

}
