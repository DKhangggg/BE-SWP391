package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "AverageCycleLength")
    private Double averageCycleLength;

    @Column(name = "PeriodDuration")
    private Integer periodDuration;

    @Column(name = "AveragePeriodDuration")
    private Double averagePeriodDuration;

    @Column(name = "IsRegular")
    private Boolean isRegular = true;

    @Column(name = "PeriodDay")
    private LocalDate periodDay;

    @Column(name = "NextPredictedPeriod")
    private LocalDate nextPredictedPeriod;

    @Column(name = "FertilityWindowStart")
    private LocalDate fertilityWindowStart;

    @Column(name = "FertilityWindowEnd")
    private LocalDate fertilityWindowEnd;

    @Column(name = "OvulationDate")
    private LocalDate ovulationDate;

    @OneToMany(mappedBy = "menstrualCycle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenstrualLog> menstrualLogs;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
}
