package com.eska.evenity.dto.request;

import com.eska.evenity.constant.EnumValue;
import com.eska.evenity.constant.ProductUnit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductRequest {
    @NotBlank(message = "Name should be filled")
    private String name;
    @NotBlank(message = "Description should be filled")
    private String description;
    @Positive(message = "Price should have positive value")
    private Long price;
    @Positive(message = "Quantity should have positive value")
    private Long qty;
    @EnumValue(enumClass = ProductUnit.class, message = "Status must be one of: DAY, PCS, HOUR, GUEST_CAPACITY")
    private String productUnit;
    @NotBlank(message = "Category id should be filled")
    private String categoryId;
    @NotBlank(message = "Vendor id should be filled")
    private String vendorId;
}
