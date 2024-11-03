package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRecommendationResponse {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String province;
    private String city;
    private String district;
    private String address;
    private String theme;
    private Long participant;
    private String customerId;
    private String customerName;
    private List<ProductRecommendedResponse> recommendedList;
}
