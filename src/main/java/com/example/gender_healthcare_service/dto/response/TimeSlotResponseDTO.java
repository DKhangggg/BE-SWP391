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
public class TimeSlotResponseDTO {
    private Integer timeSlotId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer duration;
    private String description;
    private Boolean isActive;
    

    private Integer consultantId;
    private String consultantName;
    private String consultantSpecialization;

    private Integer capacity;
    private Integer bookedCount;
    private Integer availableSlots;

    private String slotType;

    private String displayInfo;
    private Boolean isAvailable;
} 