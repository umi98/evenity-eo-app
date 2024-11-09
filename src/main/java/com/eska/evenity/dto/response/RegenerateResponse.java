package com.eska.evenity.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegenerateResponse {
    private String message;
    private List<String> skippedEventDetails;
    private List<String> proceededEventDetails;
}

