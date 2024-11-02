package com.eska.evenity.entity;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.ProductUnit;
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
@Table(name = "product_recommendation")
public class ProductRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
    private Long quantity;
    @Enumerated(EnumType.STRING)
    private ProductUnit unit;
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}
