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
public class BookingFilterRequestDTO {
    private String status;
    private Integer customerId;
    private Integer serviceId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String customerName;
    private String serviceName;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
} 