package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TransactionHistory")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Integer transactionID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ServiceID", nullable = false)
    private TestingService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookingID", nullable = false)
    private Booking booking;

    @Column(name = "TransactionDate", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "Status", nullable = false, length = 255)
    private String status;

    @Column(name = "Notes", length = 500)
    private String notes;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt; // DB default GETDATE()

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false; // DB default 0
}

