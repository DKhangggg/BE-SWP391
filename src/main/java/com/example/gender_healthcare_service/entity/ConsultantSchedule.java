package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Consultant consultant; // Changed from User to Consultant

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TimeSlotID", nullable = false)
    private TimeSlot timeSlot;

    @NotNull
    @Column(name = "ScheduleDate", nullable = false)
    private LocalDate scheduleDate;

    @NotNull
    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "Notes", length = 500)
    private String notes;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @OneToOne(mappedBy = "consultantSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Booking booking;
}
