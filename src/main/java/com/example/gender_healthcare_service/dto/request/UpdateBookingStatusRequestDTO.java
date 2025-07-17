package com.example.gender_healthcare_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingStatusRequestDTO {
    private String status;
    private String description;
    private LocalDateTime resultDate;
}

