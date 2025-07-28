package com.example.gender_healthcare_service.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class ConsultationWithFeedbackDTO {
    private Integer id;
    private Instant startTime;
    private Instant endTime;
    private String status;
    private String notes;
    private String meetingLink;
    private String consultationType;
    private Instant createdAt;
    
    // Consultant info
    private Integer consultantId;
    private String consultantName;
    private String consultantSpecialization;
    
    // Feedback info
    private Boolean hasFeedback;
    private Integer feedbackRating;
    private String feedbackComment;
    private Instant feedbackCreatedAt;
} 