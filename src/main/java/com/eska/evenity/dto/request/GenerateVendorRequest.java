package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateVendorRequest {
    private String categoryId;
    private Long lowestCost;
    private Long highestCost;
}
