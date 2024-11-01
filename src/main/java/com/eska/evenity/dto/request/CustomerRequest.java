package com.eska.evenity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequest {
    @NotBlank(message = "Name should be filled")
    private String fullName;
    @NotBlank(message = "Phone number should be filled")
    @Pattern(
            regexp = "^\\+?[0-9. ()-]{10,20}$",
            message = "Invalid phone number format"
    )
    private String phoneNumber;
    @NotBlank(message = "Address should be filled")
    private String address;
    @NotBlank(message = "Province should be filled")
    private String province;
    @NotBlank(message = "City should be filled")
    private String city;
    @NotBlank(message = "District should be filled")
    private String district;
}
