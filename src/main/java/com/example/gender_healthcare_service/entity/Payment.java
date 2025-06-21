package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Payments")
public class Payment {
    @Id
    @Column(name = "PaymentID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookingID")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ConsultationID")
    private Consultation consultation;
    @NotNull
    @Column(name = "Amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "PaymentMethod", nullable = false)
    private String paymentMethod;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "PaymentStatus", nullable = false)
    private String paymentStatus;

    @Size(max = 100)
    @Nationalized
    @Column(name = "TransactionID", length = 100)
    private String transactionID;

    @ColumnDefault("getdate()")
    @Column(name = "PaymentDate")
    private Instant paymentDate;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private Instant createdAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted;

}