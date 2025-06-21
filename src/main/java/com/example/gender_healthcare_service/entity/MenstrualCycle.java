package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MenstrualCycles")
public class MenstrualCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CycleID")
    private Integer cycleID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "CycleLength")
    private Integer cycleLength;

    @Column(name = "Notes", length = 500)
    private String notes;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt; // DB default GETDATE()

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false; // DB default 0
}

