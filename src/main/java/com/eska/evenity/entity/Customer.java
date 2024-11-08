package com.eska.evenity.entity;

import java.time.LocalDateTime;

import com.eska.evenity.constant.CustomerStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String fullName;
    @Column(unique = true)
    private String phoneNumber;
    private String province;
    private String city;
    private String district;
    @Column(columnDefinition = "TEXT")
    private String address;
    @Enumerated(EnumType.STRING)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @OneToOne
    @JoinColumn(name = "user_credential_id", referencedColumnName = "id", unique = true)
    private UserCredential userCredential;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}
