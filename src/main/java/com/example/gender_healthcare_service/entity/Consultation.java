package com.example.gender_healthcare_service.entity;

import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.List; // Added for Payments

@Getter
@Setter
@Entity
@Table(name = "Consultations")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConsultationID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private User customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ConsultantID", nullable = false)
    private User consultant;

    @NotNull
    @Column(name = "ConsultationDate", nullable = false)
    private Instant consultationDate;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments; // If a consultation can have multiple payments

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private ConsultationStatus status;

    @Size(max = 200)
    @Nationalized
    @Column(name = "MeetingLink", length = 200)
    private String meetingLink;

    @Size(max = 500)
    @Nationalized
    @Column(name = "Notes", length = 500)
    private String notes;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private Instant createdAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

}