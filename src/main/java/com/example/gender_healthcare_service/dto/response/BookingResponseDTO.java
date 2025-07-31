package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Integer bookingId;
    private Integer customerId;
    private String customerFullName;
    private String customerEmailAddress;
    private String customerPhone;
    private Integer serviceId;
    private String serviceName;
    private String serviceDescription;
    private BigDecimal servicePrice;
    private Integer timeSlotId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String slotType;
    private String status;
    private String result;
    private LocalDateTime resultDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime bookingDate;
    private String description;
    private String notes;
    private String displayInfo;

    // Sample collection information
    private SampleCollectionResponseDTO sampleCollectionProfile;
    
    // Helper method để tạo display info
    public String getDisplayInfo() {
        if (displayInfo == null) {
            displayInfo = String.format("%s - %s (%s)",
                customerFullName,
                serviceName,
                status);
        }
        return displayInfo;
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

    public boolean isSampleCollected() {
        return hasSampleCollectionProfile() && sampleCollectionProfile.isCollected();
    }
}

