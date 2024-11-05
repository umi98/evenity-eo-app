package com.eska.evenity.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse<T> {
    private String userId;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private T detail;
}
