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
public class CustomerRegisterRequest {
    @NotBlank(message = "Username should be filled")
    @Email(message = "Invalid email address")
    private String username;
    @NotBlank(message = "Password should be filled")
    @Size(min = 2, max = 50, message = "Password must be between 2 and 50 characters")
    private String password;
    @NotBlank(message = "Full name should be filled")
    private String fullName;
    @NotBlank(message = "Phone number should be filled")
    @Pattern(
            regexp = "^\\+?[0-9. ()-]{10,20}$",
            message = "Invalid phone number format"
    )
    private String phoneNumber;
    @NotBlank(message = "Address should be filled")
    private String address;
}
