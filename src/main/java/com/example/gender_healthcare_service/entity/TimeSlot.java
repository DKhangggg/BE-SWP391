package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TimeSlots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TimeSlotID")
    private Integer timeSlotID;

    @Column(name = "SlotDate", nullable = false)
    private LocalDate slotDate;

    @Nationalized
    @Column(name = "SlotType", nullable = false)
    private String slotType; // "FACILITY" for testing, "CONSULTATION" for consultation

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "Duration", nullable = false)
    private Integer duration; // Duration in minutesS

    @Nationalized
    @Column(name = "Description", length = 100)
    private String description;
    
    @Column(name = "IsAvailable", nullable = false)
    private Boolean isAvailable = true;

    // Consultant ID - nullable for facility bookings, set for consultation bookings
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ConsultantID")
    private Consultant consultant;

    @Column(name = "Capacity", nullable = false)
    @ColumnDefault("1")
    private Integer capacity = 1; // Default 1 for consultation, can be >1 for facility bookings

    @Column(name = "BookedCount", nullable = false)
    @ColumnDefault("0")
    private Integer bookedCount = 0;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Relationships
    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consultation> consultations;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (duration == null) {
            duration = Math.toIntExact(Duration.between(startTime, endTime).toMinutes());
        }
    }

    // Helper methods
    public boolean isSlotAvailable() {
        return isAvailable && !isDeleted && bookedCount < capacity;
    }

    public boolean canAcceptBooking() {
        return isSlotAvailable();
    }

    public void incrementBookedCount() {
        if (bookedCount < capacity) {
            this.bookedCount++;
        }
    }

    public void decrementBookedCount() {
        if (bookedCount > 0) {
            this.bookedCount--;
        }
    }

    public boolean isFacilitySlot() {
        return "FACILITY".equals(slotType);
    }

    public boolean isConsultationSlot() {
        return "CONSULTATION".equals(slotType);
    }

    public String getDisplayInfo() {
        if (consultant != null) {
            return String.format("%s - %s, Dr. %s (%d/%d booked)", 
                startTime, endTime, consultant.getUser().getFullName(), bookedCount, capacity);
        } else {
            return String.format("%s - %s, Facility (%d/%d booked)", 
                startTime, endTime, bookedCount, capacity);
        }
    }
}
