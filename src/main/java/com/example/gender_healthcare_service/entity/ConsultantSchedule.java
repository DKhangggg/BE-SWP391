package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ConsultantSchedules")
public class ConsultantSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScheduleID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ConsultantID", nullable = false)
    private Consultant consultant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TimeSlotID", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "ScheduleDate")
    private LocalDate scheduleDate;

    @NotNull
    @Column(name = "Status", nullable = false)
    private String status; // AVAILABLE, BUSY, UNAVAILABLE, CANCELLED

    @Column(name = "Notes", length = 500)
    private String notes;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Additional fields for schedule management
    @Column(name = "StartTime")
    private LocalTime startTime;

    @Column(name = "EndTime")
    private LocalTime endTime;

    @Column(name = "CurrentBookings")
    private Integer currentBookings = 0;

    @Column(name = "MaxBookings")
    private Integer maxBookings = 1;

    // Relationship with ConsultantAvailability
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AvailabilityID")
    private ConsultantAvailability consultantAvailability;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }
    
    public boolean canAcceptBooking() {
        return isAvailable() && !isDeleted && currentBookings < maxBookings;
    }

    public void incrementBookings() {
        if (currentBookings < maxBookings) {
            this.currentBookings++;
        }
    }

    public void decrementBookings() {
        if (currentBookings > 0) {
            this.currentBookings--;
        }
    }
}
