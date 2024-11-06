package com.eska.evenity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "proceeding_event_fee")
@Builder
public class ProceedingEventFee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "token")
    private String token;
    private String orderId;
    @Column(name = "redirect_url")
    private String redirectUrl;
    @Column(name = "transaction_status")
    private String transactionStatus;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    @CreatedDate
    private LocalDateTime createdDate;
}
