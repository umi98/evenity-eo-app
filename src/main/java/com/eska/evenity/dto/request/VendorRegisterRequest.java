package com.eska.evenity.dto.request;

import com.eska.evenity.constant.EnumValue;
import com.eska.evenity.constant.ProductUnit;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorRegisterRequest {
    @NotBlank(message = "Email should be filled")
    @Email(message = "Invalid email address")
    private String email;
    @NotBlank(message = "Password should be filled")
    @Size(min = 2, max = 50, message = "Password must be between 2 and 50 characters")
    private String password;
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
    @EnumValue(enumClass = ProductUnit.class, message = "Status must be one of: DAY, PCS, HOUR, GUEST_CAPACITY")
    private String mainCategory;
}
