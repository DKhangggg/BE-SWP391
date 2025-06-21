package com.example.gender_healthcare_service.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConsultationStatusRequestDTO {
    private String status;
    private String adminNotes;
}

