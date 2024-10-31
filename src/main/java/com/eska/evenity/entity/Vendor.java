package com.eska.evenity.entity;

import com.eska.evenity.constant.VendorStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "vendor")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Column(unique = true)
    private String phoneNumber;
    private String address;
    private String owner;
    private Integer scoring;
    @Enumerated(EnumType.STRING)
    private VendorStatus status;

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
