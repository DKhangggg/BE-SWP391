package com.example.gender_healthcare_service.entity;

import com.example.gender_healthcare_service.entity.enumpackage.SeverityLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SymptomLogs")
public class SymptomLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SymptomLogID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LogID", nullable = false)
    private MenstrualLog menstrualLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SymptomID", nullable = false)
    private Symptom symptom;

    @Enumerated(EnumType.STRING)
    @Column(name = "Severity")
    private SeverityLevel severity;

    @Nationalized
    @Column(name = "Notes")
    private String notes;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

}
