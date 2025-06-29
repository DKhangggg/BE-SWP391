package com.example.gender_healthcare_service.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ConsultantFeedbackDTO {
    private Integer customerId;
    private Integer consultantId;
    private String comment;
    private Integer rating;
}
