package com.eska.evenity.dto.request;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.EnumValue;
import com.eska.evenity.constant.VendorStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    @EnumValue(enumClass = CategoryType.class, message = "Main type not exist")
    private String mainType;
    @NotBlank(message = "Name should be filled")
    private String name;
}
