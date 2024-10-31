package com.eska.evenity.entity;

import java.time.LocalDateTime;

import com.eska.evenity.constant.TransactionType;

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
@Table(name = "transaction_history")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Long amount;
    @Enumerated(EnumType.STRING)
    private TransactionType activity;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(updatable = false)
    private LocalDateTime transactionDate;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserCredential userCredential;

}
