package com.eska.evenity.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventInfoRequest {
    private String province;
    private String city;
    private String district;
    private Long participant;
    private String categoryId;
    private Long minCost;
    private Long maxCost;
    private List<String> previousProduct;
}
