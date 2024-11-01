package com.eska.evenity.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.eska.evenity.constant.ApprovalStatus;

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
@Table(name = "withdraw_request")
public class WithdrawRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Long amount;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    @ManyToOne
    @JoinColumn(name = "balance_id", referencedColumnName = "id")
    private Balance balance;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}
