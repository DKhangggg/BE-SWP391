package com.example.gender_healthcare_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private Integer serviceId;
    private LocalDate bookingDate;
    private Integer timeSlotId;
    private String notes;
}

