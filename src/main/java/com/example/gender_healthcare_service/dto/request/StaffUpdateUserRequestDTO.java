package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class StaffUpdateUserRequestDTO {
    @NotBlank(message = "Status is required")
    private String status;
}
