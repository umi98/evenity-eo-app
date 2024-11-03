package com.eska.evenity.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDetailCustomizedRequest {
    private String province;
    private String city;
    private String categoryId;
    private Long minCost;
    private Long maxCost;
    private Long participant;
    private Long duration;
    private List<String> previousList;
}
