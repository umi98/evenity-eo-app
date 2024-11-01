package com.eska.evenity.entity;

import com.eska.evenity.constant.ERole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private ERole role;
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}
