package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScheduleID", nullable = false)
    private ConsultantSchedule consultantSchedule;

    @NotNull
    @Column(name = "BookingDate", nullable = false)
    private LocalDate bookingDate;

    @NotNull
    @Column(name = "BookingTime", nullable = false)
    private LocalTime bookingTime;
    
    @NotNull
    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Status", nullable = false)
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED, RESCHEDULED

    @Size(max = 500)
    @Nationalized
    @Column(name = "Result", length = 500)
    private String result;

    @Column(name = "ResultDate")
    private LocalDateTime resultDate;
    
    @Nationalized
    @Column(name = "CustomerNotes", length = 1000)
    private String customerNotes;
    
    @Nationalized
    @Column(name = "ConsultantNotes", length = 1000)
    private String consultantNotes;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
    
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionHistory> transactionHistories;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Tự động set thời gian từ timeSlot nếu chưa được set
        if (bookingTime == null && timeSlot != null) {
            bookingTime = timeSlot.getStartTime();
        }
        if (endTime == null && timeSlot != null) {
            endTime = timeSlot.getEndTime();
        }
        // Tự động set ngày từ consultantSchedule nếu chưa được set
        if (bookingDate == null && consultantSchedule != null) {
            bookingDate = consultantSchedule.getScheduleDate();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    public boolean isConfirmed() {
        return "CONFIRMED".equals(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }
    
    public boolean canBeCancelled() {
        return isPending() || isConfirmed();
    }
    
    public boolean canBeRescheduled() {
        return isPending() || isConfirmed();
    }
    
    public Consultant getConsultant() {
        return consultantSchedule != null ? consultantSchedule.getConsultant() : null;
    }
}