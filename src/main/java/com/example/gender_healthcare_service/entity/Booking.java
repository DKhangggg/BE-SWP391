package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List; // Added for TransactionHistory

@Getter
@Setter
@Entity
@Table(name = "Bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private User customerID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ServiceID", nullable = false)
    private TestingService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TimeSlotID", nullable = false)
    private TimeSlot timeSlot;

    @NotNull
    @Column(name = "BookingDate", nullable = false)
    private LocalDate bookingDate;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Status", nullable = false)
    private String status;

    @Size(max = 500)
    @Nationalized
    @Column(name = "Result", length = 500)
    private String result;

    @Column(name = "ResultDate")
    private Instant resultDate;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private Instant createdAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionHistory> transactionHistories;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments; // If a booking can have multiple payments (e.g. partial)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScheduleID") // This column will store the FK to ConsultantSchedules table
    private ConsultantSchedule consultantSchedule;

}