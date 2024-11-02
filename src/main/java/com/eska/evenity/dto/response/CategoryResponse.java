package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryResponse {
    private String id;
    private String mainCategory;
    private String name;
    private LocalDateTime modifiedDate;
    private LocalDateTime createdDate;
}
