package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleCollectionResponseDTO {
    
    private Integer profileId;
    private Integer bookingId;
    private String collectorFullName;
    private String collectorIdCard;
    private String collectorPhoneNumber;
    private LocalDate collectorDateOfBirth;
    private String collectorGender;
    private String genderDisplayName;
    private String relationshipToBooker;
    private String relationshipDisplayName;
    private LocalDateTime sampleCollectionDate;
    private String collectedBy;
    private String notes;
    private String doctorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Booking information
    private String customerFullName;
    private String serviceName;
    private String bookingStatus;
    
    // Helper methods
    public boolean isSelf() {
        return "SELF".equals(relationshipToBooker);
    }
    
    public boolean isFamilyMember() {
        return "FAMILY_MEMBER".equals(relationshipToBooker);
    }

    public boolean isMale() {
        return "MALE".equals(collectorGender);
    }

    public boolean isFemale() {
        return "FEMALE".equals(collectorGender);
    }

    public String getCollectorDisplayName() {
        if (isSelf()) {
            return collectorFullName + " (Chính chủ)";
        }
        return collectorFullName + " (" + relationshipDisplayName + ")";
    }

    public boolean isCollected() {
        return sampleCollectionDate != null;
    }
}
