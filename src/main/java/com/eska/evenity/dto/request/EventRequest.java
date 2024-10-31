package com.eska.evenity.dto.request;

import jakarta.validation.constraints.*;
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
    @Future(message = "Start date should take place in future")
    private LocalDate startDate;
    @Future(message = "Finish date should take place in future")
    private LocalDate endDate;
    @NotNull(message = "Start time may not be empty")
    private LocalTime startTime;
    @NotNull(message = "Finish time may not be empty")
    private LocalTime endTime;
    @NotBlank(message = "Address should be filled")
    private String address;
    @NotBlank(message = "Location should be filled")
    private String location;
    @NotBlank(message = "Event theme should be filled")
    private String theme;
    @Positive(message = "Guest number should be positive number")
    private Long guestNumber;
    @NotBlank(message = "Customer id should be filled")
    private String customerId;
}
