package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ConsultantSchedules",
       uniqueConstraints = @UniqueConstraint(columnNames = {"ConsultantID", "ScheduleDate", "TimeSlotID"}))
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
    
    // Liên kết với availability template để biết schedule này được tạo từ đâu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AvailabilityID")
    private ConsultantAvailability consultantAvailability;

    @NotNull
    @Column(name = "ScheduleDate", nullable = false)
    private LocalDate scheduleDate;
    
    @NotNull
    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;
    
    @NotNull
    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @NotNull
    @Column(name = "Status", nullable = false)
    private String status; // AVAILABLE, BUSY, UNAVAILABLE, CANCELLED
    
    @Column(name = "CurrentBookings", nullable = false)
    @ColumnDefault("0")
    private Integer currentBookings = 0;
    
    @Column(name = "MaxBookings", nullable = false)
    @ColumnDefault("1")
    private Integer maxBookings = 1;

    @Column(name = "Notes", length = 500)
    private String notes;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
    
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Một schedule có thể có nhiều booking (nếu maxBookings > 1)
    @OneToMany(mappedBy = "consultantSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Sao chép thời gian từ TimeSlot nếu chưa được set
        if (startTime == null && timeSlot != null) {
            startTime = timeSlot.getStartTime();
        }
        if (endTime == null && timeSlot != null) {
            endTime = timeSlot.getEndTime();
        }
        // Sao chép maxBookings từ availability nếu có
        if (maxBookings == 1 && consultantAvailability != null) {
            maxBookings = consultantAvailability.getMaxBookings();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isAvailable() {
        return "AVAILABLE".equals(status) && currentBookings < maxBookings;
    }
    
    public boolean canAcceptBooking() {
        return isAvailable() && !isDeleted;
    }
    
    public void incrementBookings() {
        this.currentBookings++;
        if (this.currentBookings >= this.maxBookings) {
            this.status = "FULLY_BOOKED";
        }
    }
    
    public void decrementBookings() {
        if (this.currentBookings > 0) {
            this.currentBookings--;
            if ("FULLY_BOOKED".equals(this.status) && this.currentBookings < this.maxBookings) {
                this.status = "AVAILABLE";
            }
        }
    }
}
