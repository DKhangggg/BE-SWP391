package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SampleCollectionProfiles")
public class SampleCollectionProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProfileID", nullable = false)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BookingID", nullable = false)
    private Booking booking;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "CollectorFullName", nullable = false, length = 100)
    private String collectorFullName;

    @Size(max = 20)
    @NotNull
    @Nationalized
    @Column(name = "CollectorIdCard", nullable = false, length = 20)
    private String collectorIdCard;

    @Size(max = 20)
    @Nationalized
    @Column(name = "CollectorPhoneNumber", length = 20)
    private String collectorPhoneNumber;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "RelationshipToBooker", nullable = false, length = 50)
    private String relationshipToBooker; // "SELF", "FAMILY_MEMBER", "FRIEND", "OTHER"

    @Column(name = "CollectorDateOfBirth")
    private LocalDate collectorDateOfBirth;

    @Size(max = 10)
    @Nationalized
    @Column(name = "CollectorGender", length = 10)
    private String collectorGender; // "MALE", "FEMALE", "OTHER"

    @Column(name = "SampleCollectionDate")
    private LocalDateTime sampleCollectionDate;

    @Size(max = 100)
    @Nationalized
    @Column(name = "CollectedBy", length = 100)
    private String collectedBy; // Staff member who collected the sample

    @Size(max = 1000)
    @Nationalized
    @Column(name = "Notes", length = 1000)
    private String notes;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @ColumnDefault("getdate()")
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isSelf() {
        return "SELF".equals(relationshipToBooker);
    }

    public boolean isFamilyMember() {
        return "FAMILY_MEMBER".equals(relationshipToBooker);
    }

    public boolean isCollected() {
        return sampleCollectionDate != null;
    }

    public String getCollectorDisplayName() {
        if (isSelf()) {
            return collectorFullName + " (Chính chủ)";
        }
        return collectorFullName + " (" + getRelationshipDisplayName() + ")";
    }

    public String getRelationshipDisplayName() {
        switch (relationshipToBooker) {
            case "SELF":
                return "Chính chủ";
            case "FAMILY_MEMBER":
                return "Người nhà";
            case "FRIEND":
                return "Bạn bè";
            case "OTHER":
                return "Khác";
            default:
                return relationshipToBooker;
        }
    }

    public String getGenderDisplayName() {
        switch (collectorGender) {
            case "MALE":
                return "Nam";
            case "FEMALE":
                return "Nữ";
            case "OTHER":
                return "Khác";
            default:
                return collectorGender;
        }
    }

    public boolean isMale() {
        return "MALE".equals(collectorGender);
    }

    public boolean isFemale() {
        return "FEMALE".equals(collectorGender);
    }
}
