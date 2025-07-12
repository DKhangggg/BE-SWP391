package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "SlotNumber", nullable = false)
    private Integer slotNumber;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "Duration", nullable = false)
    private Integer duration; // Duration in minutes

    @Column(name = "Description", length = 100)
    private String description;
    
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Relationship với ConsultantAvailability thay vì ConsultantSchedule trực tiếp
    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsultantAvailability> consultantAvailabilities;

    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsultantSchedule> consultantSchedules;

    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
}
