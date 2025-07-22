package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class AutoCreateTimeSlotsRequestDTO {
    private String slotType; // CONSULTATION
    private Integer capacity;
    private String description;
    private Integer duration; // phút
    private String startDate; // yyyy-MM-dd, optional
    private Integer days; // số ngày, mặc định 7
} 