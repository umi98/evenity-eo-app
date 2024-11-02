package com.eska.evenity.entity;

import java.time.LocalDateTime;

import com.eska.evenity.constant.ApprovalStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.constant.ProductUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "event_detail")
public class EventDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    @Enumerated(EnumType.STRING)
    private EventProgress eventProgress;
    private Long quantity;
    @Enumerated(EnumType.STRING)
    private ProductUnit unit;
    @Column(columnDefinition = "TEXT")
    private String notes;
    private Long cost;
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
