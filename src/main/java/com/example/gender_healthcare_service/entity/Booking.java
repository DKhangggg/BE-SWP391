package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private User customerID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ServiceID", nullable = false)
    private TestingService service;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TimeSlotID", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "BookingDate")
    private LocalDateTime bookingDate;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Status", nullable = false)
    private String status; // PENDING, SAMPLE_COLLECTED, TESTING, COMPLETED, CANCELLED

    @Size(max = 500)
    @Nationalized
    @Column(name = "Result", length = 500)
    private String result;

    @Column(name = "ResultDate")
    private LocalDateTime resultDate;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @ColumnDefault("getdate()")
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Size(max = 1000)
    @Nationalized
    @Column(name = "Description", length = 1000)
    private String description;

    @ColumnDefault("0")
    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionHistory> transactionHistories;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private SampleCollectionProfile sampleCollectionProfile;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (bookingDate == null) {
            bookingDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    public boolean isSampleCollected() {
        return "SAMPLE_COLLECTED".equals(status);
    }
    
    public boolean isTesting() {
        return "TESTING".equals(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    // Helper methods for sample collection
    public boolean hasSampleCollectionProfile() {
        return sampleCollectionProfile != null;
    }

    public boolean isSampleCollectedBySelf() {
        return hasSampleCollectionProfile() && sampleCollectionProfile.isSelf();
    }

    public String getSampleCollectorName() {
        if (hasSampleCollectionProfile()) {
            return sampleCollectionProfile.getCollectorDisplayName();
        }
        return null;
    }
    
    public boolean canBeCancelled() {
        return isPending() || isSampleCollected();
    }
    
    public boolean canBeUpdated() {
        return !isCompleted() && !isCancelled();
    }
}