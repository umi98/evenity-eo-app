package com.eska.evenity.dto.request;

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
    private String province;
    private String city;
    private String district;
    @NotBlank(message = "Address should be filled")
    private String address;
    @NotBlank(message = "Owner name should be filled")
    private String ownerName;
}
