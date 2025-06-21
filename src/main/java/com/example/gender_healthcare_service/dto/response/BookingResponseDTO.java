package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Integer bookingId;
    private Integer customerId;
    private String customerName;
    private Integer serviceId;
    private String serviceName;
    private LocalDate bookingDate;
    private Integer timeSlotId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String notes;
}

