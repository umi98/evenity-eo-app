package com.eska.evenity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoneyOnlyRequest {
    private Long amount;
}
