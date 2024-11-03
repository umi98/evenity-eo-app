package com.eska.evenity.dto.request;

import com.eska.evenity.constant.CategoryType;
import com.eska.evenity.constant.EnumValue;

import jakarta.validation.constraints.NotBlank;
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
public class CategoryRequest {
    @EnumValue(enumClass = CategoryType.class, message = "Main type not exist")
    private String mainType;
    @NotBlank(message = "Name should be filled")
    private String name;
}
