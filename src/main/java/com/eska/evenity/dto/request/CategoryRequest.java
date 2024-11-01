package com.eska.evenity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryRequest {
    @NotBlank(message = "Name should be filled")
    private String name;
}
