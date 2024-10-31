package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponse {
    private String id;
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
    private Boolean isDeleted;
}
