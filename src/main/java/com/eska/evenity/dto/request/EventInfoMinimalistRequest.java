package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventInfoMinimalistRequest {
    private String CategoryId;
    private Long minCost;
    private Long maxCost;
}
