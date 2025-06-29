package com.example.gender_healthcare_service.entity;

import com.example.gender_healthcare_service.entity.enumpackage.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "consultant_unavailability")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantUnavailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;

    @Column(name = "start_time", nullable = false)
    private LocalDate startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDate endTime;

    private String reason;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "createDate", updatable = false)
    private LocalDate createDate;

    @PrePersist
    protected void onCreate() {
        createDate = LocalDate.now();
    }
}
