package com.eska.evenity.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinMaxPriceResponse {
    private Long lowestPrice;
    private Long highestPrice;
}
