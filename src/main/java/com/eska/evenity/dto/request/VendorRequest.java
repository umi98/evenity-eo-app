package com.eska.evenity.dto.request;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.EnumValue;
import com.eska.evenity.constant.VendorStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
public class VendorRequest {
    @NotBlank(message = "Name should be filled")
    private String name;
    @NotBlank(message = "Phone number should be filled")
    @Pattern(
            regexp = "^\\+?[0-9. ()-]{10,20}$",
            message = "Invalid phone number format"
    )
    private String phoneNumber;
    @NotBlank(message = "Province should be filled")
    private String province;
    @NotBlank(message = "City should be filled")
    private String city;
    @NotBlank(message = "District should be filled")
    private String district;
    @NotBlank(message = "Address should be filled")
    private String address;
    @NotBlank(message = "Owner name should be filled")
    private String ownerName;
    @EnumValue(enumClass = VendorStatus.class, message = "Status must be one of: ACTIVE, INACTIVE, PENDING")
    private String status;
    @Positive(message = "Score number should be positive number")
    private Integer score;
    @EnumValue(enumClass = CategoryType.class, message = "Type must be one of: VENUE, PARKING, " +
            "SECURITY, CATERING, FLOWER_AND_DECORATION, PHOTOGRAPHY_AND_VIDEOGRAPHY, " +
            "TECHNOLOGY_AND_MULTIMEDIA, ENTERTAINER")
    private String mainCategory;
}
