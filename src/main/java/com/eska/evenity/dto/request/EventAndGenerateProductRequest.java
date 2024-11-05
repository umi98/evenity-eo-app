package com.eska.evenity.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventAndGenerateProductRequest {
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
    @NotBlank(message = "Province should be filled")
    private String province;
    private String city;
    private String district;
    @NotBlank(message = "Address should be filled")
    private String address;
    @NotBlank(message = "Event theme should be filled")
    private String theme;
    @Positive(message = "Guest number should be positive number")
    private Long participant;
    @NotBlank(message = "Customer id should be filled")
    private String customerId;
    private List<EventInfoMinimalistRequest> categoryProduct;
    private List<String> previousProduct;
}