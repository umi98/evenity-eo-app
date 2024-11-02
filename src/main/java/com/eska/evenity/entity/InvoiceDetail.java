package com.eska.evenity.entity;

import com.eska.evenity.constant.PaymentStatus;
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
@Table(name = "invoice_detail")
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @ManyToOne
    @JoinColumn(name = "invoice_id", referencedColumnName = "id")
    private Invoice invoice;
    @OneToOne
    @JoinColumn(name = "event_detail_id", referencedColumnName = "id")
    private EventDetail eventDetail;
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}
