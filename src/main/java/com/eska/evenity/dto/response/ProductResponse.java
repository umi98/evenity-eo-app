package com.eska.evenity.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Long price;
    private Long qty;
    private String productUnit;
    private String categoryId;
    private String categoryName;
    private String vendorId;
    private String vendorName;
    private Boolean isDeleted;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
