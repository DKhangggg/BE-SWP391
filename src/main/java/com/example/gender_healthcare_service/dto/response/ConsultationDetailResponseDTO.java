package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConsultationDetailResponseDTO {
    private Integer id;
    private Integer consultantId;
    private String consultantName;
    private String consultantSpecialty;
    private String consultantImageUrl;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String status;
    private String consultationType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    private String meetingLink;
    private List<String> consultationNotes;

    private LocationResponseDTO location;
    private TimeSlotResponseDTO timeSlot;
}
