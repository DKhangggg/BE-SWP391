package com.example.gender_healthcare_service.entity;

import com.example.gender_healthcare_service.entity.enumpackage.FlowIntensity;
import com.example.gender_healthcare_service.entity.enumpackage.MoodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MenstrualLogs")
public class MenstrualLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CycleID", nullable = false)
    private MenstrualCycle menstrualCycle;

    @Column(name = "LogDate", nullable = false)
    private LocalDate logDate;

    @Column(name = "IsActualPeriod")
    private Boolean isActualPeriod = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "FlowIntensity")
    private FlowIntensity flowIntensity;

    @Enumerated(EnumType.STRING)
    @Column(name = "Mood")
    private MoodType mood;

    @Column(name = "Temperature")
    private Double temperature;

    @OneToMany(mappedBy = "menstrualLog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SymptomLog> symptoms;

    @Column(name = "Notes", length = 500)
    private String notes;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}
