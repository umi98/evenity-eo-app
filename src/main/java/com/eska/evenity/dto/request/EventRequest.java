package com.eska.evenity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequest {
    @NotBlank(message = "Event name should be filled")
    private String name;
    @NotBlank(message = "Description should be filled")
    private String description;
    @NotBlank(message = "Start date should be filled")
    private LocalDate startDate;
    @NotBlank(message = "Finish date should be filled")
    private LocalDate endDate;
    @NotBlank(message = "Start time should be filled")
    private LocalTime startTime;
    @NotBlank(message = "Finish time should be filled")
    private LocalTime endTime;
    @NotBlank(message = "Address should be filled")
    private String address;
    @NotBlank(message = "Location should be filled")
    private String location;
    @NotBlank(message = "Event theme should be filled")
    private String theme;
    @NotBlank(message = "Guest number should be filled")
    @Positive(message = "Guest number should be positive number")
    private Long guestNumber;
    @NotBlank(message = "Customer id should be filled")
    private String customerId;
}
