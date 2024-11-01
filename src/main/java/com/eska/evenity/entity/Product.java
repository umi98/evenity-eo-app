package com.eska.evenity.entity;

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
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Long price;
    private Long qty;
    @Enumerated(EnumType.STRING)
    private ProductUnit productUnit;
    private Boolean isDeleted;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    private Vendor vendor;
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}
