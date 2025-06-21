package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantScheduleResponseDTO {
    private Long consultantId;
    private String consultantName;
    private List<AvailableSlotDTO> availableSlots;
    private List<UnavailabilityDTO> unavailabilities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSlotDTO {
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private String status; // e.g., "AVAILABLE", "BOOKED"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnavailabilityDTO {
        private Long unavailabilityId;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private String reason;
    }
}

