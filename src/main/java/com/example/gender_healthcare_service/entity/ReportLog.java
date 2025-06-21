package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ReportLogs")
public class ReportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID")
    private Integer reportID;

    @Column(name = "ReportType", nullable = false, length = 50)
    private String reportType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GeneratedBy", nullable = false)
    private User generatedBy;

    @Column(name = "GeneratedAt")
    private LocalDateTime generatedAt; // DB default GETDATE()

    @Lob
    @Column(name = "ReportData")
    private String reportData;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false; // DB default 0
}

