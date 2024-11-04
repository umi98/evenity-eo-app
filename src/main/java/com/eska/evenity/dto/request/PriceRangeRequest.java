package com.eska.evenity.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceRangeRequest {
    @NotBlank(message = "Province should be filled")
    private String province;
    @NotBlank(message = "City should be filled")
    private String city;
    @Future
    private LocalDate startDate;
    @Future
    private LocalDate endDate;
    @Positive(message = "Guest number should be positive number")
    private Long participant;
    @NotBlank(message = "Category should be filled")
    private String categoryId;
}
