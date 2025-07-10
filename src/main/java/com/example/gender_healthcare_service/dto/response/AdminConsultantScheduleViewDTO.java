package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminConsultantScheduleViewDTO {
    private Integer scheduleId;
    private Integer consultantId;
    private String consultantName;
    private LocalDateTime scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private Integer bookingId;
    private Integer customerId;
    private String customerName;
}
